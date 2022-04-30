package se.callista.blog.management.repository;

import org.springframework.data.repository.CrudRepository;
import se.callista.blog.management.domain.entity.Tenant;

public interface TenantRepository extends CrudRepository<Tenant, String> {
}