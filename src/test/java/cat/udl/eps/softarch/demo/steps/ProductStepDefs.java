package cat.udl.eps.softarch.demo.steps;

import cat.udl.eps.softarch.demo.domain.Product;
import cat.udl.eps.softarch.demo.repository.ProductRepository;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;




public class ProductStepDefs {

    @Autowired
    private StepDefs stepDefs;

    public static String currentUsername;
    public static String currentPassword;
    public static Product currentProduct;

    @Autowired
    private ProductRepository productRepository;

    @Before
    public void setUp(){
        //Mirar si es bona practica ficar un step de el RegisterStepDefs
    }

    @When("^I register a new product with name \"([^\"]*)\"$")
    public void iRegisterANewProductWithName(String name) throws Exception {
        currentProduct = new Product();
        currentProduct.setName(name);

        stepDefs.result = stepDefs.mockMvc.perform(
                post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stepDefs.mapper.writeValueAsString(currentProduct))
                        .accept(MediaType.APPLICATION_JSON)
                        .with(AuthenticationStepDefs.authenticate())
        ).andDo(print());
    }


    @And("^The product with name \"([^\"]*)\" is not registered$")
    public void theProductWithNameIsNotRegistered(String productName) throws Throwable {
        stepDefs.result = stepDefs.mockMvc.perform(
                        get("/products")
                                .param("name", productName)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(AuthenticationStepDefs.authenticate()))
                .andExpect(status().isNotFound());
    }
}
