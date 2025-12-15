package cat.udl.eps.softarch.demo.bootstrap;

import cat.udl.eps.softarch.demo.domain.Product;
import cat.udl.eps.softarch.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;
import java.util.Arrays;

@Component
@Profile("!test") // Important: Això evita que carregui dades quan executes 'mvn test'
@RequiredArgsConstructor
public class ProductLoader implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            loadData();
        }
    }

    private void loadData() {
        // 1. SPECIALTY COFFEE (Star product)
        Product cafeEtiopia = Product.builder()
                .name("Ethiopia Yirgacheffe (250g)")
                .description("Whole bean coffee with floral and citrus notes. Medium roast ideal for filter brewing.")
                .price(new BigDecimal("14.50"))
                .stock(20)
                .isAvailable(true)
                .brand("Nomad Coffee")
                .barcode("8410000000010")
                .kcal(2)
                .ingredients(Set.of("100% Washed Arabica Coffee"))
                .allergens(Set.of())
                .rating(4.8)
                .isPartOfLoyaltyProgram(true)
                .pointsGiven(15)
                .pointsCost(150)
                .build();

        // 2. PASTRY (To test allergens and short descriptions)
        Product croissant = Product.builder()
                .name("Artisan Butter Croissant")
                .description("Baked fresh every morning in our bakery. Crispy on the outside, tender on the inside.")
                .price(new BigDecimal("2.20"))
                .stock(15)
                .isAvailable(true)
                .brand("Local Bakery")
                .barcode("8410000000020")
                .kcal(280)
                .carbs(30)
                .fats(16)
                .proteins(5)
                .ingredients(Set.of("Flour", "Butter", "Sugar", "Yeast"))
                .allergens(Set.of("Gluten", "Dairy", "Egg")) // Multiple allergens to test UI
                .rating(4.5)
                .isPartOfLoyaltyProgram(true)
                .pointsGiven(2)
                .pointsCost(20)
                .build();

        // 3. COLD DRINK (To test low stock warning)
        Product coldBrew = Product.builder()
                .name("Organic Cold Brew")
                .description("Cold infused coffee for 24h. Intense and refreshing.")
                .price(new BigDecimal("4.50"))
                .stock(3) // LOW STOCK: Only 3 left!
                .isAvailable(true)
                .brand("MyCoffee House")
                .barcode("8410000000030")
                .kcal(5)
                .ingredients(Set.of("Filtered water", "Coffee"))
                .rating(4.2)
                .isPartOfLoyaltyProgram(true)
                .pointsGiven(5)
                .pointsCost(50)
                .build();

        // 4. SOLD OUT ITEM (To test red color / disabled button)
        Product cookieXoc = Product.builder()
                .name("XXL Chocolate Cookie")
                .description("American-style cookie with Belgian chocolate chunks.")
                .price(new BigDecimal("2.50"))
                .stock(0) // SOLD OUT
                .isAvailable(true)
                .brand("Local Bakery")
                .barcode("8410000000040")
                .kcal(350)
                .allergens(Set.of("Gluten", "Nuts", "Soy"))
                .rating(4.9)
                .isPartOfLoyaltyProgram(false) // No points awarded
                .build();

        // 5. VEGAN / ALTERNATIVE PRODUCT
        Product oatMilk = Product.builder()
                .name("Barista Oat Milk")
                .description("The perfect plant-based alternative to foam up your cappuccino.")
                .price(new BigDecimal("3.20"))
                .stock(50)
                .isAvailable(true)
                .brand("Oatly")
                .barcode("8410000000050")
                .kcal(60)
                .carbs(8)
                .ingredients(Set.of("Water", "Oats", "Rapeseed oil"))
                .allergens(Set.of("Gluten"))
                .rating(4.0)
                .isPartOfLoyaltyProgram(true)
                .pointsGiven(3)
                .pointsCost(30)
                .build();

        // 6. EXPENSIVE ITEM (Gift pack)
        Product giftPack = Product.builder()
                .name("Barista Tasting Pack")
                .description("Includes 3 bags of single-origin coffee (Kenya, Colombia, Brazil) and an exclusive mug.")
                .price(new BigDecimal("45.00"))
                .stock(10)
                .isAvailable(true)
                .brand("MyCoffee House")
                .barcode("8410000000060")
                .rating(5.0)
                .isPartOfLoyaltyProgram(true)
                .pointsGiven(50)
                .pointsCost(500)
                .build();

        // Save all products
        productRepository.saveAll(Arrays.asList(cafeEtiopia, croissant, coldBrew, cookieXoc, oatMilk, giftPack));

        System.out.println("------------------------------------------------");
        System.out.println("☕ COFFEE SHOP MENU LOADED: 6 Delicious products ☕");
        System.out.println("------------------------------------------------");
    }
}