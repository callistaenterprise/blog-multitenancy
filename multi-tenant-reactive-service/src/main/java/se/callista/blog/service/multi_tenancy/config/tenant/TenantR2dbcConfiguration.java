package se.callista.blog.service.multi_tenancy.config.tenant;

import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.util.StringUtils;
import se.callista.blog.service.multi_tenancy.connection.TenantAwareConnectionFactory;
import se.callista.blog.service.multi_tenancy.repository.TenantRepository;

@Configuration
public class TenantR2dbcConfiguration extends AbstractR2dbcConfiguration {

    @Autowired
    @Lazy
    private TenantRepository tenantRepository;

    @Bean
    @ConfigurationProperties("multitenancy.tenant.r2dbc")
    public R2dbcProperties r2dbcProperties() {
        return new R2dbcProperties();
    }

    @Bean
    @Override
    public ConnectionFactory connectionFactory() {
        ConnectionFactory connectionFactory =  ConnectionFactoryBuilder
            .withOptions(getConnectionFactoryOptions(r2dbcProperties()))
            .build();
        return new TenantAwareConnectionFactory(connectionFactory, tenantRepository);
    }

    // Implementation taken from non-public class
    // org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryOptionsInitializer
    private ConnectionFactoryOptions.Builder getConnectionFactoryOptions(R2dbcProperties properties) {
        ConnectionFactoryOptions urlOptions = ConnectionFactoryOptions.parse(properties.getUrl());
        ConnectionFactoryOptions.Builder optionsBuilder = urlOptions.mutate();
        if (StringUtils.hasText(properties.getUsername())) {
            optionsBuilder.option(ConnectionFactoryOptions.USER, properties.getUsername());
        }
        if (StringUtils.hasText(properties.getPassword())) {
            optionsBuilder.option(ConnectionFactoryOptions.PASSWORD, properties.getPassword());
        }
        if (properties.isGenerateUniqueName()) {
            optionsBuilder.option(ConnectionFactoryOptions.DATABASE, properties.determineUniqueName());
        } else if (StringUtils.hasText(properties.getName())) {
            optionsBuilder.option(ConnectionFactoryOptions.DATABASE, properties.getName());
        }
        if (properties.getProperties() != null) {
            properties.getProperties().forEach((key, value) -> optionsBuilder.option(Option.valueOf(key), value));
        }
        return optionsBuilder;
    }

    @Bean
    @Primary
    ReactiveTransactionManager connectionFactoryTransactionManager() {
        return new R2dbcTransactionManager(connectionFactory());
    }

}
