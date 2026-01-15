package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.Customer;
import cat.udl.eps.softarch.demo.domain.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PostAuthorize;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface OrderRepository extends CrudRepository<Order, Long>, PagingAndSortingRepository<Order, Long> {
    @PostAuthorize("hasRole('ADMIN') OR returnObject.empty OR " +
            "returnObject.get().customer.username == principal.username")
    Optional<Order> findById(@Param("id") Long id);
    List<Order> findByCustomer(@Param("customer") Customer customer);
}
