package cat.udl.eps.softarch.demo.repository;

import java.time.ZonedDateTime;
import java.util.List;
import cat.udl.eps.softarch.demo.domain.Basket;
import cat.udl.eps.softarch.demo.domain.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BasketRepository extends CrudRepository<Basket, Long>, PagingAndSortingRepository<Basket, Long> {

    List<Basket> findByCustomer(Customer customer);
    List<Basket> findByCreatedAt(ZonedDateTime createdAt);
    List<Basket> findByUpdatedAt(ZonedDateTime updatedAt);

}
