package se.callista.blog.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class MultiTenantReactiveServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultiTenantReactiveServiceApplication.class, args);
    }

}

