package se.callista.blog.service.multitenancy.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import se.callista.blog.service.multitenancy.domain.entity.Tenant;

public interface TenantRepository extends CrudRepository<Tenant, String> {
  
    @Query("SELECT DISTINCT t FROM Tenant t JOIN FETCH t.shard WHERE t.tenantId = :tenantId")
    Optional<Tenant> findByTenantId(String tenantId);

}