package cat.udl.eps.softarch.demo.repository;


import cat.udl.eps.softarch.demo.domain.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface CategoryRepository extends CrudRepository<Category, Long>, PagingAndSortingRepository<Category, Long> {

    Optional<Category> findByName(String name);
    List<Category> findByDescription(String description);

}
