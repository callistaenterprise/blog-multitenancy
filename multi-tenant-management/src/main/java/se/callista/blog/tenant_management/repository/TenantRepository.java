package se.callista.blog.tenant_management.repository;

import org.springframework.data.repository.CrudRepository;
import se.callista.blog.tenant_management.domain.entity.Tenant;

public interface TenantRepository extends CrudRepository<Tenant, String> {
}