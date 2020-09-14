package se.callista.blog.tenant_management.repository;

import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import se.callista.blog.tenant_management.annotation.SpringBootDbIntegrationTest;
import se.callista.blog.tenant_management.domain.entity.Tenant;
import se.callista.blog.tenant_management.persistence.PostgresqlTestContainer;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootDbIntegrationTest
class TenantRepositoryTest {

    @Container
    private static final PostgresqlTestContainer POSTGRESQL_CONTAINER = PostgresqlTestContainer.getInstance();

    @Autowired
    private TenantRepository tenantRepository;

    @Test
    @DataSet(value = {"repository/tenants.yml"})
    public void findById() throws Exception {

        Optional<Tenant> tenant = tenantRepository.findById("tenant1");
        assertThat(tenant).isPresent();
        assertThat(tenant.get().getUrl()).endsWith("/tenant1_db");

    }

}