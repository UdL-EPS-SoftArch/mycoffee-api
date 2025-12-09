package cat.udl.eps.softarch.demo.steps;

import cat.udl.eps.softarch.demo.domain.Loyalty;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class UpdateLoyaltyStepDefs extends StepDefs {

    private Long loyaltyId;
    private Loyalty loyalty;
    private MvcResult result;

    @Given("a loyalty program exists with {int} points")
    public void aLoyaltyProgramExistsWithPoints(Integer points) throws Exception {
        this.loyalty = new Loyalty();
        this.loyalty.setAccumulatedPoints(points);
        this.loyaltyId = 1L;
    }

    @When("I update the loyalty program to {int} points")
    public void iUpdateTheLoyaltyProgramToPoints(Integer points) throws Exception {
        loyalty.setAccumulatedPoints(points);
        
        String loyaltyJson = objectMapper.writeValueAsString(loyalty);
        
        result = mockMvc.perform(patch("/loyalties/" + loyaltyId)
                .contentType("application/json")
                .content(loyaltyJson)
                .with(httpBasic("user", "password")))
                .andReturn();
    }

    @Then("the loyalty program has been updated to {int} points")
    public void theLoyaltyProgramHasBeenUpdatedToPoints(Integer points) throws Exception {
        Loyalty updatedLoyalty = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                Loyalty.class
        );
        assert(updatedLoyalty.getAccumulatedPoints().equals(points));
    }
}