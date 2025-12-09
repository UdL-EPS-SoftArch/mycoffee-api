package cat.udl.eps.softarch.demo.steps;

import cat.udl.eps.softarch.demo.domain.Loyalty;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class ReadLoyaltyStepDefs extends StepDefs {

    private Long loyaltyId;
    private MvcResult result;

    @Given("a loyalty program exists with id {long}")
    public void aLoyaltyProgramExistsWithId(Long id) {
        this.loyaltyId = id;
    }

    @When("I retrieve the loyalty program")
    public void iRetrieveTheLoyaltyProgram() throws Exception {
        result = mockMvc.perform(get("/loyalties/" + loyaltyId)
                .with(httpBasic("user", "password")))
                .andReturn();
    }

    @Then("the loyalty program is retrieved successfully")
    public void theLoyaltyProgramIsRetrievedSuccessfully() throws Exception {
        assert(result.getResponse().getStatus() == 200);
    }

    @When("I retrieve all loyalty programs")
    public void iRetrieveAllLoyaltyPrograms() throws Exception {
        result = mockMvc.perform(get("/loyalties")
                .with(httpBasic("user", "password")))
                .andReturn();
    }

    @Then("the loyalty programs list is retrieved successfully")
    public void theLoyaltyProgramsListIsRetrievedSuccessfully() throws Exception {
        assert(result.getResponse().getStatus() == 200);
    }
}