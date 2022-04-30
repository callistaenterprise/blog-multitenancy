package se.callista.blog.service.multitenancy.repository;

import java.util.List;
import java.util.Optional;
import se.callista.blog.service.multitenancy.domain.entity.Shard;

public interface ShardRepository {

    List<Shard> findAll();

    Optional<Shard> findById(Integer id);
}
