package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.Basket;
import cat.udl.eps.softarch.demo.domain.Business;
import cat.udl.eps.softarch.demo.domain.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


import java.util.List;

@RepositoryRestResource
public interface BusinessRepository extends CrudRepository<Business, Long>, PagingAndSortingRepository<Business, Long> {
    List<Business> findById(Business business);

}
