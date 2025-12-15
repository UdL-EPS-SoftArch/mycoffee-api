package cat.udl.eps.softarch.demo.steps;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import cat.udl.eps.softarch.demo.domain.Customer;
import cat.udl.eps.softarch.demo.repository.CustomerRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

public class UpdateCustomerStepDefs {

    @Autowired
    private StepDefs stepDefs;
    @Autowired
    private CustomerRepository customerRepository;

    @When("I update the customer {string} with name {string} and phoneNumber {string}")
    public void iUpdateTheCustomerWithNameAndPhoneNumber(
            String username, String name, String phoneNumber) throws Exception {
        JSONObject customer = new JSONObject();
        if (!name.isEmpty()) {
            customer.put("name", name);
        }
        if (!phoneNumber.isEmpty()) {
            customer.put("phoneNumber", phoneNumber);
        } else {
            // ✅ Si és buit, enviar-lo explícitament per forçar validació
            customer.put("phoneNumber", "");
        }

        stepDefs.result = stepDefs.mockMvc.perform(
                        patch("/customers/{username}", username)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(customer.toString())
                                .accept(MediaType.APPLICATION_JSON)
                                .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }

    @When("I update the customer {string} with email {string}")
    public void iUpdateTheCustomerWithEmail(String username, String email) throws Exception {
        JSONObject customer = new JSONObject();
        if (!email.isEmpty()) {
            customer.put("email", email);
        } else {
            // ✅ Si és buit, enviar-lo explícitament per forçar validació
            customer.put("email", "");
        }

        stepDefs.result = stepDefs.mockMvc.perform(
                        patch("/customers/{username}", username)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(customer.toString())
                                .accept(MediaType.APPLICATION_JSON)
                                .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }

    @When("I update the customer {string} with phoneNumber {string}")
    public void iUpdateTheCustomerWithPhoneNumber(String username, String phoneNumber) throws Exception {
        JSONObject customer = new JSONObject();
        if (!phoneNumber.isEmpty()) {
            customer.put("phoneNumber", phoneNumber);
        } else {
            // ✅ Si és buit, enviar-lo explícitament per forçar validació
            customer.put("phoneNumber", "");
        }

        stepDefs.result = stepDefs.mockMvc.perform(
                        patch("/customers/{username}", username)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(customer.toString())
                                .accept(MediaType.APPLICATION_JSON)
                                .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }

    @And("The customer {string} has name {string} and phoneNumber {string}")
    public void theCustomerHasNameAndPhoneNumber(
            String username, String name, String phoneNumber) throws Exception {
        stepDefs.result.andExpect(jsonPath("$.name", is(name)))
                .andExpect(jsonPath("$.phoneNumber", is(phoneNumber)));
    }

    @And("The customer {string} has email {string}")
    public void theCustomerHasEmail(String username, String email) throws Exception {
        stepDefs.result.andExpect(jsonPath("$.email", is(email)));
    }
}

