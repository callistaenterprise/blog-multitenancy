package se.callista.blog.service.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import se.callista.blog.service.model.ProductValue;
import se.callista.blog.service.multitenancy.interceptor.TenantInterceptor;
import se.callista.blog.service.services.ProductService;

@WebMvcTest(ProductApiController.class)
@Import(TenantInterceptor.class)
class ProductApiControllerTest {

    @Autowired
    private MockMvc mvc;
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

        given(productService.getProduct(product.getProductId())).willReturn(product);

        mvc.perform(MockMvcRequestBuilders.get("/products/" + product.getProductId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(product.getName())));

        verify(productService).getProduct(product.getProductId());
    }

    @Test
    void asyncGetProduct() throws Exception {

        ProductValue product = ProductValue.builder()
                .productId(1L)
                .name("Product Name")
                .build();

        given(productService.getProduct(product.getProductId())).willReturn(product);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/async/products/" + product.getProductId()))
                .andExpect(request().asyncStarted())
                .andReturn();

        mvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(product.getName())));

        verify(productService).getProduct(product.getProductId());
    }

}