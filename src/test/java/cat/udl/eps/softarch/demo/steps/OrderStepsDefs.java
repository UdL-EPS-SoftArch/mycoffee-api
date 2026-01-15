package cat.udl.eps.softarch.demo.steps;

import cat.udl.eps.softarch.demo.domain.Customer;
import cat.udl.eps.softarch.demo.domain.Order;
import cat.udl.eps.softarch.demo.domain.Product;
import cat.udl.eps.softarch.demo.repository.CustomerRepository;
import cat.udl.eps.softarch.demo.repository.ProductRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.StreamSupport;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrderStepsDefs {

    private final StepDefs stepDefs;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    private String token;
    private String lastOrderId;

    public OrderStepsDefs(StepDefs stepDefs, ProductRepository productRepository, CustomerRepository customerRepository) {
        this.stepDefs = stepDefs;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    // =====================
    // Authentication
    // =====================
    @Given("I am authenticated as {string} with password {string}")
    public void i_am_authenticated_as_with_password(String username, String password) {
        AuthenticationStepDefs.currentUsername = username;
        AuthenticationStepDefs.currentPassword = password;
        token = username + ":" + password;
    }

    @Given("I am not authenticated")
    public void i_am_not_authenticated() {
        AuthenticationStepDefs.currentUsername = null;
        AuthenticationStepDefs.currentPassword = null;
        token = null;
    }

    // =====================
    // Create order (with auth)
    // =====================
    @When("I create an order with:")
    public void i_create_an_order_with(DataTable dataTable) throws Exception {
        Map<String, String> data = dataTable.asMaps().getFirst();
        String productName = data.get("product");

        Product product = productRepository.findByName(productName)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Product not found: " + productName));

        Order order = new Order();
        order.setCreated(ZonedDateTime.now());
        order.setServeWhen(order.getCreated().plusMinutes(10));
        order.setPaymentMethod("Card");
        order.setStatus(Order.Status.SENT);
        order.setProducts(Set.of(product));

        MockHttpServletRequestBuilder builder = post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(stepDefs.mapper.writeValueAsString(order))
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON)
                .with(AuthenticationStepDefs.authenticate());

        stepDefs.result = stepDefs.mockMvc.perform(builder).andDo(print());
        stepDefs.result.andExpect(status().isCreated());

        // Guardar ID generado
        String location = stepDefs.result.andReturn().getResponse().getHeader("Location");
        lastOrderId = location.substring(location.lastIndexOf('/') + 1);
    }

    // =====================
    // Create order (no auth)
    // =====================
    @When("I attempt to create an order with:")
    public void i_attempt_to_create_an_order_with(DataTable dataTable) throws Exception {
        Map<String, String> data = dataTable.asMaps().getFirst();
        String productName = data.get("product");

        Product product = productRepository.findByName(productName)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Product not found: " + productName));

        Order order = new Order();
        order.setCreated(ZonedDateTime.now());
        order.setServeWhen(order.getCreated().plusMinutes(10));
        order.setPaymentMethod("Card");
        order.setStatus(Order.Status.SENT);
        order.setProducts(Set.of(product));

        MockHttpServletRequestBuilder builder = post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(stepDefs.mapper.writeValueAsString(order))
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON);

        stepDefs.result = stepDefs.mockMvc.perform(builder).andDo(print());
    }

    // =====================
    // Precondition: an order exists for the user
    // =====================
    @Given("an order exists for user {string} with password {string}")
    public void an_order_exists_for_user(String username, String password) {
        try {
            AuthenticationStepDefs.currentUsername = username;
            AuthenticationStepDefs.currentPassword = password;

            Product product = StreamSupport.stream(productRepository.findAll().spliterator(), false)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No products exist"));

            Order order = new Order();
            order.setCreated(ZonedDateTime.now());
            order.setServeWhen(order.getCreated().plusMinutes(10));
            order.setPaymentMethod("Card");
            order.setStatus(Order.Status.SENT);
            order.setProducts(Set.of(product));

            MockHttpServletRequestBuilder builder = post("/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(stepDefs.mapper.writeValueAsString(order))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(AuthenticationStepDefs.authenticate());

            stepDefs.result = stepDefs.mockMvc.perform(builder).andDo(print());
            stepDefs.result.andExpect(status().isCreated());

            String location = stepDefs.result.andReturn().getResponse().getHeader("Location");
            lastOrderId = location.substring(location.lastIndexOf('/') + 1);

        } catch (Exception e) {
            fail("Failed to create order for user " + username + ": " + e.getMessage());
        }
    }

    @Given("the following orders exist for user {string}:")
    public void the_following_orders_exist_for_user(String username, DataTable dataTable) {
        AuthenticationStepDefs.currentUsername = username;
        AuthenticationStepDefs.currentPassword = username.equals("admin1") ? "admin123" : "pass123";

        for (var row : dataTable.asMaps()) {
            String productName = row.get("product");

            try {
                Product product = productRepository.findByName(productName)
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Product not found: " + productName));

                Order order = new Order();
                order.setCreated(ZonedDateTime.now());
                order.setServeWhen(order.getCreated().plusMinutes(10));
                order.setPaymentMethod("Card");
                order.setStatus(Order.Status.SENT);
                order.setProducts(Set.of(product));

                MockHttpServletRequestBuilder builder = post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stepDefs.mapper.writeValueAsString(order))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(AuthenticationStepDefs.authenticate());

                stepDefs.result = stepDefs.mockMvc.perform(builder).andDo(print());
                stepDefs.result.andExpect(status().isCreated());

            } catch (Exception e) {
                fail("Failed to create precondition order: " + e.getMessage());
            }
        }
    }

    // =====================
    // Retrieve order
    // =====================
    @When("I retrieve the order with id {string}")
    public void i_retrieve_the_order_with_id_string(String id) throws Exception {
        stepDefs.result = stepDefs.mockMvc.perform(
                get("/orders/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .with(AuthenticationStepDefs.authenticate())).andDo(print());
    }

    @When("I retrieve the last created order")
    public void i_retrieve_the_last_created_order() throws Exception {
        assertNotNull(lastOrderId, "No last order ID stored");
        i_retrieve_the_order_with_id_string(lastOrderId);
    }

    // =====================
    // List orders
    // =====================
    @When("I request the list of orders by {string}")
    public void i_request_my_list_of_orders(String customerName) throws Exception {
        Customer customer = customerRepository.findById(customerName).orElseThrow();

        stepDefs.result = stepDefs.mockMvc.perform(
                        get("/orders/search/findByCustomer?customer={customerUri}", customer.getUri())
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }

    // =====================
    // Assertions
    // =====================
    @Then("The order should exist and include the product {string}")
    public void the_order_should_exist_and_include_product(String productName) throws Exception {
        String newOrderUri = stepDefs.result.andReturn().getResponse().getHeader("Location");
        assertNotNull(newOrderUri, "Order was not created (no Location header)");

        stepDefs.result = stepDefs.mockMvc.perform(
                        get(newOrderUri + "/products")
                                .with(AuthenticationStepDefs.authenticate())
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.products[*].name",
                        hasItem(is(productName))));
    }

    @Then("the response should contain the order details")
    public void the_response_should_contain_the_order_details() throws Exception {
        assertNotNull(stepDefs.result, "No result available");
        stepDefs.result.andExpect(jsonPath("$.status").exists());
        stepDefs.result.andExpect(jsonPath("$.paymentMethod").exists());
        stepDefs.result.andExpect(jsonPath("$.serveWhen").exists());
        stepDefs.result.andExpect(jsonPath("$.created").exists());
    }

    @Then("the response should contain {int} orders")
    public void the_response_should_contain_orders(Integer expectedCount) throws Exception {
        assertNotNull(stepDefs.result, "No result available");
        stepDefs.result.andExpect(jsonPath("$._embedded.orders").isArray());
        stepDefs.result.andExpect(jsonPath("$._embedded.orders.length()").value(expectedCount));
    }
}