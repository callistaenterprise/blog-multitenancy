package se.callista.blog.service.repository;

import static org.assertj.core.api.Assertions.assertThat;

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

        Product product = TenantContext.withTenantId("tenant1",
                productRepository.findById(1L))
                .block();
        assertThat(product.getName()).isEqualTo("Product 1");

    }

    @Test
    @DataSet(value = {"products.yml"})
    public void findByIdForTenant2() {

        Product product = TenantContext.withTenantId("tenant2",
                productRepository.findById(1L))
            .block();
        assertThat(product).isNull();

    }

}