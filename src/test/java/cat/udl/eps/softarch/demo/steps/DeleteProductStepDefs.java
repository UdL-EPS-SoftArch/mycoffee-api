package cat.udl.eps.softarch.demo.steps;

import cat.udl.eps.softarch.demo.domain.Product;
import cat.udl.eps.softarch.demo.repository.ProductRepository;
import io.cucumber.java.en.When;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Transactional
public class DeleteProductStepDefs {

    @Autowired
    private StepDefs stepDefs;

    @Autowired
    private ProductRepository productRepository;

    public static Product currentProduct;

    @When("^I delete the product with id \"([^\"]*)\"$")
    public void iRequestTheProductWithId(String id) throws Exception {

        var requestBuilder = delete("/products/" + id)
                .accept(MediaType.APPLICATION_JSON);

        if (AuthenticationStepDefs.currentUsername != null) {
            requestBuilder = requestBuilder.with(AuthenticationStepDefs.authenticate());
        }

        stepDefs.result = stepDefs.mockMvc.perform(requestBuilder).andDo(print());

    }


}
