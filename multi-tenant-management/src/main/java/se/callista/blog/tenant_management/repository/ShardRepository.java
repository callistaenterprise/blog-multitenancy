package se.callista.blog.tenant_management.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import se.callista.blog.tenant_management.domain.entity.Shard;

public interface ShardRepository extends CrudRepository<Shard, Integer> {

    @Query("SELECT s FROM Shard s WHERE s.numberOfTenants < :maxTenants")
    List<Shard> findShardsWithFreeCapacity(int maxTenants);

}
