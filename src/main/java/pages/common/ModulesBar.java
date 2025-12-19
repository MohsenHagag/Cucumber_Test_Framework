package pages.common;
import org.openqa.selenium.By;
import utils.WaitUtils;

public class ModulesBar extends BasePage{

    /*
    Locators
     */

    private final By testModule = By.id("testModule");

    /*
    Methods
     */

    public void openSalesModule(){
        WaitUtils.waitForClickable(testModule);
        clickElement(testModule);
    }

}
