package se.callista.blog.service.multi_tenancy.config.shard.hibernate;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.zaxxer.hikari.HikariDataSource;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Component;
import se.callista.blog.service.exception.NoSuchTenantException;
import se.callista.blog.service.multi_tenancy.datasource.TenantAwareDataSource;
import se.callista.blog.service.multi_tenancy.domain.entity.Shard;
import se.callista.blog.service.multi_tenancy.domain.entity.Tenant;
import se.callista.blog.service.multi_tenancy.repository.TenantRepository;

@Slf4j
@Component
public class DynamicShardingMultiTenantConnectionProvider
                extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    private static final long serialVersionUID = -460277105706399638L;

    private static final String TENANT_POOL_NAME_SUFFIX = "_DataSource";

    @Autowired
    @Qualifier("masterDataSource")
    private DataSource masterDataSource;

    @Autowired
    @Qualifier("masterDataSourceProperties")
    private DataSourceProperties dataSourceProperties;

    @Autowired
    private TenantRepository masterTenantRepository;

    @Value("${multitenancy.tenant-cache.maximumSize:100}")
    private Long tenantCacheMaximumSize;

    @Value("${multitenancy.tenant-cache.expireAfterAccess:10}")
    private Integer tenantCacheExpireAfterAccess;

    @Value("${multitenancy.datasource-cache.maximumSize:#{null}}")
    private Long datasourceCacheMaximumSize;

    @Value("${multitenancy.datasource-cache.expireAfterAccess:#{null}}")
    private Integer datasourceCacheExpireAfterAccess;

    @Value("${multitenancy.shard.datasource.url-prefix}")
    private String urlPrefix;

    @Value("${multitenancy.shard.username}")
    private String username;

    @Value("${multitenancy.shard.password}")
    private String password;

    private LoadingCache<String, Tenant> tenants;
    private LoadingCache<Shard, DataSource> shardDataSources;

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
            tenantId -> masterTenantRepository.findByTenantId(tenantId).orElseThrow(
                            () -> new NoSuchTenantException("No such tenant: " + tenantId)));
        Caffeine<Object, Object> shardDataSourcesCacheBuilder = Caffeine.newBuilder();
        if (datasourceCacheMaximumSize != null) {
            shardDataSourcesCacheBuilder.maximumSize(datasourceCacheMaximumSize);
        }
        if (datasourceCacheExpireAfterAccess != null) {
            shardDataSourcesCacheBuilder.expireAfterAccess(datasourceCacheExpireAfterAccess,
                            TimeUnit.MINUTES);
        }
        shardDataSourcesCacheBuilder.removalListener(
            (RemovalListener<Shard, DataSource>) (shard, dataSource, removalCause) -> {
                    HikariDataSource ds = (HikariDataSource) dataSource;
                    ds.close(); // tear down properly
                    log.info("Closed datasource: {}", ds.getPoolName());
            });
        shardDataSources = shardDataSourcesCacheBuilder.build(
            shard -> createAndConfigureDataSource(shard));
    }

    @Override
    protected DataSource selectAnyDataSource() {
        return masterDataSource;
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        Tenant tenant = tenants.get(tenantIdentifier);
        DataSource shardDataSource = shardDataSources.get(tenant.getShard());
        return new TenantAwareDataSource(shardDataSource);
    }

    private DataSource createAndConfigureDataSource(Shard shard) {
        HikariDataSource ds = dataSourceProperties.initializeDataSourceBuilder()
                        .type(HikariDataSource.class).build();

        ds.setUsername(username);
        ds.setPassword(password);
        ds.setJdbcUrl(urlPrefix + shard.getDb());

        ds.setPoolName(shard.getDb() + TENANT_POOL_NAME_SUFFIX);

        log.info("Configured datasource: {}", ds.getPoolName());
        return ds;
    }

}
