package se.callista.blog.service.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import se.callista.blog.service.domain.entity.Product;

import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long> {

    // Hibernate GOTCHA: Filters apply to entity queries, but not to direct fetching.
    // The implementation of findById() for e.g SimpleJpaRepository in Spring uses
    // em.find under the hood, and therefore the request is by default not filtered.
    // Hence we must force a query to be generated, in order for the filtering to work.
    @Query("SELECT p from Product p WHERE p.id = :id")
    Optional<Product> findById(long id);

}