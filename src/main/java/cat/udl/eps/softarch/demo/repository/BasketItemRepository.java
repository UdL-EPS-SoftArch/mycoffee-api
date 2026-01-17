package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.Basket;
import cat.udl.eps.softarch.demo.domain.BasketItem;
import cat.udl.eps.softarch.demo.domain.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface BasketItemRepository extends CrudRepository<BasketItem, Long>, PagingAndSortingRepository<BasketItem, Long> {
    
    List<BasketItem> findByBasket(Basket basket);
    Optional<BasketItem> findByBasketAndProduct(Basket basket, Product product);
}
