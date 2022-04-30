package se.callista.blog.service.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.callista.blog.service.model.ProductValue;
import se.callista.blog.service.services.ProductService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/")
public class ProductApiController {

    private final ProductService productService;

    @GetMapping(value = "/products", produces = {ContentType.PRODUCTS_1_0})
    public ResponseEntity<List<ProductValue>> getProducts() {
        List<ProductValue> productValues = productService.getProducts();
        return new ResponseEntity<>(productValues, HttpStatus.OK);
    }

    @GetMapping(value = "/products/{productId}", produces = {ContentType.PRODUCT_1_0})
    public ResponseEntity<ProductValue> getProduct(@PathVariable("productId") long productId) {
        try {
            ProductValue branch = productService.getProduct(productId);
            return new ResponseEntity<>(branch, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @PostMapping(value = "/products",
                 consumes = {ContentType.PRODUCT_1_0},
                 produces = {ContentType.PRODUCT_1_0})
    public ResponseEntity<ProductValue> createProduct(@Valid @RequestBody ProductValue productValue) {
        ProductValue product = productService.createProduct(productValue);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.LOCATION, "/products/" + product.getProductId());
        return new ResponseEntity<>(product, headers, HttpStatus.CREATED);
    }

    @PutMapping(value = "/products/{productId}",
                consumes = {ContentType.PRODUCT_1_0},
                produces = {ContentType.PRODUCT_1_0})
    ResponseEntity<ProductValue> updateProduct(@PathVariable long productId, @Valid @RequestBody ProductValue productValue) {
        productValue.setProductId(productId);
        try {
            ProductValue product = productService.updateProduct(productValue);
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @DeleteMapping("/products/{productId}")
    ResponseEntity<Void> deleteProduct(@PathVariable long productId) {
        try {
            productService.deleteProductById(productId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @GetMapping(value = "/async/products", produces = {ContentType.PRODUCTS_1_0})
    public CompletableFuture<ResponseEntity<List<ProductValue>>> asyncGetProducts() {
        List<ProductValue> productValues = productService.getProducts();
        return CompletableFuture.completedFuture(new ResponseEntity<>(productValues, HttpStatus.OK));
    }

    @GetMapping(value = "/async/products/{productId}", produces = {ContentType.PRODUCT_1_0})
    public CompletableFuture<ResponseEntity<ProductValue>> asyncGetProduct(@PathVariable("productId") long productId) {
        return CompletableFuture.completedFuture(getProduct(productId));
    }

}
