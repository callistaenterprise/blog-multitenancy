package se.callista.blog.tenant_management.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.callista.blog.tenant_management.domain.entity.Shard;
import se.callista.blog.tenant_management.domain.entity.Tenant;
import se.callista.blog.tenant_management.repository.ShardRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class ShardManagementServiceImpl implements ShardManagementService {

    private final ShardRepository shardRepository;
    private final ShardInitializer shardInitializer;

    @Value("${multitenancy.master.database}")
    private String database;
    @Value("${multitenancy.shard.datasource.url-prefix}")
    private String urlPrefix;
    @Value("${multitenancy.shard.max-tenants}")
    private int maxTenants;

    private static final String DATABASE_NAME_INFIX = "_shard_";

    @Override
    @Transactional
    public void allocateToShard(Tenant tenant) {
        List<Shard> shardsWithFreeCapacity = shardRepository.findShardsWithFreeCapacity(maxTenants);
        if (!shardsWithFreeCapacity.isEmpty()) {
            Shard shard = shardsWithFreeCapacity.get(0);
            shard.addTenant(tenant);
            log.info("Allocated tenant {} to shard {}", tenant.getTenantId(), shard.getUrl());
        } else {
            int newShardIndex = ((int) shardRepository.count()) + 1;
            String newShardName = database + DATABASE_NAME_INFIX + newShardIndex;
            String newShardUrl = urlPrefix + newShardName;
            Shard shard = Shard.builder()
                .id(newShardIndex)
                .db(newShardName)
                .url(newShardUrl)
                .build();
            shardInitializer.initializeShard(shard);
            shard.addTenant(tenant);
            shardRepository.save(shard);
            log.info("Allocated tenant {} to new shard {}", tenant.getTenantId(), shard.getUrl());
        }
    }

}
