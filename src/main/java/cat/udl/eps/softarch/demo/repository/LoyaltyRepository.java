package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.Loyalty;
import cat.udl.eps.softarch.demo.domain.Customer;
import cat.udl.eps.softarch.demo.domain.Business;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;

@RepositoryRestResource
public interface LoyaltyRepository extends PagingAndSortingRepository<Loyalty, Long>, CrudRepository<Loyalty, Long> {
    List<Loyalty> findByCustomer(Customer customer);
    List<Loyalty> findByAccumulatedPointsGreaterThanEqual(Integer points);
    List<Loyalty> findByCustomerOrderByStartDateDesc(Customer customer);
    List<Loyalty> findByCustomerAndBusiness(Customer customer, Business business);
    List<Loyalty> findByBusiness(Business business);
}