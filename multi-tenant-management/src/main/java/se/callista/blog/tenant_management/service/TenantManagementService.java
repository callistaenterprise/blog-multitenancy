package se.callista.blog.tenant_management.service;

public interface TenantManagementService {

    /**
     * Create a tenant and allocate to a suitable shard.
     */
    void createTenant(String tenantId);

}