package se.callista.blog.service.multi_tenancy.config.tenant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;

@Slf4j
@Lazy(false)
@Configuration
@EnableConfigurationProperties(LiquibaseProperties.class)
public class TenantLiquibaseConfig {

    @Value("${multitenancy.tenant.liquibase.changeLog}")
    private String tenantLiquibaseChangeLog;
    @Value("${multitenancy.tenant.liquibase.contexts:}")
    private String tenantLiquibaseContexts;

}