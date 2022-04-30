package se.callista.blog.service.multitenancy.connection;

import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryMetadata;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.connection.lookup.AbstractRoutingConnectionFactory;
import org.springframework.r2dbc.connection.lookup.ConnectionFactoryLookup;
import reactor.core.publisher.Mono;
import se.callista.blog.service.multitenancy.util.TenantContext;

@RequiredArgsConstructor
@Slf4j
public class TenantAwareConnectionFactory extends AbstractRoutingConnectionFactory {

    // Copy of io.r2dbc.postgresql.PostgresqlConnectionFactoryMetadata, which is not public
    static final class PostgresqlConnectionFactoryMetadata implements ConnectionFactoryMetadata {

        static final PostgresqlConnectionFactoryMetadata INSTANCE = new PostgresqlConnectionFactoryMetadata();

        public static final String NAME = "PostgreSQL";

        private PostgresqlConnectionFactoryMetadata() {
        }

        @Override
        public String getName() {
            return NAME;
        }
    }

    public TenantAwareConnectionFactory(ConnectionFactoryLookup connectionFactoryLookup) {
        super.setConnectionFactoryLookup(connectionFactoryLookup);
        super.setTargetConnectionFactories(Collections.emptyMap());
        super.setLenientFallback(false);
    }

    @Override
    protected Mono<ConnectionFactory> determineTargetConnectionFactory() {
        return determineCurrentLookupKey()
            .map(tenantId -> resolveSpecifiedConnectionFactory(tenantId));
    }

    @Override
    protected Mono<Object> determineCurrentLookupKey() {
        return TenantContext.getTenantId()
            .switchIfEmpty(Mono.defer(() ->
                Mono.error(new RuntimeException("No tenantId set in TenantContext"))));
    }

    @Override
    public ConnectionFactoryMetadata getMetadata() {
        return PostgresqlConnectionFactoryMetadata.INSTANCE;
    }
}