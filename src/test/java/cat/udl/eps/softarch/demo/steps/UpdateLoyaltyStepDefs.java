package cat.udl.eps.softarch.demo.steps;

import cat.udl.eps.softarch.demo.domain.Business;
import cat.udl.eps.softarch.demo.domain.Customer;
import cat.udl.eps.softarch.demo.domain.Loyalty;
import cat.udl.eps.softarch.demo.repository.BusinessRepository;
import cat.udl.eps.softarch.demo.repository.CustomerRepository;
import cat.udl.eps.softarch.demo.repository.LoyaltyRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class UpdateLoyaltyStepDefs {

    @Autowired
    private StepDefs stepDefs;

    @Autowired
    private LoyaltyRepository loyaltyRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BusinessRepository businessRepository;

    private Long loyaltyId;

    @When("I update the loyalty for customer {string} and business {Long} to have {int} points")
    public void iUpdateTheLoyaltyForCustomerAndBusinessToHavePoints(
            String customerUsername, String businessId, Integer newPoints) throws Exception {

        Customer customer = customerRepository.findById(customerUsername).orElseThrow();
        Business business = businessRepository.findById(businessId).orElseThrow();

        List<Loyalty> loyalties = loyaltyRepository.findByCustomerAndBusiness(customer, business);
        assertTrue(!loyalties.isEmpty(), "Loyalty should exist before updating");
        Loyalty loyalty = loyalties.get(0);
        loyaltyId = loyalty.getId();

        JSONObject loyaltyUpdate = new JSONObject();
        loyaltyUpdate.put("accumulatedPoints", newPoints);

        stepDefs.result = stepDefs.mockMvc.perform(
                        patch("/loyalties/{id}", loyaltyId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loyaltyUpdate.toString())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }

    @When("I update the loyalty with id {long} to have {int} points")
    public void iUpdateTheLoyaltyWithIdToHavePoints(Long id, Integer newPoints) throws Exception {
        JSONObject loyaltyUpdate = new JSONObject();
        loyaltyUpdate.put("accumulatedPoints", newPoints);

        stepDefs.result = stepDefs.mockMvc.perform(
                        patch("/loyalties/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loyaltyUpdate.toString())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }

    @And("The loyalty has {int} accumulated points")
    public void theLoyaltyHasAccumulatedPoints(Integer expectedPoints) throws Exception {
        // Verificamos que el último resultado contenga los puntos esperados
        stepDefs.result.andExpect(jsonPath("$.accumulatedPoints", is(expectedPoints)));
    }

    @Given("There is no loyalty with id {long}")
    public void thereIsNoLoyaltyWithId(Long id) {
        assertFalse(loyaltyRepository.existsById(id),
                "Loyalty with id \"" + id + "\" shouldn't exist");
    }
}
