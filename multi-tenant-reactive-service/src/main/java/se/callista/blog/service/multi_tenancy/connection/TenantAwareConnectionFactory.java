package se.callista.blog.service.multi_tenancy.connection;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryMetadata;
import io.r2dbc.spi.Statement;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.r2dbc.connection.lookup.ConnectionFactoryLookupFailureException;
import reactor.core.publisher.Mono;
import se.callista.blog.service.multi_tenancy.domain.entity.Tenant;
import se.callista.blog.service.multi_tenancy.repository.TenantRepository;
import se.callista.blog.service.multi_tenancy.util.TenantContext;

@RequiredArgsConstructor
@Slf4j
public class TenantAwareConnectionFactory implements ConnectionFactory {

    @Value("${multitenancy.schema-cache.maximumSize:100}")
    private Long maximumSize;

    @Value("${multitenancy.schema-cache.expireAfterAccess:10}")
    private Integer expireAfterAccess;

    private final ConnectionFactory connectionFactory;

    private final TenantRepository tenantRepository;

    private LoadingCache<String, String> tenantSchemas;

    @PostConstruct
    private void createCache() {
        Caffeine<Object, Object> tenantsCacheBuilder = Caffeine.newBuilder();
        if (maximumSize != null) {
            tenantsCacheBuilder.maximumSize(maximumSize);
        }
        if (expireAfterAccess != null) {
            tenantsCacheBuilder.expireAfterAccess(expireAfterAccess, TimeUnit.MINUTES);
        }
        tenantSchemas = tenantsCacheBuilder.build(
            tenantId -> {
                Tenant tenant = tenantRepository.findByTenantId(tenantId)
                    .orElseThrow(() -> new ConnectionFactoryLookupFailureException("No such tenant: " + tenantId));
                return tenant.getSchema();
            });
    }

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

    @Override
    public Publisher<? extends Connection> create() {
        return TenantContext.getTenantId()
            .switchIfEmpty(Mono.defer(() ->
                Mono.error(new RuntimeException(String.format("ContextView does not contain the Lookup Key '%s'", TenantContext.TENANT_KEY)))))
            .flatMapMany(this::createConnection);
    }

    private Mono<Connection> createConnection(String tenant) {
        return Mono.from(connectionFactory.create())
            .flatMap(connection -> setTenant(connection, tenant));
    }

    private Mono<Connection> setTenant(Connection connection, String tenantId) {
        final String schema = tenantSchemas.get(tenantId);
        final Statement statement = connection.createStatement("SET SCHEMA '" + schema + "'");
        return Mono.from(statement.execute())
            .doOnSuccess(e -> log.debug("set tenant to {}", tenantId))
            .map(e -> connection);
    }

    @Override
    public ConnectionFactoryMetadata getMetadata() {
        return PostgresqlConnectionFactoryMetadata.INSTANCE;
    }

}