package se.callista.blog.service.services;


import java.util.List;
import se.callista.blog.service.model.ProductValue;

public interface ProductService {

    List<ProductValue> getProducts();

    ProductValue getProduct(long productId);

    ProductValue createProduct(ProductValue productValue);

    ProductValue updateProduct(ProductValue productValue);

    void deleteProductById(long productId);
}
