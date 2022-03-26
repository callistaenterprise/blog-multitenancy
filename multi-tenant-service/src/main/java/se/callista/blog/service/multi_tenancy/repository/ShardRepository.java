package se.callista.blog.service.multi_tenancy.repository;

import org.springframework.data.repository.CrudRepository;
import se.callista.blog.service.multi_tenancy.domain.entity.Shard;

public interface ShardRepository extends CrudRepository<Shard, Integer> {
}
