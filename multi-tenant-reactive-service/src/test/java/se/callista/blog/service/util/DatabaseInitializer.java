package se.callista.blog.service.util;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Component;
import se.callista.blog.service.multi_tenancy.config.shard.liquibase.DynamicShardingMultiTenantSpringLiquibase;

@Component
public class DatabaseInitializer {

    @Autowired
    @Qualifier("masterDataSource")
    private DataSource masterDataSource;

    @Autowired
    DynamicShardingMultiTenantSpringLiquibase shardSpringLiquibase;

    @Value("${multitenancy.master.datasource.username}")
    private String username;

    @Value("${multitenancy.master.datasource.password}")
    private String password;

    private static boolean initialized = false;

    public void ensureInitialized() throws Exception {
        if (!initialized) {
            String dbHost = System.getProperty("DB_HOST");
            String dbName = System.getProperty("DB_NAME");
            JdbcTemplate jdbcTemplate = new JdbcTemplate(masterDataSource);
            jdbcTemplate.execute("CREATE DATABASE " + dbName + "_shard_1");
            jdbcTemplate.execute("CREATE DATABASE " + dbName + "_shard_2");
            jdbcTemplate.execute("GRANT ALL PRIVILEGES ON DATABASE " + dbName + "_shard_1 TO " + username);
            jdbcTemplate.execute("GRANT ALL PRIVILEGES ON DATABASE " + dbName + "_shard_2 TO " + username);
            shardSpringLiquibase.afterPropertiesSet();
            Connection connection = DriverManager.getConnection("jdbc:postgresql://" + dbHost + "/" + dbName + "_shard_1", username, password);
            DataSource tenantDataSource = new SingleConnectionDataSource(connection, false);
            try {
                jdbcTemplate = new JdbcTemplate(tenantDataSource);
                jdbcTemplate.execute("insert into product(id, name, tenant_id) values (1, 'Product 1', 'tenant1');");
                jdbcTemplate.execute("insert into product(id, name, tenant_id) values (2, 'Product 2', 'tenant2');");
            } finally {
                connection.close();
            }
            connection = DriverManager.getConnection("jdbc:postgresql://" + dbHost + "/" + dbName + "_shard_2", username, password);
            tenantDataSource = new SingleConnectionDataSource(connection, false);
            try {
                jdbcTemplate = new JdbcTemplate(tenantDataSource);
                jdbcTemplate.execute("insert into product(id, name, tenant_id) values (3, 'Product 3', 'tenant3');");
            } finally {
                connection.close();
            }
            initialized = true;
        }
    }

}