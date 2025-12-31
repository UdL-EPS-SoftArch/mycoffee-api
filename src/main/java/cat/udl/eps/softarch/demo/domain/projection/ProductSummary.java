package cat.udl.eps.softarch.demo.domain.projection;

import cat.udl.eps.softarch.demo.domain.Category;
import cat.udl.eps.softarch.demo.domain.Product;
import org.springframework.data.rest.core.config.Projection;
import java.math.BigDecimal;

// Aquesta projecció només la farem servir per la LLISTA
@Projection(name = "productSummary", types = { Product.class })
public interface ProductSummary {
    Long getId();
    String getName();
    BigDecimal getPrice();
    int getStock();
    boolean getIsAvailable();
    String getDescription(); // Per la descripció curta de la card

    // Aquí podem agafar l'objecte sencer o fer servir el truc del categoryName
    // Si vols l'objecte sencer per la llista:
    Category getCategory();
}