package se.callista.blog.service.multi_tenancy.repository;

import java.util.List;
import java.util.Optional;
import se.callista.blog.service.multi_tenancy.domain.entity.Tenant;

public interface TenantRepository {

    List<Tenant> findAll();

    Optional<Tenant> findByTenantId(String tenantId);
}
