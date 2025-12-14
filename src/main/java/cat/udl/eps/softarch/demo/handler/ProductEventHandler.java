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
        validateBusinessLogic(product);
    }

    @HandleBeforeSave
    public void handleBeforeSave(Product product) {
        logger.info("Before updating product: {}", product.getName());
        validateProductAccess(product, "update");
        validateBusinessLogic(product);
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

    private void validateBusinessLogic(Product product) {
        // Validate loyalty program consistency
        if (product.isPartOfLoyaltyProgram()) {
            Integer pointsGiven = product.getPointsGiven();
            Integer pointsCost = product.getPointsCost();

            if ((pointsGiven == null || pointsGiven == 0) && (pointsCost == null || pointsCost == 0)) {
                logger.error("Product {} is part of loyalty program but has no points defined", product.getName());
                throw new IllegalArgumentException(
                        "Products in loyalty program must have either pointsGiven or pointsCost greater than 0");
            }

            logger.info("Product {} loyalty program validated: pointsGiven={}, pointsCost={}",
                    product.getName(), pointsGiven, pointsCost);
        }

        // Auto-adjust availability based on stock
        if (product.getStock() == 0 && product.isAvailable()) {
            logger.warn("Product {} has 0 stock but is marked as available. Setting to unavailable.",
                    product.getName());
            product.setAvailable(false);
        }

        // Validate price is set
        if (product.getPrice() == null || product.getPrice().doubleValue() <= 0) {
            logger.error("Product {} has invalid price: {}", product.getName(), product.getPrice());
            throw new IllegalArgumentException("Product must have a valid price greater than 0");
        }
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
