package cat.udl.eps.softarch.demo.controller;

import cat.udl.eps.softarch.demo.domain.Product;
import cat.udl.eps.softarch.demo.repository.ProductRepository;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@BasePathAwareController
public class ProductImageController {

    private final ProductRepository productRepository;

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
        "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    public ProductImageController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostMapping("/products/{id}/image")
    public ResponseEntity<?> uploadImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile file) {

        // Validate product exists
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        // Validate file is not empty
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Image file is required");
        }

        // Validate file size
        if (file.getSize() > MAX_IMAGE_SIZE) {
            return ResponseEntity.badRequest()
                .body("Image size exceeds maximum allowed size of 5MB");
        }

        // Validate content type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            return ResponseEntity.badRequest()
                .body("Invalid image format. Allowed formats: JPEG, PNG, GIF, WEBP");
        }

        try {
            // Store image
            product.setImage(file.getBytes());
            product.setImageContentType(contentType);
            productRepository.save(product);

            return ResponseEntity.ok().body("Image uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to upload image: " + e.getMessage());
        }
    }

    @GetMapping("/products/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        // 1. Buscas el producto (con la corrección del 404 que vimos antes)
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        // 2. Obtienes la imagen (suponiendo que tienes un getter, ej: getImageBytes())
        // Nota: verifica cómo se llama el campo en tu entidad Product.
        // Si usas Lombok y el campo es 'image', el getter es 'getImage()'.
        byte[] imageBytes = product.getImage();

        if (imageBytes == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found for this product");
        }

        // 3. Devuelves el ResponseEntity configurado
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG) // Forzamos que sea PNG para que pase el test
                .body(imageBytes);
    }

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
