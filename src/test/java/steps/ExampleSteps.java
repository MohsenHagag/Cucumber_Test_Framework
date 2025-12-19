package steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;

public class ExampleSteps {

    @Given("I have navigated to the application page")
    public void iHaveNavigatedToTheApplicationPage() {
        // Code to navigate to the page
        System.out.println("Navigated to the application page.");
    }

    @When("I perform a generic action")
    public void iPerformAGenericAction() {
        // Code to perform an action
        System.out.println("Performed a generic action.");
    }

    @And("I enter {string} into the {string}")
    public void iEnterIntoThe(String data, String field) {
        // Code to enter data into a specific field
        System.out.println("Entered '" + data + "' into field '" + field + "'.");
    }

    @Then("I should see a success message")
    public void iShouldSeeASuccessMessage() {
        // Code to verify success message
        System.out.println("Verified success message.");
    }

    @And("the result should be {string}")
    public void theResultShouldBe(String expectedResult) {
        // Code to verify the result
        System.out.println("Verified result is '" + expectedResult + "'.");
    }
}
