package se.callista.blog.management.service;

public interface TenantManagementService {

    /**
     * Create a tenant and allocate to a suitable shard.
     */
    void createTenant(String tenantId);

}