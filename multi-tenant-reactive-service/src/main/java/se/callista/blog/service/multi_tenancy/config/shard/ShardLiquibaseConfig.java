package se.callista.blog.service.multi_tenancy.config.shard;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.callista.blog.service.multi_tenancy.config.shard.liquibase.DynamicShardingMultiTenantSpringLiquibase;

@Configuration
@ConditionalOnProperty(name = "multitenancy.shard.liquibase.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(LiquibaseProperties.class)
public class ShardLiquibaseConfig {

    @Bean
    @ConfigurationProperties("multitenancy.shard.liquibase")
    public LiquibaseProperties shardLiquibaseProperties() {
        return new LiquibaseProperties();
    }

    @Bean
    public DynamicShardingMultiTenantSpringLiquibase shardLiquibase() {
        return new DynamicShardingMultiTenantSpringLiquibase();
    }

}
