package cat.udl.eps.softarch.demo.steps;

import cat.udl.eps.softarch.demo.domain.Product;
import cat.udl.eps.softarch.demo.repository.ProductRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class ProductImageStepDefs {

    @Autowired
    private StepDefs stepDefs;

    @Autowired
    private ProductRepository productRepository;

    private static final byte[] VALID_IMAGE = new byte[]{
        (byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A  // PNG header
    };

    // ========== GIVEN/AND - Preparar datos ==========

    @And("^The product with id \"([^\"]*)\" has an image$")
    public void theProductWithIdHasAnImage(String id) throws Exception {
        Product product = productRepository.findById(Long.parseLong(id))
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        product.setImage(VALID_IMAGE);
        product.setImageContentType("image/png");
        productRepository.save(product);
    }

    // ========== WHEN - Subir imagen ==========

    @When("^I upload an image to product with id \"([^\"]*)\"$")
    public void iUploadAnImageToProductWithId(String id) throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile(
            "image",
            "test-image.png",
            "image/png",
            VALID_IMAGE
        );

        var requestBuilder = multipart("/products/" + id + "/image")
                .file(imageFile)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        if (AuthenticationStepDefs.currentUsername != null) {
            requestBuilder = requestBuilder.with(AuthenticationStepDefs.authenticate());
        }

        stepDefs.result = stepDefs.mockMvc.perform(requestBuilder).andDo(print());
    }

    @When("^I upload an invalid image file to product with id \"([^\"]*)\"$")
    public void iUploadAnInvalidImageFileToProductWithId(String id) throws Exception {
        MockMultipartFile textFile = new MockMultipartFile(
            "image",
            "test.txt",
            "text/plain",
            "This is not an image".getBytes()
        );

        var requestBuilder = multipart("/products/" + id + "/image")
                .file(textFile)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        if (AuthenticationStepDefs.currentUsername != null) {
            requestBuilder = requestBuilder.with(AuthenticationStepDefs.authenticate());
        }

        stepDefs.result = stepDefs.mockMvc.perform(requestBuilder).andDo(print());
    }

    @When("^I upload a large image to product with id \"([^\"]*)\"$")
    public void iUploadALargeImageToProductWithId(String id) throws Exception {
        // Create a 6MB file (exceeds 5MB limit)
        byte[] largeImage = new byte[6 * 1024 * 1024];

        MockMultipartFile largeFile = new MockMultipartFile(
            "image",
            "large-image.png",
            "image/png",
            largeImage
        );

        var requestBuilder = multipart("/products/" + id + "/image")
                .file(largeFile)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        if (AuthenticationStepDefs.currentUsername != null) {
            requestBuilder = requestBuilder.with(AuthenticationStepDefs.authenticate());
        }

        stepDefs.result = stepDefs.mockMvc.perform(requestBuilder).andDo(print());
    }

    @When("^I upload an empty image to product with id \"([^\"]*)\"$")
    public void iUploadAnEmptyImageToProductWithId(String id) throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
            "image",
            "empty.png",
            "image/png",
            new byte[0]
        );

        var requestBuilder = multipart("/products/" + id + "/image")
                .file(emptyFile)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        if (AuthenticationStepDefs.currentUsername != null) {
            requestBuilder = requestBuilder.with(AuthenticationStepDefs.authenticate());
        }

        stepDefs.result = stepDefs.mockMvc.perform(requestBuilder).andDo(print());
    }

    // ========== WHEN - Obtener imagen ==========

    @When("^I request the image of product with id \"([^\"]*)\"$")
    public void iRequestTheImageOfProductWithId(String id) throws Exception {
        var requestBuilder = get("/products/" + id + "/image");

        if (AuthenticationStepDefs.currentUsername != null) {
            requestBuilder = requestBuilder.with(AuthenticationStepDefs.authenticate());
        }

        stepDefs.result = stepDefs.mockMvc.perform(requestBuilder).andDo(print());
    }

    // ========== WHEN - Eliminar imagen ==========

    @When("^I delete the image of product with id \"([^\"]*)\"$")
    public void iDeleteTheImageOfProductWithId(String id) throws Exception {
        var requestBuilder = delete("/products/" + id + "/image");

        if (AuthenticationStepDefs.currentUsername != null) {
            requestBuilder = requestBuilder.with(AuthenticationStepDefs.authenticate());
        }

        stepDefs.result = stepDefs.mockMvc.perform(requestBuilder).andDo(print());
    }

    // ========== THEN/AND - Verificaciones ==========

    @And("^The response contains message \"([^\"]*)\"$")
    public void theResponseContainsMessage(String message) throws Exception {
        stepDefs.result.andExpect(content().string(containsString(message)));
    }

    @And("^The response content type is \"([^\"]*)\"$")
    public void theResponseContentTypeIs(String contentType) throws Exception {
        stepDefs.result.andExpect(content().contentType(contentType));
    }

    @And("^The product with id \"([^\"]*)\" has the new image$")
    public void theProductWithIdHasTheNewImage(String id) throws Exception {
        Product product = productRepository.findById(Long.parseLong(id))
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        assert product.getImage() != null : "Product should have an image";
        assert product.getImageContentType() != null : "Product should have image content type";
    }
}
