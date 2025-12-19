package factory;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;
import utils.ConfigReader;

public class DriverFactory {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static WebDriver getDriver() {
        if (driver.get() == null) {
            String browser = ConfigReader.get("browser", "chrome");

            WebDriver instance;
            switch (browser.toLowerCase()) {
                case "firefox":
                    instance = new FirefoxDriver();
                    break;
                case "edge":
                    instance = new EdgeDriver();
                    break;
                case "safari":
                    instance = new SafariDriver();
                    break;
                default:
                    instance = new ChromeDriver();
            }

            instance.manage().window().maximize();
            driver.set(instance);
        }
        return driver.get();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}
