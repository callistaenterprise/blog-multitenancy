package se.callista.blog.service.multitenancy.connection;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.r2dbc.connection.lookup.ConnectionFactoryLookup;
import org.springframework.r2dbc.connection.lookup.ConnectionFactoryLookupFailureException;
import org.springframework.stereotype.Component;
import se.callista.blog.service.multitenancy.domain.entity.Tenant;
import se.callista.blog.service.multitenancy.repository.TenantRepository;
import se.callista.blog.service.util.EncryptionService;

@Slf4j
@RequiredArgsConstructor
@Component
public class TenantAwareConnectionFactoryLookup implements ConnectionFactoryLookup {

  @Value("${multitenancy.tenant.r2dbc.url-prefix}")
  private String urlPrefix;

  @Value("${multitenancy.connectionfactory-cache.maximumSize:100}")
  private Long maximumSize;

  @Value("${multitenancy.connectionfactory-cache.expireAfterAccess:10}")
  private Integer expireAfterAccess;

  @Value("${encryption.secret}")
  private String secret;

  @Value("${encryption.salt}")
  private String salt;

  private final TenantRepository tenantRepository;

  private final EncryptionService encryptionService;

  private LoadingCache<String, ConnectionFactory> tenantConnectionFactories;

  @PostConstruct
  private void createCache() {
    Caffeine<Object, Object> tenantsCacheBuilder = Caffeine.newBuilder();
    if (maximumSize != null) {
      tenantsCacheBuilder.maximumSize(maximumSize);
    }
    if (expireAfterAccess != null) {
      tenantsCacheBuilder.expireAfterAccess(expireAfterAccess, TimeUnit.MINUTES);
    }
    tenantConnectionFactories = tenantsCacheBuilder.build(
        tenantId -> {
          Tenant tenant = tenantRepository.findByTenantId(tenantId)
              .orElseThrow(() -> new ConnectionFactoryLookupFailureException("No such tenant: " + tenantId));
            return createAndConfigureConnectionFactory(tenant);
        });
  }

  @Override
  public ConnectionFactory getConnectionFactory(String tenantId)
      throws ConnectionFactoryLookupFailureException {
    return tenantConnectionFactories.get(tenantId);
  }

  private ConnectionFactory createAndConfigureConnectionFactory(Tenant tenant) {
    ConnectionFactory connectionFactory =  ConnectionFactoryBuilder
        .withOptions(getConnectionFactoryOptions(urlPrefix, tenant))
        .build();
    log.info("Configured connection factory for tenant {}", tenant.getTenantId());
    return connectionFactory;
  }

  private ConnectionFactoryOptions.Builder getConnectionFactoryOptions(String url, Tenant tenant) {
    ConnectionFactoryOptions urlOptions = ConnectionFactoryOptions.parse(url);
    ConnectionFactoryOptions.Builder optionsBuilder = urlOptions.mutate();
    optionsBuilder.option(ConnectionFactoryOptions.DATABASE, tenant.getDb());
    optionsBuilder.option(ConnectionFactoryOptions.USER, tenant.getDb());
    optionsBuilder.option(ConnectionFactoryOptions.PASSWORD, encryptionService.decrypt(tenant.getPassword(), secret, salt));
    return optionsBuilder;
  }
}
