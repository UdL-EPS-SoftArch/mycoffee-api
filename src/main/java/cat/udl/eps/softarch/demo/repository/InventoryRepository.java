package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.Business;
import cat.udl.eps.softarch.demo.domain.Inventory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;

@RepositoryRestResource
public interface InventoryRepository extends CrudRepository<Inventory, Long>, PagingAndSortingRepository<Inventory, Long> {
  List<Inventory> findByBusiness(@Param("business") Business business);
}

