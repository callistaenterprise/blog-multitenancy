package se.callista.blog.service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.callista.blog.service.domain.entity.Product;
import se.callista.blog.service.model.ProductValue;
import se.callista.blog.service.repository.ProductRepository;

@RequiredArgsConstructor
@Component
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public Flux<ProductValue> getProducts() {
        return productRepository.findAll()
                .map(ProductValue::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ProductValue> getProduct(long productId) {
        return productRepository.findById(productId)
                .map(ProductValue::fromEntity);
    }

    @Override
    @Transactional
    public Mono<ProductValue> createProduct(ProductValue productValue) {
        Product product = Product.builder()
                .name(productValue.getName())
                .build();
        return productRepository.save(product)
            .map(ProductValue::fromEntity);
    }

    @Override
    @Transactional
    public Mono<ProductValue> updateProduct(ProductValue productValue) {
        return productRepository.findById(productValue.getProductId())
            .flatMap(p -> {
                p.setName(productValue.getName());
                return productRepository.save(p);
            })
            .map(ProductValue::fromEntity);
    }

    @Override
    @Transactional
    public Mono<Void> deleteProductById(long productId) {
        return productRepository.deleteById(productId);
    }
}
