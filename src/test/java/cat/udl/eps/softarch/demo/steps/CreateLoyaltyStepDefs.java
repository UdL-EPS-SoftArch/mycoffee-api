package cat.udl.eps.softarch.demo.steps;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import cat.udl.eps.softarch.demo.domain.Loyalty;
import cat.udl.eps.softarch.demo.domain.Customer;
import cat.udl.eps.softarch.demo.domain.Business;
import cat.udl.eps.softarch.demo.repository.LoyaltyRepository;
import cat.udl.eps.softarch.demo.repository.CustomerRepository;
import cat.udl.eps.softarch.demo.repository.BusinessRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import jakarta.validation.constraints.NotEmpty;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.List;

public class CreateLoyaltyStepDefs {

    @Autowired
    private StepDefs stepDefs;

    @Autowired
    private LoyaltyRepository loyaltyRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BusinessRepository businessRepository;

    private Long createdLoyaltyId;

    @Given("There is no loyalty with id {long}")
    public void thereIsNoLoyaltyWithId(Long id) {
        assertFalse(loyaltyRepository.existsById(id),
                "Loyalty with id \"" + id + "\" shouldn't exist");
    }

    @Given("There is a registered customer with username {string} and password {string} and email {string}")
    public void thereIsARegisteredCustomerWithUsernameAndPasswordAndEmail(
            String username, String password, String email) {
        if (!customerRepository.existsById(username)) {
            Customer customer = new Customer();
            customer.setId(username);
            customer.setEmail(email);
            customer.setPhoneNumber("123456789");
            customer.setName(username);
            customer.setPassword(password);
            customer.encodePassword();
            customerRepository.save(customer);
        }
    }

    @Given("There is a registered business with id {long} and name {string} and address {string}")
    public void thereIsARegisteredBusinessWithIdAndNameAndAddress(Long id, String name, String address) {
        if (!businessRepository.existsById(id)) {
            Business business = new Business();
            business.setId(String.valueOf(id));
            business.setName(name);
            business.setAddress(address);
            business.setEmail("business" + id + "@example.com");
            business.setPassword("password");
            business.encodePassword();
            businessRepository.save(business);
        }
    }

    @Given("There is a loyalty for customer {string} and business {long} with {int} points")
    public void thereIsALoyaltyForCustomerAndBusinessWithPoints(
            String customerUsername, Long businessId, Integer points) {
        Customer customer = customerRepository.findById(customerUsername).orElseThrow();
        Business business = businessRepository.findById(businessId).orElseThrow();

        List<Loyalty> existing = loyaltyRepository.findByCustomerAndBusiness(customer, business);
        if (existing.isEmpty()) {
            Loyalty loyalty = new Loyalty();
            loyalty.setCustomer(customer);
            loyalty.setBusiness(business);
            loyalty.setStartDate(ZonedDateTime.now());
            loyalty.setAccumulatedPoints(points);
            loyaltyRepository.save(loyalty);
        }
    }

    @When("I create a loyalty for customer {string} and business {long} with {int} points")
    public void iCreateALoyaltyForCustomerAndBusinessWithPoints(
            String customerUsername, Long businessId, Integer points) throws Exception {
        Customer customer = customerRepository.findById(customerUsername).orElseThrow();
        Business business = businessRepository.findById(businessId).orElseThrow();

        JSONObject loyalty = new JSONObject();
        loyalty.put("startDate", ZonedDateTime.now().toString());
        loyalty.put("accumulatedPoints", points);
        loyalty.put("customer", "/customers/" + customerUsername);
        loyalty.put("business", "/businesses/" + businessId);

        stepDefs.result = stepDefs.mockMvc.perform(
                        post("/loyalties")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loyalty.toString())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }

    @When("I retrieve the loyalty with id {long}")
    public void iRetrieveTheLoyaltyWithId(Long id) throws Exception {
        stepDefs.result = stepDefs.mockMvc.perform(
                        get("/loyalties/{id}", id)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }



    @And("It has been created a loyalty for customer {string} and business {long} with {int} points")
    public void itHasBeenCreatedALoyaltyForCustomerAndBusinessWithPoints(
            String customerUsername, Long businessId, Integer points) throws Exception {
        Customer customer = customerRepository.findById(customerUsername).orElseThrow();
        Business business = businessRepository.findById(businessId).orElseThrow();

        List<Loyalty> loyalties = loyaltyRepository.findByCustomerAndBusiness(customer, business);
        assertTrue(!loyalties.isEmpty(), "Loyalty should exist");

        Loyalty loyalty = loyalties.get(0);
        createdLoyaltyId = loyalty.getId();

        stepDefs.result = stepDefs.mockMvc.perform(
                        get("/loyalties/{id}", loyalty.getId())
                                .accept(MediaType.APPLICATION_JSON)
                                .with(AuthenticationStepDefs.authenticate()))
                .andDo(print())
                .andExpect(jsonPath("$.accumulatedPoints", is(points)));
    }

    @And("The loyalty has {int} accumulated points")
    public void theLoyaltyHasAccumulatedPoints(Integer points) throws Exception {
        stepDefs.result.andExpect(jsonPath("$.accumulatedPoints", is(points)));
    }

    @And("The loyalty does not exist with id {long}")
    public void theLoyaltyDoesNotExistWithId(Long id) {
        assertFalse(loyaltyRepository.existsById(id),
                "Loyalty with id \"" + id + "\" should not exist");
    }
}