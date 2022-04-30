package se.callista.blog.management.service;

import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import se.callista.blog.management.annotation.SpringBootDbIntegrationTest;
import se.callista.blog.management.persistence.PostgresqlTestContainer;

@Testcontainers
@SpringBootDbIntegrationTest
class TenantManagementServiceTest {

    @Container
    private static final PostgresqlTestContainer POSTGRESQL_CONTAINER = PostgresqlTestContainer.getInstance();

    @Autowired
    private TenantManagementService tenantManagementService;

    @Test
    @ExpectedDataSet({"service/tenants.yml"})
    void createTenant1() throws SQLException {
        String dbHost = System.getProperty("DB_HOST");
        tenantManagementService.createTenant("tenant1", "tenant1_db", "secret");
        Connection connection = DriverManager.getConnection("jdbc:postgresql://" + dbHost + "/tenant1_db", "tenant1_db", "secret");
        DataSource tenantDataSource = new SingleConnectionDataSource(connection, false);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(tenantDataSource);
        int count = jdbcTemplate.queryForObject("select count(*) from product", Integer.class);
        Assert.assertEquals(0, count);
    }
}