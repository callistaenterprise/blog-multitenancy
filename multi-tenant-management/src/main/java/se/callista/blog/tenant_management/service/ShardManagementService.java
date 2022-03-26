package se.callista.blog.tenant_management.service;

import se.callista.blog.tenant_management.domain.entity.Tenant;

public interface ShardManagementService {
    
    /**
     * Allocate a tenant to a shard, creating a new shard if necessary.
     * 
     * @param tenant the tenant, which must be a JPA managed object that
     * will be modified by the operation.
     */
    void allocateToShard(Tenant tenant);

}