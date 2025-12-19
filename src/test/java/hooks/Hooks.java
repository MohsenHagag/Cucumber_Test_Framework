package hooks;

import factory.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
// import pages.login.LoginPage; // Removed: Project specific
import utils.ConfigReader;
import utils.WaitUtils;

import java.io.ByteArrayInputStream;

public class Hooks {

    private WebDriver driver;

    @Before
    public void setUp(Scenario scenario) {
        System.out.println("🚀 Starting scenario: " + scenario.getName());
        driver = DriverFactory.getDriver();

        boolean autoLogin = Boolean.parseBoolean(ConfigReader.get("autoLogin", "false"));
        if (autoLogin) {
            performAutoLogin(scenario);
        }
    }

    @After
    public void tearDown(Scenario scenario) {
        if (driver != null) {
            try {
                if (scenario.isFailed()) {
                    // 📸 Take screenshot on failure (attach to Cucumber + Allure)
                    byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                    scenario.attach(screenshot, "image/png", "Failed Scenario Screenshot");
                    Allure.addAttachment("Failed Screenshot", new ByteArrayInputStream(screenshot));
                    System.out.println("🧩 Screenshot attached to report for failed scenario: " + scenario.getName());
                }

                System.out
                        .println("🧹 Scenario finished: " + scenario.getName() + " - Status: " + scenario.getStatus());

            } catch (Exception e) {
                System.err.println("⚠ Error in tearDown: " + e.getMessage());
            } finally {
                DriverFactory.quitDriver();
            }
        }
    }

    private void performAutoLogin(Scenario scenario) {
        /*
         * 📝 TODO: Implement generic auto-login logic if needed.
         * The original implementation depended on specific 'LoginPage' and 'BasePage'
         * methods.
         */
        System.out.println("ℹ Auto-login requested but not implemented in this skeleton framework.");

        /*
         * Original Logic Placeholder:
         * try {
         * String baseUrl = ConfigReader.get("baseUrl");
         * String username = ConfigReader.get("username");
         * String password = ConfigReader.get("password");
         * 
         * // Perform login actions...
         * 
         * } catch (Exception e) {
         * System.err.println("⚠ Auto-login failed: " + e.getMessage());
         * e.printStackTrace();
         * }
         */
    }
}
