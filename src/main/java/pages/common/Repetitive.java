package pages.common;

import factory.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class Repetitive extends BasePage {

    private WebDriver driver;

    public Repetitive() {
        this.driver = DriverFactory.getDriver();
        PageFactory.initElements(driver, this);
    }

    // Placeholders - Replace with actual locators for your new project
    public static final By New_btn = By.id("placeholder_new_btn");
    public static final By searchBar = By.id("placeholder_search_bar");
    public static final By searchIcon = By.id("placeholder_search_icon");
    public static final By save_btn = By.id("placeholder_save_btn");
    public static final By cancel_btn = By.id("placeholder_cancel_btn");
    public static final By system_card = By.id("placeholder_system_card");
    public static final By Yes_btn = By.id("placeholder_yes_btn");
    public static final By No_btn = By.id("placeholder_no_btn");

    public static final By firstItemOptionsButton = By.id("placeholder_item_options");
    public static final By firstItemViewButton = By.id("placeholder_item_view");
    public static final By firstItemEditButton = By.id("placeholder_item_edit");
    public static final By firstItemDeleteButton = By.id("placeholder_item_delete");
    public static final By firstItemPrintButton = By.id("placeholder_item_print");
    public static final By firstItemPrintPDFButton = By.id("placeholder_item_print_pdf");

    public static final By Print_options_dropdown = By.id("placeholder_print_dropdown");
    public static final By print_record = By.id("placeholder_print_record");
    public static final By print_record_pdf = By.id("placeholder_print_record_pdf");

    public static final By List = By.tagName("li");

    /*
     * 🔐 Common Auth / Print Credentials
     * Removed sensitive company data.
     */
    public static final By print_user = By.id("placeholder_user_field");
    public static final By print_pass = By.id("placeholder_pass_field");
    public static final By print_signin = By.id("placeholder_signin_btn");

    private static final String USERNAME = "";
    private static final String PASSWORD = "";
    static final String LOGIN_URL = "";

    public static String getUsername() {
        return USERNAME;
    }

    public static String getPassword() {
        return PASSWORD;
    }

    public static String getLoginUrl() {
        return LOGIN_URL;
    }

}
