package se.callista.blog.service.controller;

import java.net.URI;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.callista.blog.service.model.ProductValue;
import se.callista.blog.service.services.ProductService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/")
public class ProductApiController {

    private final ProductService productService;

    @GetMapping(value = "/products", produces = {ContentType.PRODUCTS_1_0})
    public Flux<ProductValue> getProducts() {
        return productService.getProducts();
    }

    @GetMapping(value = "/products/{productId}", produces = {ContentType.PRODUCT_1_0})
    public Mono<ProductValue> getProduct(@PathVariable("productId") long productId) {
        return productService.getProduct(productId)
            .switchIfEmpty(Mono.error(new NotFoundException("No such product")));
    }

    @PostMapping(value = "/products",
                 consumes = {ContentType.PRODUCT_1_0},
                 produces = {ContentType.PRODUCT_1_0})
    public Mono<ResponseEntity<ProductValue>> createProduct(@Valid @RequestBody ProductValue productValue) {
        return productService.createProduct(productValue)
            .map(product -> ResponseEntity.created(URI.create("/products/" + product.getProductId()))
                .body(product));
    }

    @PutMapping(value = "/products/{productId}",
                consumes = {ContentType.PRODUCT_1_0},
                produces = {ContentType.PRODUCT_1_0})
    Mono<ProductValue> updateProduct(@PathVariable long productId, @Valid @RequestBody ProductValue productValue) {
        productValue.setProductId(productId);
        return productService.updateProduct(productValue)
            .switchIfEmpty(Mono.error(new NotFoundException("No such product")));
    }

    @DeleteMapping("/products/{productId}")
    Mono<Void> deleteProduct(@PathVariable long productId) {
        return productService.deleteProductById(productId)
            .switchIfEmpty(Mono.error(new NotFoundException("No such product")));
    }

}
