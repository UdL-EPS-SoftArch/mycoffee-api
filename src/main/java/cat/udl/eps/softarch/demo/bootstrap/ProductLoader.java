package cat.udl.eps.softarch.demo.bootstrap;

import cat.udl.eps.softarch.demo.domain.Business;
import cat.udl.eps.softarch.demo.domain.Category;
import cat.udl.eps.softarch.demo.domain.Inventory;
import cat.udl.eps.softarch.demo.domain.Product;
import cat.udl.eps.softarch.demo.repository.BusinessRepository;
import cat.udl.eps.softarch.demo.repository.CategoryRepository;
import cat.udl.eps.softarch.demo.repository.InventoryRepository;
import cat.udl.eps.softarch.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class ProductLoader implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BusinessRepository businessRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            loadData();
        }
    }

    private void loadData() {


        Business mainBusiness = new Business();
        mainBusiness.setName("Lleida Coffee Shop");
        mainBusiness.setEmail("info@lleidacoffee.com");
        mainBusiness.setUsername("admin1");


        mainBusiness.setPassword("password123"); // Ara té 11 caràcters, ja passarà la validació
        mainBusiness.setAddress("Carrer Major, 1, Lleida");
        // ------------------------------------------------

        businessRepository.save(mainBusiness);

        Inventory mainInventory = new Inventory();
        mainInventory.setName("Main Warehouse");
        mainInventory.setDescription("Magatzem principal de la botiga");
        mainInventory.setLocation("Lleida, Rovira Roure 4");
        mainInventory.setTotalStock(1000);
        mainInventory.setBusiness(mainBusiness);

        inventoryRepository.save(mainInventory);

        System.out.println("📦 Created Main Inventory: " + mainInventory.getName());


        Category catCoffee = Category.builder().name("Specialty Coffee").description("Best beans in town").build();
        Category catFood = Category.builder().name("Food & Pastry").description("Freshly baked goods").build();
        Category catDrink = Category.builder().name("Drinks").description("Cold and refreshing").build();
        Category catAlt = Category.builder().name("Alternatives").description("Plant based options").build();
        Category catMerch = Category.builder().name("Merchandise").description("Gifts and accessories").build();

        categoryRepository.saveAll(Arrays.asList(catCoffee, catFood, catDrink, catAlt, catMerch));



        Product cafeEtiopia = Product.builder()
                .name("Ethiopia Yirgacheffe (250g)")
                .description("Whole bean coffee with floral and citrus notes.")
                .price(new BigDecimal("14.50"))
                .stock(20)
                .isAvailable(true)
                .brand("Nomad Coffee")
                .category(catCoffee)
                .inventory(mainInventory)
                .barcode("8410000000010")
                .kcal(2)
                .ingredients(Set.of("100% Washed Arabica Coffee"))
                .rating(4.8)
                .isPartOfLoyaltyProgram(true)
                .pointsGiven(15)
                .pointsCost(150)
                .build();

        Product croissant = Product.builder()
                .name("Artisan Butter Croissant")
                .description("Baked fresh every morning.")
                .price(new BigDecimal("2.20"))
                .stock(15)
                .isAvailable(true)
                .brand("Local Bakery")
                .category(catFood)
                .inventory(mainInventory)
                .barcode("8410000000020")
                .kcal(280)
                .carbs(30)
                .proteins(5)
                .ingredients(Set.of("Flour", "Butter", "Sugar", "Yeast"))
                .allergens(Set.of("Gluten", "Dairy", "Egg"))
                .rating(4.5)
                .isPartOfLoyaltyProgram(true)
                .pointsGiven(2)
                .pointsCost(20)
                .build();

        Product coldBrew = Product.builder()
                .name("Organic Cold Brew")
                .description("Cold infused coffee for 24h.")
                .price(new BigDecimal("4.50"))
                .stock(3)
                .isAvailable(true)
                .brand("MyCoffee House")
                .category(catDrink)
                .inventory(mainInventory)
                .barcode("8410000000030")
                .kcal(5)
                .ingredients(Set.of("Filtered water", "Coffee"))
                .rating(4.2)
                .isPartOfLoyaltyProgram(true)
                .pointsGiven(5)
                .pointsCost(50)
                .build();

        Product cookieXoc = Product.builder()
                .name("XXL Chocolate Cookie")
                .description("American-style cookie.")
                .price(new BigDecimal("2.50"))
                .stock(0)
                .isAvailable(true)
                .brand("Local Bakery")
                .category(catFood)
                .inventory(mainInventory)
                .barcode("8410000000040")
                .kcal(350)
                .allergens(Set.of("Gluten", "Nuts", "Soy"))
                .rating(4.9)
                .isPartOfLoyaltyProgram(false)
                .build();

        Product oatMilk = Product.builder()
                .name("Barista Oat Milk")
                .description("The perfect plant-based alternative.")
                .price(new BigDecimal("3.20"))
                .stock(50)
                .isAvailable(true)
                .brand("Oatly")
                .category(catAlt)
                .inventory(mainInventory)
                .barcode("8410000000050")
                .kcal(60)
                .ingredients(Set.of("Water", "Oats", "Rapeseed oil"))
                .allergens(Set.of("Gluten"))
                .rating(4.0)
                .isPartOfLoyaltyProgram(true)
                .pointsGiven(3)
                .pointsCost(30)
                .build();

        Product giftPack = Product.builder()
                .name("Barista Tasting Pack")
                .description("Includes 3 bags of single-origin coffee.")
                .price(new BigDecimal("45.00"))
                .stock(10)
                .isAvailable(true)
                .brand("MyCoffee House")
                .category(catMerch)
                .inventory(mainInventory)
                .barcode("8410000000060")
                .rating(5.0)
                .isPartOfLoyaltyProgram(true)
                .pointsGiven(50)
                .pointsCost(500)
                .build();

        productRepository.saveAll(Arrays.asList(cafeEtiopia, croissant, coldBrew, cookieXoc, oatMilk, giftPack));

        System.out.println("-------------------------------------------------------");
        System.out.println("☕ COFFEE SHOP MENU LOADED WITH CATEGORIES & INVENTORY ☕");
        System.out.println("-------------------------------------------------------");
    }
}