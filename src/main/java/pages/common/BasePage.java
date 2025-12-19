package pages.common;

import factory.DriverFactory;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import utils.WaitUtils;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class BasePage {

    protected WebDriver driver;
    private static final String capitalLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String smallLetters = "abcdefghijklmnopqrstuvwxyz";

    private static final SecureRandom Srandom = new SecureRandom();

    private static final String LETTERS_BASIC = "ابتثجحخدذرزسشصضطظعغفقكلمنهوي";
    private static final Random random = new Random();

    public BasePage() {
        this.driver = DriverFactory.getDriver();
    }

    /*
     * =============================
     * 🧩 Common Actions
     * =============================
     */

    public void typeText(By locator, String text) {
        try {
            WebElement element = WaitUtils.waitForVisibility(locator);

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", element);

            Thread.sleep(500);
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, -150);");
            Thread.sleep(300);

            // Wait for element to be clickable
            WaitUtils.waitForClickable(locator);

            // Click to focus
            element.click();

            // ⚠️ REPLACE element.clear() with JavaScript clear
            ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", element);
            Thread.sleep(200);

            // Send keys
            element.sendKeys(text);

            // Trigger input event for Vue/React
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                    element);

        } catch (Exception e) {
            System.out.println("❌ Error while typing text to element: " + locator);
            throw new RuntimeException(e);
        }
    }

    public void typeText(WebElement field, String text) {
        WaitUtils.waitForVisibility(field);
        field.clear();
        field.sendKeys(text);
    }

    public void clickElement(By locator) {
        WaitUtils.waitForPageToLoad();

        int attempts = 0;
        while (attempts < 3) {
            try {
                WebElement element = WaitUtils.waitForClickable(locator);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
                Thread.sleep(200);

                try {
                    element.click();
                    return;
                } catch (ElementClickInterceptedException e) {
                    System.out.println("⚠ Click intercepted, trying JS click");
                    WaitUtils.jsClick(element);
                    return;
                }

            } catch (StaleElementReferenceException e) {
                System.out.println("⚠ Stale element detected, retrying click... Attempt " + (attempts + 1));
            } catch (Exception e) {
                if (attempts == 2)
                    throw new RuntimeException("❌ Failed to click element: " + locator, e);
            }
            attempts++;
        }
    }

    public void doubleClick(By locator) {
        WebElement element = WaitUtils.waitForClickable(locator);
        Actions actions = new Actions(driver);
        actions.moveToElement(element).doubleClick().perform();
    }

    public void clearField(By locator) {
        WebElement element = WaitUtils.waitForVisibility(locator);
        element.clear();
    }

    public void uploadImageFromDevice(By uploadButtonLocator, String imagePath) {
        try {
            WebElement uploadButton = WaitUtils.waitForClickable(uploadButtonLocator);
            WebElement fileInput = uploadButton.findElement(By.xpath(".//input[@type='file']"));

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].style.display='block'; " +
                            "arguments[0].style.visibility='visible'; " +
                            "arguments[0].style.opacity='1';",
                    fileInput);

            fileInput.sendKeys(imagePath);
            System.out.println("✅ Image uploaded successfully: " + imagePath);

        } catch (NoSuchElementException e) {
            throw new RuntimeException(
                    "❌ File input element not found. Ensure the upload button contains a hidden file input.", e);
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to upload image: " + imagePath, e);
        }
    }

    /*
     * =============================
     * 🔍 Get Text & Validation
     * =============================
     */

    public String getElementText(By locator) {
        try {
            return WaitUtils.waitForVisibility(locator).getText().trim();
        } catch (TimeoutException e) {
            System.out.println("❌ Timeout: Could not find element for getText → " + locator);
            throw e;
        }
    }

    public String getTextFieldValue(By locator) {
        try {
            return WaitUtils.waitForVisibility(locator).getAttribute("value").trim();
        } catch (TimeoutException e) {
            System.out.println("❌ Timeout: Could not find element for getValue → " + locator);
            throw e;
        }
    }

    public boolean isElementDisplayed(By locator) {
        WaitUtils.waitForPageToLoad();
        try {
            return WaitUtils.waitForVisibility(locator).isDisplayed();
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    public boolean isElementClickable(By locator) {
        WaitUtils.waitForPageToLoad();
        try {
            return WaitUtils.waitForClickable(locator).isEnabled();
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    public boolean isRecordPresent(By tableLocator, String expectedText) {
        long endTime = System.currentTimeMillis() + 10000; // 10s timeout
        while (System.currentTimeMillis() < endTime) {
            try {
                WebElement table = driver.findElement(tableLocator);
                if (table.getText().contains(expectedText)) {
                    return true;
                }
            } catch (NoSuchElementException | StaleElementReferenceException ignored) {
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }
        System.out.println("Record not found after 10s: " + expectedText);
        return false;
    }

    public String getErrorMessageBelowField(By inputLocator) {
        WebElement inputField = WaitUtils.waitForPresence(inputLocator);
        WebElement errorElement = inputField.findElement(
                By.xpath("following::p[contains(@class, 'Mui-error')]"));
        return errorElement.getText().trim();
    }

    /*
     * =============================
     * 🎭 Hover & Scroll
     * =============================
     */

    public boolean hoverOverElement(By locator) {
        try {
            WebElement element = WaitUtils.waitForVisibility(locator);
            new Actions(driver).moveToElement(element).perform();
            return true;
        } catch (TimeoutException e) {
            System.out.println("❌ Hover failed — Element not visible: " + locator);
            return false;
        } catch (Exception e) {
            System.out.println("❌ Unexpected error while hovering on: " + locator);
            e.printStackTrace();
            return false;
        }
    }

    public void scrollToElement(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        } catch (Exception e) {
            System.out.println("❌ Could not scroll to element: " + locator);
        }
    }

    /*
     * =============================
     * 🧩 Dropdown & Select
     * =============================
     */

    public List<WebElement> getOptions(By select, By options) {
        clickElement(select);
        return driver.findElements(options);
    }

    /**
     * type = 0 -> select by index
     * type = 1 -> select by visible text
     */
    public void selectFromListByIndex(By select, By optionList, Object value, int type) {
        clickElement(select);
        WaitUtils.waitForPageToLoad();

        try {
            Thread.sleep(700);
        } catch (InterruptedException ignored) {
        }
        List<WebElement> options = driver.findElements(optionList);

        System.out.println("Options count: " + options.size());
        for (int i = 0; i < options.size(); i++) {
            System.out.println("[" + i + "] -> " + options.get(i).getText());
        }

        switch (type) {
            case 0:
                int index = (int) value;
                if (index >= 0 && index < options.size()) {
                    options.get(index).click();
                } else {
                    throw new IllegalArgumentException(
                            "⚠ Invalid index: " + index + ". Options size: " + options.size());
                }
                break;
            case 1:
                String text = value.toString().trim();
                boolean found = false;
                for (WebElement option : options) {
                    String optionText = option.getText().trim();
                    if (optionText.contains(text)) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", option);
                        option.click();
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw new IllegalArgumentException("⚠ Option not found: " + text);
                }
                break;
            default:
                throw new IllegalArgumentException("⚠ Unsupported selection type: " + type);
        }
    }

    public void selectFromList(By locator, String value) {
        List<WebElement> options = driver.findElements(locator)
                .stream()
                .filter(WebElement::isDisplayed)
                .collect(Collectors.toList());

        if (options.isEmpty()) {
            throw new IllegalStateException("⚠ No visible dropdown options found for locator: " + locator);
        }

        boolean found = false;
        StringBuilder available = new StringBuilder();

        for (WebElement option : options) {
            String text = option.getText().trim();
            available.append("\n- ").append(text);
            if (text.contains(value.trim())) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", option);
                option.click();
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException(
                    "⚠ Option not found: '" + value + "'. Available options: " + available);
        }
    }

    /*
     * =============================
     * 📅 Date Handling
     * =============================
     */

    public void selectDateFromCalendar(By calendarInput, String targetDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate date = LocalDate.parse(targetDate, formatter);

        String targetDay = String.valueOf(date.getDayOfMonth());
        String targetMonth = getArabicMonthName(date.getMonthValue());
        int targetYear = date.getYear();

        // 1️⃣ Open calendar
        clickElement(calendarInput);
        WaitUtils.waitForVisibility(By.cssSelector(".ant-picker-dropdown, .ant-picker-panel"));

        By headerView = By.cssSelector(".ant-picker-header-view");
        clickElement(headerView); // months
        clickElement(headerView); // years

        By decadeLabel = By.cssSelector(".ant-picker-header-view");
        while (true) {
            String label = WaitUtils.waitForVisibility(decadeLabel).getText(); // e.g. "2020 - 2029"
            if (!label.contains("-"))
                break;

            String[] parts = label.replace("سنة", "").trim().split("-");
            int startYear = Integer.parseInt(parts[0].trim());
            int endYear = Integer.parseInt(parts[1].trim());

            if (targetYear < startYear) {
                clickElement(By.cssSelector(".ant-picker-super-prev-icon")); // <<
            } else if (targetYear > endYear) {
                clickElement(By.cssSelector(".ant-picker-super-next-icon")); // >>
            } else
                break;
        }

        By yearOption = By.xpath("//div[contains(@class,'ant-picker-year-panel')]//div[text()='" + targetYear + "']");
        clickElement(yearOption);

        By monthOption = By
                .xpath("//div[contains(@class,'ant-picker-month-panel')]//div[contains(text(),'" + targetMonth + "')]");
        clickElement(monthOption);

        By dayOption = By
                .xpath("//td[not(contains(@class,'ant-picker-cell-disabled'))]//div[text()='" + targetDay + "']");
        clickElement(dayOption);

        System.out.println("✅ Date selected: " + targetDate);
    }

    private String getArabicMonthName(int month) {
        switch (month) {
            case 1:
                return "يناير";
            case 2:
                return "فبراير";
            case 3:
                return "مارس";
            case 4:
                return "أبريل";
            case 5:
                return "مايو";
            case 6:
                return "يونيو";
            case 7:
                return "يوليو";
            case 8:
                return "أغسطس";
            case 9:
                return "سبتمبر";
            case 10:
                return "أكتوبر";
            case 11:
                return "نوفمبر";
            case 12:
                return "ديسمبر";
            default:
                throw new IllegalArgumentException("Invalid month: " + month);
        }
    }

    /*
     * =============================
     * 🧭 Navigation & Windows
     * =============================
     */

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public void navigateTo(String url) {
        driver.get(url);
        WaitUtils.waitForPageToLoad();
    }

    public void closePage() {
        driver.close();
    }

    public void closeAllPages() {
        driver.quit();
    }

    public void switchToNewTab() {
        String currentHandle = driver.getWindowHandle();
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(currentHandle)) {
                driver.switchTo().window(handle);
                break;
            }
        }
    }

    /*
     * =============================
     * 🖨 Print PDF Methods
     * =============================
     */

    public void printPDF(By printButtonLocator) {
        printPDF(printButtonLocator, Repetitive.print_user, Repetitive.print_pass, Repetitive.print_signin,
                Repetitive.getUsername(), Repetitive.getPassword(), Repetitive.getLoginUrl());
    }

    private void printPDF(By printButtonLocator,
            By userLocator,
            By passLocator,
            By signinButtonLocator,
            String username,
            String password,
            String loginUrl) {

        Set<String> oldTabs = driver.getWindowHandles();

        clickElement(printButtonLocator);
        WaitUtils.waitForPageToLoad();

        Set<String> newTabs = driver.getWindowHandles();
        newTabs.removeAll(oldTabs);

        String printTab = null;
        if (!newTabs.isEmpty()) {
            printTab = newTabs.iterator().next();
            System.out.println("✅ Print tab detected successfully!");
        } else {
            System.out.println("❌ No new tab detected after clicking Print.");
            return;
        }

        String originalTab = driver.getWindowHandle();

        ((JavascriptExecutor) driver).executeScript("window.open('" + loginUrl + "');");
        WaitUtils.waitForPageToLoad();

        Set<String> afterLoginTabs = driver.getWindowHandles();
        afterLoginTabs.remove(originalTab);
        afterLoginTabs.remove(printTab);

        String loginTab = afterLoginTabs.iterator().next();
        driver.switchTo().window(loginTab);
        System.out.println("✅ Switched to login tab.");

        try {
            WaitUtils.waitForVisibility(userLocator);
            typeText(userLocator, username);
            typeText(passLocator, password);
            clickElement(signinButtonLocator);
            System.out.println("✅ Successfully signed in.");
        } catch (TimeoutException e) {
            System.out.println("❌ Login fields not visible or took too long to load.");
        }

        // driver.switchTo().window(printTab);
        // driver.navigate().refresh();
        System.out.println("✅ Print tab refreshed successfully!");
    }

    public void verifyPDFPrintout(String expectedUrlPart) {
        String actualUrl = driver.getCurrentUrl();

        System.out.println("🔍 Checking printout URL...");
        System.out.println("Expected URL part: " + expectedUrlPart);
        System.out.println("Actual URL: " + actualUrl);

        assertTrue(
                "❌ The actual URL does not contain the expected part!\nExpected part: "
                        + expectedUrlPart + "\nActual URL: " + actualUrl,
                actualUrl.contains(expectedUrlPart));

        Response response = RestAssured
                .given()
                .when()
                .get(actualUrl)
                .then()
                .extract()
                .response();

        int statusCode = response.getStatusCode();
        System.out.println("📊 Response Status Code: " + statusCode);

        assertEquals("❌ Expected status code 200 but got: " + statusCode, 200, statusCode);
        System.out.println("✅ Printout URL and status code verified successfully!");
    }

    public void print(By printButtonLocator) {
        print(printButtonLocator, Repetitive.print_user, Repetitive.print_pass, Repetitive.print_signin,
                Repetitive.getUsername(), Repetitive.getPassword(), Repetitive.getLoginUrl());
    }

    public void print(By printButtonLocator,
            By userLocator,
            By passLocator,
            By signinButtonLocator,
            String username,
            String password,
            String loginUrl) {

        Set<String> oldTabs = driver.getWindowHandles();

        clickElement(printButtonLocator);
        WaitUtils.waitForPageToLoad();

        Set<String> newTabs = driver.getWindowHandles();
        newTabs.removeAll(oldTabs);

        String printTab = null;
        if (!newTabs.isEmpty()) {
            printTab = newTabs.iterator().next();
            System.out.println(" Print tab detected successfully!");
        } else {
            System.out.println(" No new tab detected after clicking Print.");
            return;
        }

        String originalTab = driver.getWindowHandle();

        ((JavascriptExecutor) driver).executeScript("window.open('" + loginUrl + "');");
        WaitUtils.waitForPageToLoad();

        Set<String> afterLoginTabs = driver.getWindowHandles();
        afterLoginTabs.remove(originalTab);
        afterLoginTabs.remove(printTab);

        String loginTab = afterLoginTabs.iterator().next();
        driver.switchTo().window(loginTab);
        System.out.println(" Switched to login tab.");

        try {
            WaitUtils.waitForVisibility(userLocator);
            typeText(userLocator, username);
            typeText(passLocator, password);
            clickElement(signinButtonLocator);
            System.out.println(" Successfully signed in.");
        } catch (TimeoutException e) {
            System.out.println(" Login fields not visible or took too long to load.");
        }

        driver.switchTo().window(printTab);
        driver.navigate().refresh();
        System.out.println(" Print tab refreshed successfully!");
    }

    public void verifyPrintout(String expectedUrlPart) {

        WaitUtils.waitForPageToLoad();
        String actualUrl = driver.getCurrentUrl();

        System.out.println("🔍 Checking printout URL...");
        System.out.println("Expected URL part: " + expectedUrlPart);
        System.out.println("Actual URL: " + actualUrl);

        assertTrue(
                "❌ The actual URL does not contain the expected part!\nExpected part: "
                        + expectedUrlPart + "\nActual URL: " + actualUrl,
                actualUrl.contains(expectedUrlPart));

        Response response = RestAssured
                .given()
                .when()
                .get(actualUrl)
                .then()
                .extract()
                .response();

        int statusCode = response.getStatusCode();
        System.out.println("📊 Response Status Code: " + statusCode);

        assertEquals("❌ Expected status code 200 but got: " + statusCode, 200, statusCode);
        System.out.println("✅ Printout URL and status code verified successfully!");
    }

    public static String generateUniqueId() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static String uniqueID(int length) {
        String id = generateUniqueId();
        return id.substring(id.length() - length);
    }

    public static String generateUniqueIdString(int CSlength) {
        StringBuilder sb = new StringBuilder(CSlength);
        for (int i = 0; i < CSlength; i++) {
            sb.append(capitalLetters.charAt(Srandom.nextInt(capitalLetters.length())));
        }
        return sb.toString();
    }

    public static String generateUniqueSmallString(int SSlength) {
        StringBuilder sb = new StringBuilder(SSlength);
        for (int i = 0; i < SSlength; i++) {
            sb.append(smallLetters.charAt(Srandom.nextInt(smallLetters.length())));
        }
        return sb.toString();
    }

    public static String uniqueIDString(int USlength) {
        return generateUniqueIdString(USlength);
    }

    public static String arabicUniqueId(int length) {
        return generateBasicArabicId(length);
    }

    private static String generateBasicArabicId(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(LETTERS_BASIC.charAt(random.nextInt(LETTERS_BASIC.length())));
        }
        return sb.toString();
    }

    /**
     * ✅ Close all tabs except the one containing your base URL
     */
    public void keepOnlyBaseTab() {
        try {
            String baseUrl = utils.ConfigReader.get("baseUrl");
            String baseHost = baseUrl.replaceAll("https?://", "").replaceAll("/.*", "");

            Set<String> allTabs = driver.getWindowHandles();

            for (String handle : allTabs) {
                driver.switchTo().window(handle);
                String currentUrl = driver.getCurrentUrl();

                if (!currentUrl.contains(baseHost)) {
                    driver.close();
                }
            }

            // Switch to the main (base) tab again
            for (String handle : driver.getWindowHandles()) {
                driver.switchTo().window(handle);
                if (driver.getCurrentUrl().contains(baseHost)) {
                    System.out.println(" Active tab kept: " + driver.getCurrentUrl());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error while cleaning tabs: " + e.getMessage());
        }
    }

    public boolean isRecordDeleted(By tableLocator, String expectedText) {
        try {
            WebElement table = WaitUtils.waitForVisibility(tableLocator);
            String tableText = table.getText();
            boolean isDeleted = !tableText.contains(expectedText);

            if (isDeleted) {
                System.out.println("Record successfully deleted: " + expectedText);
            } else {
                System.out.println("Record still present: " + expectedText);
            }

            return isDeleted;
        } catch (Exception e) {
            System.out.println("Error while checking deleted record in table: " + e.getMessage());
            return false;
        }
    }

    public boolean waitForRecordDeletion(By tableLocator, String recordText) {
        try {
            WaitUtils.waitForPageToLoad();

            long startTime = System.currentTimeMillis();
            long timeout = 15000; // 15 seconds (matches WaitUtils TIMEOUT)

            while (System.currentTimeMillis() - startTime < timeout) {
                try {
                    WebElement table = driver.findElement(tableLocator);
                    String tableText = table.getText();

                    if (!tableText.contains(recordText)) {
                        System.out.println("Record successfully deleted: " + recordText);
                        return true;
                    }

                    Thread.sleep(500); // Poll every 500ms (matches WaitUtils POLLING)

                } catch (NoSuchElementException | StaleElementReferenceException e) {
                    // Table not found or stale - might be refreshing, continue waiting
                    Thread.sleep(500);
                }
            }

            System.out.println("Timeout: Record still presen after 15s: " + recordText);
            return false;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread interrupted while waiting for record deletion");
            return false;
        } catch (Exception e) {
            System.out.println("Error while waiting for record deletion: " + e.getMessage());
            return false;
        }
    }

    public void refreshPage() {
        driver.navigate().refresh();
        WaitUtils.waitForPageToLoad();
    }

    public void ClickAdd_New() {
        WaitUtils.waitForSeconds(2);
        clickElement(Repetitive.New_btn);
    }

    public void Click_save() {
        clickElement(Repetitive.save_btn);
    }

    protected void scrollToTop() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
    }

    protected void scrollUp(int pixels) {
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, -" + pixels + ");");
    }

    public void pressEscKey() {
        new Actions(driver).sendKeys(Keys.ESCAPE).perform();
    }

    public void pressCtrlKey() {
        new Actions(driver).sendKeys(Keys.CONTROL).perform();
    }
}
