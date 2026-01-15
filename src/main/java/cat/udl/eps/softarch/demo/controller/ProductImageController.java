package cat.udl.eps.softarch.demo.controller;

import cat.udl.eps.softarch.demo.domain.Product;
import cat.udl.eps.softarch.demo.repository.ProductRepository;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders; // Import necessari
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // Això inclou @RestController
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController // <--- RECOMANAT: Canvia BasePathAwareController per RestController
public class ProductImageController {

    private final ProductRepository productRepository;

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    public ProductImageController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // POST està bé
    @PostMapping("/products/{id}/image")
    public ResponseEntity<?> uploadImage(@PathVariable Long id, @RequestParam("image") MultipartFile file) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Image file is required");
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            return ResponseEntity.badRequest().body("Image size exceeds maximum allowed size"); // Text ajustat al test
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            return ResponseEntity.badRequest().body("Invalid image format"); // Text ajustat al test
        }

        try {
            product.setImage(file.getBytes());
            product.setImageContentType(contentType);
            productRepository.save(product);
            return ResponseEntity.ok().body("Image uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image");
        }
    }

    // GET: CORREGIT
    @GetMapping("/products/{id}/image") // <--- AFEGIT /products/
    public ResponseEntity<Resource> getImage(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        byte[] imageBytes = product.getImage();

        if (imageBytes == null || imageBytes.length == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"); // Missatge simple pel 404
        }

        ByteArrayResource resource = new ByteArrayResource(imageBytes);

        return ResponseEntity.ok()
                // Usem el tipus real de la imatge, no sempre PNG
                .header(HttpHeaders.CONTENT_TYPE, product.getImageContentType())
                .contentLength(imageBytes.length)
                .body(resource);
    }

    // DELETE està bé
    @DeleteMapping("/products/{id}/image")
    public ResponseEntity<?> deleteImage(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (product.getImage() == null) {
            return ResponseEntity.notFound().build();
        }

        product.setImage(null);
        product.setImageContentType(null);
        productRepository.save(product);

        return ResponseEntity.ok().body("Image deleted successfully");
    }
}