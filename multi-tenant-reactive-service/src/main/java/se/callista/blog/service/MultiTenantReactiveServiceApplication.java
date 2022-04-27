package se.callista.blog.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, LiquibaseAutoConfiguration.class, R2dbcAutoConfiguration.class})
@EnableTransactionManagement
public class MultiTenantReactiveServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultiTenantReactiveServiceApplication.class, args);
    }

}

