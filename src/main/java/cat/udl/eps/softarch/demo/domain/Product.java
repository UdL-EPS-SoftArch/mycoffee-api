package cat.udl.eps.softarch.demo.domain;

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
    private String name;

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

    private boolean isAvailable;

    private String promotions;
    private String discount;

    @PositiveOrZero private int kcal;
    @PositiveOrZero private int carbs;
    @PositiveOrZero private int proteins;
    @PositiveOrZero private int fats;

    @ElementCollection
    private Set<String> ingredients;
    @ElementCollection
    private Set<String> allergens;

    @DecimalMin(value = "0")
    @DecimalMax(value = "5")
    private Double rating;

    // Loyalty program related fields
    @PositiveOrZero
    private Integer pointsGiven; // Points given when purchasing this product
    
    @PositiveOrZero
    private Integer pointsCost;  // Points needed to redeem this product
    
    private boolean isPartOfLoyaltyProgram;

    
    //TODO
    // @ManyToMany(mappedBy = "products")
    //    private Set<Order> orders;

    @ManyToOne
    private Category category;

    //TODO
    // @ManyToMany
    // private Set<Basket> baskets;


    //TODO
    // @OneToMany(cascade = CascadeType.ALL)
    // private Set<Loyalty> loyalties;
    //
    //TODO
    // @ManyToOne
    // private Inventory inventory;


}
