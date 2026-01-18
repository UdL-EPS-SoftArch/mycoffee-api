package cat.udl.eps.softarch.demo.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.util.Set;

//Attributes and methods of Product Class that extends UriEntity
@EqualsAndHashCode(callSuper = true)
@Entity(name = "Product")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product extends UriEntity<Long>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(unique = true, nullable = false)
    private String name;

    @org.hibernate.validator.constraints.Length(max = 100)
    @Column(length = 100)
    private String description;

    @PositiveOrZero
    private int stock;

    @Positive
    private BigDecimal price;

    private String brand;
    private String size;

    @Pattern(regexp = "\\d{13}", message = "El código de barras debe tener 13 dígitos numéricos")
    private String barcode;

    @PositiveOrZero
    private BigDecimal tax;

    @JsonProperty("available")
    private boolean isAvailable;

    private String promotions;
    private String discount;

    @PositiveOrZero private int kcal;
    @PositiveOrZero private int carbs;
    @PositiveOrZero private int proteins;
    @PositiveOrZero private int fats;

    // IMPORTAT: Excloure col·leccions del HashCode/Equals per evitar problemes amb Hibernate
    @ElementCollection
    @EqualsAndHashCode.Exclude
    private Set<String> ingredients;

    @ElementCollection
    @EqualsAndHashCode.Exclude
    private Set<String> allergens;

    @DecimalMin(value = "0")
    @DecimalMax(value = "5")
    private Double rating;

    // Loyalty program related fields
    @PositiveOrZero
    private Integer pointsGiven;

    @PositiveOrZero
    private Integer pointsCost;

    @JsonProperty("partOfLoyaltyProgram")
    private boolean isPartOfLoyaltyProgram;

    // Image storage fields
    @Lob
    @JsonIgnore
    @Column(length = 5242880) // 5MB max
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private byte[] image;

    private String imageContentType;

    // --- CORRECCIONS PRINCIPALS AQUÍ ---

    @ManyToMany(mappedBy = "products")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude // <--- VITAL: Evita que Hibernate cridi hashCode mentre gestiona la col·lecció
    private Set<Order> orders;

    @ManyToOne
    private Category category;

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude // <--- VITAL
    private Set<BasketItem> basketItems;
    // -----------------------------------

    @ManyToOne
    private Inventory inventory;

    @JsonProperty("categoryName")
    public String getCategoryName() {
        return category != null ? category.getName() : "General";
    }

}