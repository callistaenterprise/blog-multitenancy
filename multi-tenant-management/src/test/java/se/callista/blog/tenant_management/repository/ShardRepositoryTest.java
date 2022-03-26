package se.callista.blog.tenant_management.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.database.rider.core.api.dataset.DataSet;
import java.util.Optional;
import liquibase.integration.spring.SpringLiquibase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import se.callista.blog.tenant_management.annotation.SpringBootDbIntegrationTest;
import se.callista.blog.tenant_management.domain.entity.Shard;
import se.callista.blog.tenant_management.persistence.PostgresqlTestContainer;

@Testcontainers
@SpringBootDbIntegrationTest
class ShardRepositoryTest {

    @Container
    private static final PostgresqlTestContainer POSTGRESQL_CONTAINER = PostgresqlTestContainer.getInstance();

    @Autowired // Autowired to force eager loading
    private SpringLiquibase springLiquibase;

    @Autowired
    private ShardRepository shardRepository;

    @Test
    @DataSet(value = {"repository/shards.yml", "repository/tenants.yml"})
    public void findById() throws Exception {

        Optional<Shard> shard = shardRepository.findById(1);
        assertThat(shard).isPresent();
        assertThat(shard.get().getUrl()).endsWith("shard_1");

    }

}