package se.callista.blog.service.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import se.callista.blog.service.model.ProductValue;
import se.callista.blog.service.services.ProductService;

@WebFluxTest(ProductApiController.class)
class ProductApiControllerTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    void getProduct() throws Exception {

        ProductValue product = ProductValue.builder()
                .productId(1L)
                .name("Product Name")
                .build();

        given(productService.getProduct(product.getProductId())).willReturn(Mono.just(product));

        webClient.get().uri("/products/" + product.getProductId()).exchange()
            .expectStatus().isOk()
            .expectBody(ProductValue.class).equals(product);

        verify(productService).getProduct(product.getProductId());
    }

}