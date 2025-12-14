package cat.udl.eps.softarch.demo.controller;

import cat.udl.eps.softarch.demo.domain.Business;
import cat.udl.eps.softarch.demo.domain.Product;
import cat.udl.eps.softarch.demo.repository.BusinessRepository;
import cat.udl.eps.softarch.demo.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final BusinessRepository businessRepository;

    public ProductController(ProductRepository productRepository, BusinessRepository businessRepository) {
        this.productRepository = productRepository;
        this.businessRepository = businessRepository;
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<?> updateStock(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        validateProductOwnership(product);

        Integer newStock = request.get("stock");
        if (newStock == null) {
            return ResponseEntity.badRequest().body("Stock value is required");
        }

        if (newStock < 0) {
            return ResponseEntity.badRequest().body("Stock cannot be negative");
        }

        product.setStock(newStock);

        // Auto-adjust availability based on stock
        if (newStock == 0) {
            product.setAvailable(false);
        }

        productRepository.save(product);

        return ResponseEntity.ok(Map.of(
                "message", "Stock updated successfully",
                "productId", id,
                "newStock", newStock,
                "isAvailable", product.isAvailable()
        ));
    }

    @PutMapping("/{id}/toggle-availability")
    public ResponseEntity<?> toggleAvailability(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        validateProductOwnership(product);

        // Check if we can make it available
        if (!product.isAvailable() && product.getStock() == 0) {
            return ResponseEntity.badRequest().body("Cannot make product available with 0 stock");
        }

        product.setAvailable(!product.isAvailable());
        productRepository.save(product);

        return ResponseEntity.ok(Map.of(
                "message", "Availability toggled successfully",
                "productId", id,
                "isAvailable", product.isAvailable()
        ));
    }

    private void validateProductOwnership(Product product) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            throw new AccessDeniedException("Authentication required");
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (isAdmin) {
            return;
        }

        if (product.getInventory() == null || product.getInventory().getBusiness() == null) {
            throw new AccessDeniedException("Product must be associated with a business");
        }

        String businessOwner = product.getInventory().getBusiness().getUsername();
        String currentUser = auth.getName();

        if (!currentUser.equals(businessOwner)) {
            throw new AccessDeniedException("You can only modify products from your own business");
        }
    }
}
