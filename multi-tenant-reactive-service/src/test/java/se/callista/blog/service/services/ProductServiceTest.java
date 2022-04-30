package se.callista.blog.service.services;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import se.callista.blog.service.annotation.SpringBootDbIntegrationTest;
import se.callista.blog.service.model.ProductValue;
import se.callista.blog.service.multitenancy.util.TenantContext;
import se.callista.blog.service.persistence.PostgresqlTestContainer;
import se.callista.blog.service.util.DatabaseInitializer;

@Testcontainers
@SpringBootDbIntegrationTest
public class ProductServiceTest {

    @Container
    private static final PostgresqlTestContainer POSTGRESQL_CONTAINER = PostgresqlTestContainer.getInstance();

    @Autowired
    private DatabaseInitializer databaseInitializer;

    @Autowired
    private ProductService productService;

    @BeforeEach
    @DataSet(value = {"tenants.yml"})
    public void initialize() throws Exception {
        databaseInitializer.ensureInitialized();
    }

    @Test
    @DataSet(value = {"products.yml"})
    public void getProductForTenant1() {

        ProductValue product = TenantContext.withTenantId("tenant1",
            productService.getProduct(1))
            .block();
        assertThat(product.getName()).isEqualTo("Product 1");

    }

    @Test
    @DataSet(value = {"products.yml"})
    public void getProductForTenant2() {

        ProductValue product = TenantContext.withTenantId("tenant2",
                productService.getProduct(1))
            .block();
        assertThat(product).isNull();

    }

}
