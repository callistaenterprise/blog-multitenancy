package se.callista.blog.service.multitenancy.config.tenant;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import se.callista.blog.service.multitenancy.config.tenant.liquibase.DynamicDataSourceBasedMultiTenantSpringLiquibase;

@Lazy(false)
@Configuration
@ConditionalOnProperty(name = "multitenancy.tenant.liquibase.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(LiquibaseProperties.class)
public class TenantLiquibaseConfig {

    @Bean
    @ConfigurationProperties("multitenancy.tenant.liquibase")
    public LiquibaseProperties tenantLiquibaseProperties() {
        return new LiquibaseProperties();
    }

    @Bean
    public DynamicDataSourceBasedMultiTenantSpringLiquibase tenantLiquibase() {
        return new DynamicDataSourceBasedMultiTenantSpringLiquibase();
    }

}
