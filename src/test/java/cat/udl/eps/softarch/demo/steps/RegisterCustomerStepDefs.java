package cat.udl.eps.softarch.demo.steps;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import java.nio.charset.StandardCharsets;

public class RegisterCustomerStepDefs {

    @Autowired
    private StepDefs stepDefs;

    @Autowired
    private CustomerRepository customerRepository;

    @Given("There is no registered customer with username {string}")
    public void thereIsNoRegisteredCustomerWithUsername(String username) {
        assertFalse(customerRepository.existsById(username),
                "Customer \"" + username + "\" shouldn't exist");
    }

    @Given("There is a registered customer with username {string} and password {string} and email {string} and phoneNumber {string}")
    public void thereIsARegisteredCustomerWithUsernameAndPasswordAndEmailAndPhoneNumber(
            String username, String password, String email, String phoneNumber) {
        if (!customerRepository.existsById(username)) {
            Customer customer = new Customer();
            customer.setId(username);
            customer.setEmail(email);
            customer.setPhoneNumber(phoneNumber);
            customer.setName(username);
            customer.setPassword(password);
            customer.encodePassword();
            customerRepository.save(customer);
        }
    }

    @When("I register a new customer with username {string} and password {string} and email {string} and phoneNumber {string}")
    public void iRegisterANewCustomerWithUsernameAndPasswordAndEmailAndPhoneNumber(
            String username, String password, String email, String phoneNumber) throws Exception {
        Customer customer = new Customer();
        customer.setId(username);
        customer.setEmail(email);
        customer.setPhoneNumber(phoneNumber);
        customer.setName(username);

        stepDefs.result = stepDefs.mockMvc.perform(
                        post("/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new JSONObject(
                                        stepDefs.mapper.writeValueAsString(customer)
                                ).put("password", password).toString())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }

    @And("It has been created a customer with username {string} and email {string} and phoneNumber {string}")
    public void itHasBeenCreatedACustomerWithUsernameAndEmailAndPhoneNumber(
            String username, String email, String phoneNumber) throws Exception {
        stepDefs.result = stepDefs.mockMvc.perform(
                        get("/customers/{username}", username)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(AuthenticationStepDefs.authenticate()))
                .andDo(print())
                .andExpect(jsonPath("$.name", is(username)))
                .andExpect(jsonPath("$.email", is(email)))
                .andExpect(jsonPath("$.phoneNumber", is(phoneNumber)))
                .andExpect(jsonPath("$.password").doesNotExist());
    }
}
