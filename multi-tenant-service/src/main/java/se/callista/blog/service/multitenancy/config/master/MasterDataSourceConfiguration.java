package se.callista.blog.service.multitenancy.config.master;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MasterDataSourceConfiguration {

    @Bean
    @ConfigurationProperties("multitenancy.master.datasource")
    public DataSourceProperties masterDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @LiquibaseDataSource
    @ConfigurationProperties("multitenancy.master.datasource.hikari")
    public DataSource masterDataSource() {
        HikariDataSource dataSource = masterDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        dataSource.setPoolName("masterDataSource");
        return dataSource;
    }

}