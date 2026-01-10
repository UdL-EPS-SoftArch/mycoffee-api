package cat.udl.eps.softarch.demo.domain;

import cat.udl.eps.softarch.demo.domain.UriEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Inventory extends UriEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String location;

    private int totalStock;

    @ManyToOne
    private Business business;

    @Enumerated(EnumType.STRING)
    private InventoryType type;

    @Enumerated(EnumType.STRING)
    private InventoryStatus status;

    private Integer capacity;

    private java.time.LocalDateTime lastUpdated;

    @OneToMany(mappedBy = "inventory", fetch = FetchType.LAZY)
    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Product> products;

    /**
     * Calculates the real total stock from the list of products.
     * Returns 0 if the product list is not loaded/initialized.
     */
    public int getCalculatedTotalStock() {
        if (products == null) {
            return 0;
        }
        return products.stream()
                .mapToInt(Product::getStock)
                .sum();
    }

    /**
     * Checks if the inventory is full based on capacity.
     */
    public boolean isFull() {
        if (capacity == null || capacity == 0)
            return false;
        return getCalculatedTotalStock() >= capacity;
    }

    /**
     * Updates the local totalStock field to match the calculated reality.
     */
    public void syncTotalStock() {
        this.totalStock = getCalculatedTotalStock();
    }
}
