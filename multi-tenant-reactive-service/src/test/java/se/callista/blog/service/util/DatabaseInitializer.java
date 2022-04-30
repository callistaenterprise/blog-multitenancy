package se.callista.blog.service.util;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Component;
import se.callista.blog.service.multitenancy.config.tenant.liquibase.DynamicDataSourceBasedMultiTenantSpringLiquibase;

@Component
public class DatabaseInitializer {

    @Autowired
    @Qualifier("masterDataSource")
    private DataSource masterDataSource;

    @Autowired
    DynamicDataSourceBasedMultiTenantSpringLiquibase tenantSpringLiquibase;

    private static boolean initialized = false;

    public void ensureInitialized() throws Exception {
        if (!initialized) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(masterDataSource);
            jdbcTemplate.execute("CREATE DATABASE tenant1_db");
            jdbcTemplate.execute("CREATE DATABASE tenant2_db");
            jdbcTemplate.execute("CREATE USER tenant1_db WITH ENCRYPTED PASSWORD 'secret'");
            jdbcTemplate.execute("CREATE USER tenant2_db WITH ENCRYPTED PASSWORD 'secret';");
            jdbcTemplate.execute("GRANT ALL PRIVILEGES ON DATABASE tenant1_db TO tenant1_db");
            jdbcTemplate.execute("GRANT ALL PRIVILEGES ON DATABASE tenant2_db TO tenant2_db");
            tenantSpringLiquibase.afterPropertiesSet();
            String dbHost = System.getProperty("DB_HOST");
            Connection connection = DriverManager.getConnection("jdbc:postgresql://" + dbHost + "/tenant1_db", "tenant1_db", "secret");
            DataSource tenantDataSource = new SingleConnectionDataSource(connection, false);
            jdbcTemplate = new JdbcTemplate(tenantDataSource);
            jdbcTemplate.execute("insert into product(id, name) values (1, 'Product 1');");
            jdbcTemplate.execute("insert into product(id, name) values (2, 'Product 2');");
            connection.close();
            initialized = true;
        }
    }

}