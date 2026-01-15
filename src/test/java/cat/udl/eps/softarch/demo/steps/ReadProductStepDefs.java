package cat.udl.eps.softarch.demo.steps;

import cat.udl.eps.softarch.demo.domain.Product;
import cat.udl.eps.softarch.demo.repository.ProductRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReadProductStepDefs {

    @Autowired
    private StepDefs stepDefs;

    @Autowired
    private ProductRepository productRepository;

    public static Product currentProduct;

    @Before
    public void setup() {
        productRepository.deleteAll();
    }

    // ========== GIVEN/AND - Preparar datos ==========

    @And("^A product exists with the following details:$")
    public void aProductExistsWithTheFollowingDetails(DataTable dataTable) throws Exception {
        Map<String, String> productData = dataTable.asMap(String.class, String.class);
        currentProduct = fromMap(productData);
        currentProduct = productRepository.save(currentProduct);
    }

    @And("^The following products exist:$")
    public void theFollowingProductsExist(DataTable dataTable) throws Exception {
        List<Map<String, String>> products = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> productData : products) {
            Product product = fromMap(productData);
            productRepository.save(product);
        }
    }

    @And("^No products exist$")
    public void noProductsExist() {
        productRepository.deleteAll();
    }

    // ========== WHEN - Peticiones GET ==========

    @When("^I request the product with id \"([^\"]*)\"$")
    public void iRequestTheProductWithId(String id) throws Exception {
        var requestBuilder = get("/products/" + id)
                .accept(MediaType.APPLICATION_JSON);

        if (AuthenticationStepDefs.currentUsername != null) {
            requestBuilder = requestBuilder.with(AuthenticationStepDefs.authenticate());
        }

        stepDefs.result = stepDefs.mockMvc.perform(requestBuilder).andDo(print());
    }

    @When("^I request all products$")
    public void iRequestAllProducts() throws Exception {
        var requestBuilder = get("/products")
                .accept(MediaType.APPLICATION_JSON);

        if (AuthenticationStepDefs.currentUsername != null) {
            requestBuilder = requestBuilder.with(AuthenticationStepDefs.authenticate());
        }

        stepDefs.result = stepDefs.mockMvc.perform(requestBuilder).andDo(print());
    }

    @When("^I search for products with name \"([^\"]*)\"$")
    public void iSearchForProductsWithName(String name) throws Exception {
        var requestBuilder = get("/products/search/findByName")
                .param("name", name)
                .accept(MediaType.APPLICATION_JSON);

        if (AuthenticationStepDefs.currentUsername != null) {
            requestBuilder = requestBuilder.with(AuthenticationStepDefs.authenticate());
        }

        stepDefs.result = stepDefs.mockMvc.perform(requestBuilder).andDo(print());
    }

    // ========== THEN/AND - Verificaciones ==========

    @And("^The response contains (\\d+) products?$")
    public void theResponseContainsProducts(int count) throws Exception {
        if (count == 0) {
            stepDefs.result.andExpect(status().isOk());
        } else {
            stepDefs.result.andExpect(jsonPath("$._embedded.products", hasSize(count)));
        }
    }

    @And("^The response contains a product with name \"([^\"]*)\"$")
    public void theResponseContainsProductWithName(String name) throws Exception {
        stepDefs.result.andExpect(jsonPath("$.name", is(name)));
    }

    @And("^The response contains a product with price \"([^\"]*)\"$")
    public void theResponseContainsProductWithPrice(String price) throws Exception {
        stepDefs.result.andExpect(jsonPath("$.price", is(Double.parseDouble(price))));
    }

    @And("^The response contains a product with stock \"([^\"]*)\"$")
    public void theResponseContainsProductWithStock(String stock) throws Exception {
        stepDefs.result.andExpect(jsonPath("$.stock", is(Integer.parseInt(stock))));
    }

    @And("^The response contains a product with availability \"([^\"]*)\"$")
    public void theResponseContainsProductWithAvailability(String available) throws Exception {
        stepDefs.result.andExpect(jsonPath("$.available", is(Boolean.parseBoolean(available))));
    }

    @And("^The response contains a product with rating \"([^\"]*)\"$")
    public void theResponseContainsProductWithRating(String rating) throws Exception {
        stepDefs.result.andExpect(jsonPath("$.rating", is(Double.parseDouble(rating))));
    }

    // ========== MÉTODO AUXILIAR ==========

    private Product fromMap(Map<String, String> data) {
        Product p = new Product();

        // Valores por defecto
        p.setStock(0);
        p.setKcal(0);
        p.setCarbs(0);
        p.setProteins(0);
        p.setFats(0);
        p.setAvailable(true);
        p.setPartOfLoyaltyProgram(false);

        data.forEach((key, value) -> {
            if (value == null || value.isBlank()) return;
            switch (key) {
                case "name" -> p.setName(value);
                case "price" -> p.setPrice(new BigDecimal(value));
                case "stock" -> p.setStock(Integer.parseInt(value));
                case "barcode" -> p.setBarcode(value);
                case "rating" -> p.setRating(Double.parseDouble(value));
                case "tax" -> p.setTax(new BigDecimal(value));
                case "kcal" -> p.setKcal(Integer.parseInt(value));
                case "carbs" -> p.setCarbs(Integer.parseInt(value));
                case "proteins" -> p.setProteins(Integer.parseInt(value));
                case "fats" -> p.setFats(Integer.parseInt(value));
                case "pointsGiven" -> p.setPointsGiven(Integer.parseInt(value));
                case "pointsCost" -> p.setPointsCost(Integer.parseInt(value));
                case "isAvailable" -> p.setAvailable(Boolean.parseBoolean(value));
                case "isPartOfLoyaltyProgram" -> p.setPartOfLoyaltyProgram(Boolean.parseBoolean(value));
                case "brand" -> p.setBrand(value);
                case "description" -> p.setDescription(value);
                case "discount" -> p.setDiscount(value);
                case "size" -> p.setSize(value);
                case "promotions" -> p.setPromotions(value);
                default -> System.out.println("⚠️ Campo desconocido: " + key);
            }
        });

        return p;
    }
}