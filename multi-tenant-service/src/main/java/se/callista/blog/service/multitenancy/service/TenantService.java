package se.callista.blog.service.multitenancy.service;

import org.springframework.data.repository.query.Param;
import se.callista.blog.service.multitenancy.domain.entity.Tenant;

public interface TenantService {
    
    Tenant findByTenantId(@Param("tenantId") String tenantId);
}
