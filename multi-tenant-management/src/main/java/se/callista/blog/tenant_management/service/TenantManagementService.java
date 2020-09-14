package se.callista.blog.tenant_management.service;

public interface TenantManagementService {
    
    void createTenant(String tenantId, String db, String password);

}