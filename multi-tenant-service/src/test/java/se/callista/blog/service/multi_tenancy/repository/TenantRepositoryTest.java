package se.callista.blog.service.multi_tenancy.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.database.rider.core.api.dataset.DataSet;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import se.callista.blog.service.annotation.SpringBootDbIntegrationTest;
import se.callista.blog.service.multi_tenancy.domain.entity.Shard;
import se.callista.blog.service.multi_tenancy.domain.entity.Tenant;
import se.callista.blog.service.persistence.PostgresqlTestContainer;

@Testcontainers
@SpringBootDbIntegrationTest
class TenantRepositoryTest {

    @Container
    private static final PostgresqlTestContainer POSTGRESQL_CONTAINER = PostgresqlTestContainer.getInstance();

    @Autowired
    private TenantRepository tenantRepository;

    @Test
    @DataSet(value = {"repository/shards.yml, repository/tenants.yml"})
    public void findById() throws Exception {

        Optional<Tenant> tenant = tenantRepository.findByTenantId("tenant1");
        assertThat(tenant).isPresent();
        Shard shard = tenant.get().getShard();
        assertThat(shard.getId()).isEqualTo(1);
    }

}