package cat.udl.eps.softarch.demo.domain.projection;

import cat.udl.eps.softarch.demo.domain.Category;
import cat.udl.eps.softarch.demo.domain.Product;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "inlineCategory", types = { Product.class })
public interface ProductInlineCategory {
    // Aquests getters exposen els camps normals del producte
    String getName();
    String getDescription();
    java.math.BigDecimal getPrice();
    int getStock();
    boolean getIsAvailable();
    // ... afegeix altres getters si els necessites al front (image, brand, etc.)

    // AQUESTA és la clau: incrusta l'objecte Category sencer enlloc d'un link
    Category getCategory();
}