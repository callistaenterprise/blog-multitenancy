package se.callista.blog.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, LiquibaseAutoConfiguration.class })
@EnableTransactionManagement
@EnableAsync
public class MultiTenantServiceApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(MultiTenantServiceApplication.class, args);
    }

}

