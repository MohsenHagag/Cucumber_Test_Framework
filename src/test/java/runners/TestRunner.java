package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(features = "src/test/resources/features", glue = { "steps", "hooks" }, plugin = {
        "pretty",
        "html:target/cucumber-reports/cucumber.html",
        "json:target/cucumber-reports/cucumber.json",
        "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
}, monochrome = true, dryRun = false)

public class TestRunner extends AbstractTestNGCucumberTests {
    // This class will be empty - TestNG + Cucumber will handle execution
}
