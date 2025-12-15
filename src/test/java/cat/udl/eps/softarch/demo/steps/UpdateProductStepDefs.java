package cat.udl.eps.softarch.demo.steps;

import cat.udl.eps.softarch.demo.domain.Product;
import cat.udl.eps.softarch.demo.repository.ProductRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

public class UpdateProductStepDefs {

    @Autowired
    private StepDefs stepDefs;

    @Autowired
    private ProductRepository productRepository;

    // ========== WHEN - Actualizar producto ==========

    @When("^I update the product with id \"([^\"]*)\" with the following details:$")
    public void iUpdateTheProductWithIdWithTheFollowingDetails(String id, DataTable dataTable) throws Exception {
        Map<String, String> updateData = dataTable.asMap(String.class, String.class);

        // Construir el JSON solo con los campos a actualizar
        StringBuilder jsonBuilder = new StringBuilder("{");
        boolean first = true;

        for (Map.Entry<String, String> entry : updateData.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value == null || value.isBlank()) {
                // Para campos vacíos, enviar string vacío
                if (!first) jsonBuilder.append(",");
                jsonBuilder.append("\"").append(key).append("\":\"\"");
                first = false;
                continue;
            }

            if (!first) {
                jsonBuilder.append(",");
            }
            first = false;

            switch (key) {
                case "name", "brand", "size", "barcode", "promotions", "discount", "description" ->
                    jsonBuilder.append("\"").append(key).append("\":\"").append(value).append("\"");
                case "price", "tax" ->
                    jsonBuilder.append("\"").append(key).append("\":").append(value);
                case "stock", "kcal", "carbs", "proteins", "fats", "pointsGiven", "pointsCost" ->
                    jsonBuilder.append("\"").append(key).append("\":").append(Integer.parseInt(value));
                case "isAvailable", "isPartOfLoyaltyProgram" -> {
                    String fieldName = key.equals("isAvailable") ? "available" : "partOfLoyaltyProgram";
                    jsonBuilder.append("\"").append(fieldName).append("\":").append(Boolean.parseBoolean(value));
                }
                case "rating" ->
                    jsonBuilder.append("\"").append(key).append("\":").append(Double.parseDouble(value));
                default -> System.out.println("⚠️ Campo desconocido en UPDATE: " + key);
            }
        }

        jsonBuilder.append("}");
        String jsonBody = jsonBuilder.toString();

        // 🔍 DEBUG
        System.out.println("📤 PATCH JSON enviado: " + jsonBody);

        var requestBuilder = patch("/products/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON);

        if (AuthenticationStepDefs.currentUsername != null) {
            requestBuilder = requestBuilder.with(AuthenticationStepDefs.authenticate());
        }

        stepDefs.result = stepDefs.mockMvc.perform(requestBuilder).andDo(print());

        String responseBody = stepDefs.result.andReturn().getResponse().getContentAsString();
        System.out.println("📥 Respuesta: " + responseBody);
    }
}
