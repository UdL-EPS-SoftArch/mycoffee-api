package cat.udl.eps.softarch.demo.steps;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cat.udl.eps.softarch.demo.repository.CustomerRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

public class DeleteCustomerStepDefs {

    @Autowired
    private StepDefs stepDefs;

    @Autowired
    private CustomerRepository customerRepository;

    @When("I delete the customer {string}")
    public void iDeleteTheCustomer(String username) throws Exception {
        stepDefs.result = stepDefs.mockMvc.perform(
                        delete("/customers/{username}", username)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }

    @And("The customer {string} does not exist")
    public void theCustomerDoesNotExist(String username) throws Exception {
        stepDefs.mockMvc.perform(
                        get("/customers/{username}", username)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(AuthenticationStepDefs.authenticate()))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}