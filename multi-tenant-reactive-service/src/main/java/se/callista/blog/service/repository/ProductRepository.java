package se.callista.blog.service.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import se.callista.blog.service.domain.entity.Product;

public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {

}