package se.callista.blog.service.multi_tenancy.config.shard;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.r2dbc.connection.lookup.AbstractRoutingConnectionFactory;
import org.springframework.transaction.ReactiveTransactionManager;
import se.callista.blog.service.multi_tenancy.connection.TenantAwareConnectionFactoryLookup;
import se.callista.blog.service.multi_tenancy.connection.TenantAwareRoutingConnectionFactory;

@Configuration
public class ShardR2dbcConfiguration extends AbstractR2dbcConfiguration {

    @Autowired
    @Lazy
    private TenantAwareConnectionFactoryLookup connectionFactoryLookup;

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        AbstractRoutingConnectionFactory connectionFactory = new TenantAwareRoutingConnectionFactory(connectionFactoryLookup);

        connectionFactory.afterPropertiesSet();

        return connectionFactory;
    }

    @Bean
    @Primary
    ReactiveTransactionManager connectionFactoryTransactionManager() {
        return new R2dbcTransactionManager(connectionFactory());
    }
}
