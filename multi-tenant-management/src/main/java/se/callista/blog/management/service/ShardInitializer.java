package se.callista.blog.management.service;

import se.callista.blog.management.domain.entity.Shard;

public interface ShardInitializer {

    void initializeShard(Shard shard);

}
