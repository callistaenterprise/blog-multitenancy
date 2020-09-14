package se.callista.blog.tenant_management.service;

import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import se.callista.blog.tenant_management.persistence.PostgresqlTestContainer;
import se.callista.blog.tenant_management.annotation.SpringBootDbIntegrationTest;

@Testcontainers
@SpringBootDbIntegrationTest
class TenantManagementServiceTest {

    @Container
    private static final PostgresqlTestContainer POSTGRESQL_CONTAINER = PostgresqlTestContainer.getInstance();

    @Autowired
    private TenantManagementService tenantManagementService;

}