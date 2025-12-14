package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.Product;
import cat.udl.eps.softarch.demo.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.*;
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
    }

    @HandleBeforeSave
    public void handleBeforeSave(Product product) {
        logger.info("Before updating product: {}", product.getName());
    }

    @HandleBeforeDelete
    public void handleBeforeDelete(Product product) {
        logger.info("Before deleting product: {}", product.getName());
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
}
