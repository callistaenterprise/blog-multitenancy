package se.callista.blog.service.multi_tenancy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.callista.blog.service.multi_tenancy.domain.entity.Tenant;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, String> {

    @Query("select t from Tenant t where t.tenantId = :tenantId")
    Optional<Tenant> findByTenantId(@Param("tenantId") String tenantId);
}
