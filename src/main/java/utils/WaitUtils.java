package utils;

import factory.DriverFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class WaitUtils {

    private static final Duration TIMEOUT = Duration.ofSeconds(15);
    private static final Duration POLLING = Duration.ofMillis(500);

    private static FluentWait<WebDriver> getWait() {
        WebDriver driver = DriverFactory.getDriver();
        return new FluentWait<>(driver)
                .withTimeout(TIMEOUT)
                .pollingEvery(POLLING)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
    }

    // ✅ Wait for visibility by locator
    public static WebElement waitForVisibility(By locator) {
        return getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // ✅ Wait for visibility of WebElement
    public static WebElement waitForVisibility(WebElement element) {
        return getWait().until(ExpectedConditions.visibilityOf(element));
    }

    // ✅ Wait for element to be clickable by locator
    public static WebElement waitForClickable(By locator) {
        return getWait().until(ExpectedConditions.elementToBeClickable(locator));
    }

    // ✅ Wait for element to be clickable (WebElement)
    public static WebElement waitForClickable(WebElement element) {
        return getWait().until(ExpectedConditions.elementToBeClickable(element));
    }

    // ✅ Wait for presence by locator

    public static WebElement waitForPresence(By locator) {
        return getWait().until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    // ✅ Wait for invisibility
    public static boolean waitForInvisibility(By locator) {
        try {
            return getWait().until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            return false;
        }
    }

    // ✅ Wait for text to be present in element
    public static boolean waitForText(WebElement element, String text) {
        return getWait().until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    // ✅ Wait for page to load completely
    public static void waitForPageToLoad() {
        WebDriver driver = DriverFactory.getDriver();
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(Exception.class)
                .until(drv -> {
                    try {
                        return ((JavascriptExecutor) drv)
                                .executeScript("return document.readyState").equals("complete");
                    } catch (Exception e) {
                        return false;
                    }
                });
    }

    // ✅ Custom wait with longer timeout
    public static WebElement waitForVisibilityWithLongTimeout(By locator) {
        WebDriver driver = DriverFactory.getDriver();
        return new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(POLLING)
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static void jsClick(WebElement element) {
        WebDriver driver = DriverFactory.getDriver();
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    public static void waitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted during sleep", e);
        }
    }

    public static List<WebElement> waitForVisibilityOfAllElements(By locator) {
        try {
            WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(15));
            return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
        } catch (TimeoutException e) {
            System.out.println("❌ Timeout: Elements not visible for locator: " + locator);
            throw e;
        }
    }

}
