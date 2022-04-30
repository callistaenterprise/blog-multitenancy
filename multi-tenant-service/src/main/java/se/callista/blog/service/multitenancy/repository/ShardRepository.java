package se.callista.blog.service.multitenancy.repository;

import org.springframework.data.repository.CrudRepository;
import se.callista.blog.service.multitenancy.domain.entity.Shard;

public interface ShardRepository extends CrudRepository<Shard, Integer> {
}
