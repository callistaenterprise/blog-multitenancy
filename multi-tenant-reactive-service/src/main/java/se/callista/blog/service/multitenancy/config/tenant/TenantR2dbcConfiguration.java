package se.callista.blog.service.multitenancy.config.tenant;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.r2dbc.connection.lookup.AbstractRoutingConnectionFactory;
import org.springframework.transaction.ReactiveTransactionManager;
import se.callista.blog.service.multitenancy.connection.TenantAwareConnectionFactory;
import se.callista.blog.service.multitenancy.connection.TenantAwareConnectionFactoryLookup;

@Configuration
public class TenantR2dbcConfiguration extends AbstractR2dbcConfiguration {

    @Bean
    @ConfigurationProperties("spring.r2dbc")
//    @Autowired
//    @Lazy
    public R2dbcProperties r2dbcProperties() {
        return new R2dbcProperties();
    }

    @Autowired
    @Lazy
    private TenantAwareConnectionFactoryLookup connectionFactoryLookup;

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        AbstractRoutingConnectionFactory connectionFactory = new TenantAwareConnectionFactory(connectionFactoryLookup);

        connectionFactory.afterPropertiesSet();

        return connectionFactory;
    }

    @Bean
    @Primary
    ReactiveTransactionManager connectionFactoryTransactionManager() {
        return new R2dbcTransactionManager(connectionFactory());
    }
}
