package se.callista.blog.service.multi_tenancy.repository;

import java.util.List;
import java.util.Optional;
import se.callista.blog.service.multi_tenancy.domain.entity.Shard;

public interface ShardRepository {

    List<Shard> findAll();

    Optional<Shard> findById(Integer id);
}
