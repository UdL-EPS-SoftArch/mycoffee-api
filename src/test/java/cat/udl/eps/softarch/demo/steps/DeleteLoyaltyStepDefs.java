package cat.udl.eps.softarch.demo.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DeleteLoyaltyStepDefs extends StepDefs {

    private Long loyaltyId;
    private MvcResult result;

    @Given("a loyalty program exists with id {long}")
    public void aLoyaltyProgramExistsWithId(Long id) throws Exception {
        this.loyaltyId = id;
    }

    @When("I delete the loyalty program")
    public void iDeleteTheLoyaltyProgram() throws Exception {
        result = mockMvc.perform(delete("/loyalties/" + loyaltyId)
                .with(httpBasic("user", "password")))
                .andReturn();
    }

    @Then("the loyalty program is deleted successfully")
    public void theLoyaltyProgramIsDeletedSuccessfully() throws Exception {
        assert(result.getResponse().getStatus() == 204);
    }

    @Then("the loyalty program deletion fails")
    public void theLoyaltyProgramDeletionFails() throws Exception {
        assert(result.getResponse().getStatus() >= 400);
    }
}