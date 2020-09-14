package se.callista.blog.service.multi_tenancy.config.tenant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import se.callista.blog.service.multi_tenancy.config.tenant.liquibase.DynamicDataSourceBasedMultiTenantSpringLiquibase;

@Slf4j
@Lazy(false)
@Configuration
@EnableConfigurationProperties(LiquibaseProperties.class)
public class TenantLiquibaseConfig {

    @Value("${multitenancy.tenant.liquibase.changeLog}")
    private String tenantLiquibaseChangeLog;
    @Value("${multitenancy.tenant.liquibase.contexts:}")
    private String tenantLiquibaseContexts;

    @Bean
    @DependsOn("liquibase")
    public DynamicDataSourceBasedMultiTenantSpringLiquibase dynamicDataSourceBasedMultiTenantSpringLiquibase(
            @Qualifier("masterLiquibaseProperties")
            LiquibaseProperties liquibaseProperties) {
        DynamicDataSourceBasedMultiTenantSpringLiquibase liquibase = new DynamicDataSourceBasedMultiTenantSpringLiquibase();
        liquibase.setChangeLog(tenantLiquibaseChangeLog);
        liquibase.setContexts(tenantLiquibaseContexts);
        liquibase.setDropFirst(liquibaseProperties.isDropFirst());
        liquibase.setShouldRun(liquibaseProperties.isEnabled());
        return liquibase;
    }

}