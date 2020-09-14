package se.callista.blog.service.repository;

import org.springframework.data.repository.CrudRepository;
import se.callista.blog.service.domain.entity.Product;

public interface ProductRepository extends CrudRepository<Product, Long> {

}