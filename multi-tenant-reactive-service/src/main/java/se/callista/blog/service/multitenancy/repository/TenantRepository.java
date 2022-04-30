package se.callista.blog.service.multitenancy.repository;

import java.util.List;
import java.util.Optional;
import se.callista.blog.service.multitenancy.domain.entity.Tenant;

public interface TenantRepository {

    List<Tenant> findAll();

    Optional<Tenant> findByTenantId(String tenantId);
}
