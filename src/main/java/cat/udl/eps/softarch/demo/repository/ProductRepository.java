package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.Basket;
import cat.udl.eps.softarch.demo.domain.Business;
import cat.udl.eps.softarch.demo.domain.Category;
import cat.udl.eps.softarch.demo.domain.Inventory;
import cat.udl.eps.softarch.demo.domain.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


import java.math.BigDecimal;
import java.util.List;

@RepositoryRestResource
public interface ProductRepository extends CrudRepository<Product, Long>, PagingAndSortingRepository<Product, Long> {


    List<Product> findByName(String name);
    List<Product> findByBrand(String brand);
    List<Product> findByPriceBetween(BigDecimal min, BigDecimal max);
    List<Product> findByRatingGreaterThanEqual(Double rating);
    List<Product> findByIsAvailable(boolean available);
    List<Product> findByPromotions( String promotion);
    List<Product> findBySize(String size);
    List<Product> findByKcalLessThanEqual(int kcal);
    List<Product> findByIngredientsContaining(String ingredient);
    List<Product> findByAllergensContaining(String allergen);
    List<Product> findByCategory(Category category);

    // List<Product> findByOrders(Order order);
    List<Product> findByBaskets(Basket basket);

    // Loyalty related queries
    List<Product> findByIsPartOfLoyaltyProgram(boolean isPartOfLoyaltyProgram);
    List<Product> findByPointsCostLessThanEqual(Integer points);
    List<Product> findByPointsGivenGreaterThan(Integer points);

    // Inventory and Business queries
    List<Product> findByInventory(Inventory inventory);
    List<Product> findByInventory_Business(Business business);
    List<Product> findByStockLessThanEqual(int stock);
    List<Product> findByStockGreaterThan(int stock);
}
