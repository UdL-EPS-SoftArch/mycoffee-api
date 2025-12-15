package cat.udl.eps.softarch.demo.steps;

import cat.udl.eps.softarch.demo.domain.Product;
import cat.udl.eps.softarch.demo.repository.ProductRepository;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Map;


public class CreateProductStepDefs {

    @Autowired
    private StepDefs stepDefs;

    public static Product currentProduct;

    @Autowired
    private ProductRepository productRepository;

    @Before
    public void setUp(){
        //Mirar si es bona practica ficar un step de el RegisterStepDefs
    }

    /**
     * Converteix un Map<String, String> en un Product, mapejant només els camps presents.
     */
    private Product fromMap(Map<String, String> data) {
        Product p = new Product();

        // ⭐ Valores por defecto para campos con validaciones
        p.setStock(0);
        p.setKcal(0);
        p.setCarbs(0);
        p.setProteins(0);
        p.setFats(0);
        p.setAvailable(true);  // Disponible por defecto
        p.setPartOfLoyaltyProgram(false);

        // Sobrescribir con los datos del test
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

    @When("^I register a new product with the following details:$")
    public void iRegisterANewProductWithDetails(io.cucumber.datatable.DataTable dataTable) throws Exception {
        // Convertir la tabla correctamente (primera fila = headers, segunda = valores)
        Map<String, String> productData = dataTable.asMap(String.class, String.class);

        // 🔍 DEBUG: Ver qué recibe de Cucumber
        System.out.println("📋 Datos recibidos de Cucumber:");
        productData.forEach((k, v) -> System.out.println("  " + k + " = " + v));

        currentProduct = fromMap(productData);

        // 🔍 DEBUG
        String jsonBody = stepDefs.mapper.writeValueAsString(currentProduct);
        System.out.println("📤 JSON enviado: " + jsonBody);

        var requestBuilder = post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON);

        if (AuthenticationStepDefs.currentUsername != null) {
            requestBuilder = requestBuilder.with(AuthenticationStepDefs.authenticate());
        }

        stepDefs.result = stepDefs.mockMvc.perform(requestBuilder).andDo(print());

        // 🔍 DEBUG
        MvcResult mvcresult = stepDefs.result.andReturn();
        String responseBody = mvcresult.getResponse().getContentAsString();
        System.out.println("📥 Respuesta: " + responseBody);
    }

    @And("^The product with name \"([^\"]*)\" is registered$")
    public void theProductWithNameIsRegistered(String productName) throws Exception {

        if (productRepository.findByName(productName).isEmpty()) {
            Product product = new Product();
            product.setName(productName);
            productRepository.save(product);
        }

    }

    @And("^The product with name \"([^\"]*)\" is not registered$")
    public void theProductWithNameIsNotRegistered(String productName) throws Exception {
        stepDefs.result = stepDefs.mockMvc.perform(
                        get("/products/search/findByName")
                                .param("name", productName)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(AuthenticationStepDefs.authenticate()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.products", hasSize(0))); // Solo 1
    }
}
