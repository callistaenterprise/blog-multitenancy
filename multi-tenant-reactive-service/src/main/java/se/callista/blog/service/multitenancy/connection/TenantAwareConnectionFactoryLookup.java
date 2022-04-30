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
import se.callista.blog.service.multitenancy.domain.entity.Shard;
import se.callista.blog.service.multitenancy.domain.entity.Tenant;
import se.callista.blog.service.multitenancy.repository.ShardRepository;
import se.callista.blog.service.multitenancy.repository.TenantRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class TenantAwareConnectionFactoryLookup implements ConnectionFactoryLookup {

  @Value("${multitenancy.shard.r2dbc.url-prefix}")
  private String urlPrefix;

  @Value("${multitenancy.tenant-cache.maximumSize:100}")
  private Long tenantCacheMaximumSize;

  @Value("${multitenancy.tenant-cache.expireAfterAccess:10}")
  private Integer tenantCacheExpireAfterAccess;

  @Value("${multitenancy.connectionfactory-cache.maximumSize:#{null}}")
  private Long connectionfactoryCacheMaximumSize;

  @Value("${multitenancy.connectionfactory-cache.expireAfterAccess:#{null}}")
  private Integer connectionfactoryCacheExpireAfterAccess;

  @Value("${multitenancy.shard.username}")
  private String username;

  @Value("${multitenancy.shard.password}")
  private String password;

  private final TenantRepository tenantRepository;
  private final ShardRepository shardRepository;

  private LoadingCache<String, Tenant> tenants;
  private LoadingCache<Integer, ConnectionFactory> connectionFactories;

  @PostConstruct
  private void createCaches() {
    Caffeine<Object, Object> tenantsCacheBuilder = Caffeine.newBuilder();
    if (tenantCacheMaximumSize != null) {
      tenantsCacheBuilder.maximumSize(tenantCacheMaximumSize);
    }
    if (tenantCacheExpireAfterAccess != null) {
      tenantsCacheBuilder.expireAfterAccess(tenantCacheExpireAfterAccess, TimeUnit.MINUTES);
    }
    tenants = tenantsCacheBuilder.build(
        tenantId -> tenantRepository.findByTenantId(tenantId).orElseThrow(
            () -> new ConnectionFactoryLookupFailureException("No such tenant: " + tenantId)));
    Caffeine<Object, Object> shardDataSourcesCacheBuilder = Caffeine.newBuilder();
    if (connectionfactoryCacheMaximumSize != null) {
      shardDataSourcesCacheBuilder.maximumSize(connectionfactoryCacheMaximumSize);
    }
    if (connectionfactoryCacheExpireAfterAccess != null) {
      shardDataSourcesCacheBuilder.expireAfterAccess(connectionfactoryCacheExpireAfterAccess,
          TimeUnit.MINUTES);
    }
    connectionFactories = shardDataSourcesCacheBuilder.build(
        shardId -> {
          Shard shard = shardRepository.findById(shardId).orElseThrow(
              () -> new ConnectionFactoryLookupFailureException("No such shard: " + shardId));
          return createAndConfigureConnectionFactory(shard);
        });
  }

  @Override
  public ConnectionFactory getConnectionFactory(String tenantId)
      throws ConnectionFactoryLookupFailureException {
    Tenant tenant = tenants.get(tenantId);
    ConnectionFactory shardConnectionFactory = connectionFactories.get(tenant.getShardId());
    return new TenantAwareConnectionFactory(shardConnectionFactory);
  }

  private ConnectionFactory createAndConfigureConnectionFactory(Shard shard) {
    ConnectionFactory connectionFactory =  ConnectionFactoryBuilder
        .withOptions(getConnectionFactoryOptions(urlPrefix, shard))
        .build();
    log.info("Configured connection factory for shard {}", shard.getId());
    return connectionFactory;
  }

  private ConnectionFactoryOptions.Builder getConnectionFactoryOptions(String url, Shard shard) {
    ConnectionFactoryOptions urlOptions = ConnectionFactoryOptions.parse(url);
    ConnectionFactoryOptions.Builder optionsBuilder = urlOptions.mutate();
    optionsBuilder.option(ConnectionFactoryOptions.DATABASE, shard.getDb());
    optionsBuilder.option(ConnectionFactoryOptions.USER, username);
    optionsBuilder.option(ConnectionFactoryOptions.PASSWORD, password);
    return optionsBuilder;
  }
}
