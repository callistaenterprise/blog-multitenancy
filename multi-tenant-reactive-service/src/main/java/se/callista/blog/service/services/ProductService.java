package se.callista.blog.service.services;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.callista.blog.service.model.ProductValue;

public interface ProductService {

    Flux<ProductValue> getProducts();

    Mono<ProductValue> getProduct(long productId);

    Mono<ProductValue> createProduct(ProductValue productValue);

    Mono<ProductValue> updateProduct(ProductValue productValue);

    Mono<Void> deleteProductById(long productId);
}
