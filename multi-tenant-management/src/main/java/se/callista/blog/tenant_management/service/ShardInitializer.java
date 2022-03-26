package se.callista.blog.tenant_management.service;

import se.callista.blog.tenant_management.domain.entity.Shard;

public interface ShardInitializer {

    void initializeShard(Shard shard);

}
