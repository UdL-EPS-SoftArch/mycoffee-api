package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CustomerRepository extends CrudRepository<Customer, String>, PagingAndSortingRepository<Customer, String> {

    List<Customer> findByPhoneNumber(String phone);
    List<Customer> findByName(String name);

    boolean existsByName(String name);
    boolean existsByPhoneNumber(String phoneNumber);

}
