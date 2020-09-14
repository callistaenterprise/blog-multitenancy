package se.callista.blog.tenant_management.service;

import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import se.callista.blog.tenant_management.annotation.SpringBootDbIntegrationTest;
import se.callista.blog.tenant_management.persistence.PostgresqlTestContainer;

@Testcontainers
@SpringBootDbIntegrationTest
class TenantManagementServiceTest {

    @Container
    private static final PostgresqlTestContainer POSTGRESQL_CONTAINER = PostgresqlTestContainer.getInstance();

    @Autowired
    private TenantManagementService tenantManagementService;

    @Test
    @ExpectedDataSet({"service/tenants.yml", "service/products.yml"})
    void createTenant() {
        tenantManagementService.createTenant("tenant1", "tenant1_schema");
    }
}