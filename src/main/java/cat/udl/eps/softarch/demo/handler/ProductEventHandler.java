package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.Product;
import cat.udl.eps.softarch.demo.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class ProductEventHandler {

    private final Logger logger = LoggerFactory.getLogger(ProductEventHandler.class);
    private final ProductRepository productRepository;

    public ProductEventHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @HandleBeforeCreate
    public void handleBeforeCreate(Product product) {
        logger.info("Before creating product: {}", product.getName());
        validateProductAccess(product, "create");
    }

    @HandleBeforeSave
    public void handleBeforeSave(Product product) {
        logger.info("Before updating product: {}", product.getName());
        validateProductAccess(product, "update");
    }

    @HandleBeforeDelete
    public void handleBeforeDelete(Product product) {
        logger.info("Before deleting product: {}", product.getName());
        validateProductAccess(product, "delete");
    }

    @HandleBeforeLinkSave
    public void handleBeforeLinkSave(Product product, Object linked) {
        logger.info("Before linking: {} to {}", product.getName(), linked);
    }

    @HandleAfterCreate
    public void handleAfterCreate(Product product) {
        logger.info("After creating product: {}", product.getName());
    }

    @HandleAfterSave
    public void handleAfterSave(Product product) {
        logger.info("After updating product: {}", product.getName());
    }

    @HandleAfterDelete
    public void handleAfterDelete(Product product) {
        logger.info("After deleting product: {}", product.getName());
    }

    @HandleAfterLinkSave
    public void handleAfterLinkSave(Product product, Object linked) {
        logger.info("After linking: {} to {}", product.getName(), linked);
    }

    private void validateProductAccess(Product product, String operation) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            throw new AccessDeniedException("Authentication required to " + operation + " products");
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (isAdmin) {
            logger.info("Admin user {} performing {} on product", auth.getName(), operation);
            return;
        }

        if (product.getInventory() == null || product.getInventory().getBusiness() == null) {
            logger.warn("Product {} has no inventory or business associated", product.getName());
            throw new AccessDeniedException("Product must be associated with a business inventory");
        }

        String businessOwner = product.getInventory().getBusiness().getUsername();
        String currentUser = auth.getName();

        if (!currentUser.equals(businessOwner)) {
            logger.warn("User {} attempted to {} product {} owned by business {}",
                    currentUser, operation, product.getName(), businessOwner);
            throw new AccessDeniedException("You can only " + operation + " products from your own business");
        }

        logger.info("Business owner {} performing {} on product {}", currentUser, operation, product.getName());
    }
}
