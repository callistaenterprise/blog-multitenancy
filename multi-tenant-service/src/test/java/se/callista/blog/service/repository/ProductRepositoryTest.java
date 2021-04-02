package se.callista.blog.service.repository;

import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import se.callista.blog.service.annotation.SpringBootDbIntegrationTest;
import se.callista.blog.service.domain.entity.Product;
import se.callista.blog.service.multi_tenancy.config.tenant.liquibase.DynamicSchemaBasedMultiTenantSpringLiquibase;
import se.callista.blog.service.multi_tenancy.util.TenantContext;
import se.callista.blog.service.persistence.PostgresqlTestContainer;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootDbIntegrationTest
class ProductRepositoryTest {

    @Container
    private static final PostgresqlTestContainer POSTGRESQL_CONTAINER = PostgresqlTestContainer.getInstance();

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    DynamicSchemaBasedMultiTenantSpringLiquibase tenantSpringLiquibase;

    @BeforeEach
    @DataSet(value = {"tenants.yml"}, executeStatementsBefore = {
            "CREATE SCHEMA IF NOT EXISTS tenant1_schema;",
            "CREATE SCHEMA IF NOT EXISTS tenant2_schema;"
    })
    public void setUpSchemas() throws Exception {
        tenantSpringLiquibase.afterPropertiesSet();
    }

    @AfterEach
    @DataSet(executeStatementsBefore = {
            "DROP SCHEMA tenant1_schema cascade;",
            "DROP SCHEMA tenant2_schema cascade;"
    })
    public void tearDownSchemas() throws Exception {
    }

    @Test
    @DataSet(value = {"products.yml"})
    public void findByIdForTenant1() {

        TenantContext.setTenantId("tenant1");
        Optional<Product> product = productRepository.findById(1L);
        assertThat(product).isPresent();
        assertThat(product.get().getName()).isEqualTo("Product 1");
        TenantContext.clear();

    }

    @Test
    @DataSet(value = {"products.yml"})
    public void findByIdForTenant2() {

        TenantContext.setTenantId("tenant2");
        assertThat(productRepository.findById(1L)).isNotPresent();
        TenantContext.clear();

    }

}