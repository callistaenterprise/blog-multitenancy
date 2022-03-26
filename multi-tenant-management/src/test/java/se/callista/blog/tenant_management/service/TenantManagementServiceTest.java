package se.callista.blog.tenant_management.service;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import se.callista.blog.tenant_management.annotation.SpringBootDbIntegrationTest;
import se.callista.blog.tenant_management.persistence.PostgresqlTestContainer;

@Testcontainers
@SpringBootDbIntegrationTest
class TenantManagementServiceTest {

    @Container
    private static final PostgresqlTestContainer POSTGRESQL_CONTAINER = PostgresqlTestContainer.getInstance();

    @Autowired // Autowired to force eager loading
    private SpringLiquibase springLiquibase;

    @Autowired
    private TenantManagementService tenantManagementService;

    @Value("${multitenancy.master.datasource.username}")
    private String username;
    @Value("${multitenancy.master.datasource.password}")
    private String password;

    @Test
    @DataSet({"service/empty.yml"})
    @ExpectedDataSet({"service/shards.yml", "service/tenants.yml"})
    void createTenant1() throws SQLException {
        String dbName = System.getProperty("DB_NAME");
        String dbHost = System.getProperty("DB_HOST");
        tenantManagementService.createTenant("tenant1");
        tenantManagementService.createTenant("tenant2");
        tenantManagementService.createTenant("tenant3");
        Connection connection = DriverManager.getConnection("jdbc:postgresql://" + dbHost + "/" + dbName + "_shard_1", username, password);
        DataSource tenantDataSource = new SingleConnectionDataSource(connection, false);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(tenantDataSource);
        int count = jdbcTemplate.queryForObject("select count(*) from product", Integer.class);
        Assert.assertEquals(0, count);
    }
}