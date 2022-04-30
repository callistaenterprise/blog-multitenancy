package se.callista.blog.management.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.database.rider.core.api.dataset.DataSet;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import se.callista.blog.management.annotation.SpringBootDbIntegrationTest;
import se.callista.blog.management.domain.entity.Tenant;
import se.callista.blog.management.persistence.PostgresqlTestContainer;

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
        assertThat(tenant.get().getDb()).isEqualTo("tenant1_db");

    }

}