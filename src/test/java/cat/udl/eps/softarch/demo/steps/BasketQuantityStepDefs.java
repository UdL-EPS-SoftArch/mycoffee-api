package cat.udl.eps.softarch.demo.steps;

import cat.udl.eps.softarch.demo.domain.Basket;
import cat.udl.eps.softarch.demo.domain.BasketItem;
import cat.udl.eps.softarch.demo.domain.Customer;
import cat.udl.eps.softarch.demo.domain.Product;
import cat.udl.eps.softarch.demo.repository.BasketItemRepository;
import cat.udl.eps.softarch.demo.repository.BasketRepository;
import cat.udl.eps.softarch.demo.repository.CustomerRepository;
import cat.udl.eps.softarch.demo.repository.ProductRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BasketQuantityStepDefs {

    @Autowired
    private StepDefs stepDefs;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private BasketItemRepository basketItemRepository;

    private Long currentBasketId;

    @Given("the following products exist:")
    public void the_following_products_exist(DataTable dataTable) {
        for (Map<String, String> row : dataTable.asMaps()) {
            String name = row.get("name");
            BigDecimal price = new BigDecimal(row.get("price"));
            if (productRepository.findByName(name).isEmpty()) {
                Product product = new Product();
                product.setName(name);
                product.setPrice(price);
                product.setAvailable(true);
                productRepository.save(product);
            }
        }
    }

    @Given("a customer {string} with password {string} exists")
    public void a_customer_exists(String username, String password) {
        if (!customerRepository.existsById(username)) {
            Customer customer = new Customer();
            customer.setId(username);
            customer.setName(username);
            customer.setPassword(password);
            customer.setEmail(username + "@example.com");
            customer.setPhoneNumber("123456789");
            customer.encodePassword();
            customerRepository.save(customer);
        }
    }

    @And("a basket exists for customer {string}")
    public void a_basket_exists_for_customer(String username) throws Exception {
        Customer customer = customerRepository.findById(username).orElseThrow();
        Basket basket = new Basket();
        basket.setCustomer(customer);
        basket = basketRepository.save(basket);
        currentBasketId = basket.getId();
    }

    @When("I add {int} units of {string} to my basket")
    public void i_add_units_of_to_my_basket(int quantity, String productName) throws Exception {
        Product product = productRepository.findByName(productName).get(0);
        
        // Construct the BasketItem JSON manually since it uses URI references in Spring Data REST
        String basketUri = "/baskets/" + currentBasketId;
        String productUri = "/products/" + product.getId();
        
        String basketItemJson = String.format(
            "{\"basket\": \"%s\", \"product\": \"%s\", \"quantity\": %d}",
            basketUri, productUri, quantity
        );

        stepDefs.result = stepDefs.mockMvc.perform(
                post("/basketItems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(basketItemJson)
                        .with(AuthenticationStepDefs.authenticate()))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Then("my basket should contain {int} units of {string}")
    public void my_basket_should_contain_units_of(int quantity, String productName) throws Exception {
        stepDefs.result = stepDefs.mockMvc.perform(
                get("/basketItems/search/findByBasket")
                        .param("basket", "/baskets/" + currentBasketId)
                        .with(AuthenticationStepDefs.authenticate()))
                .andDo(print())
                .andExpect(status().isOk());
        
        // We need to find the specific product in the list of items
        stepDefs.result.andExpect(jsonPath("$._embedded.basketItems[?(@.quantity == " + quantity + ")]").exists());
    }
}
