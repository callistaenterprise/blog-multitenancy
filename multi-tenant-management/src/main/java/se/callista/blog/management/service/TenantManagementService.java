package se.callista.blog.management.service;

public interface TenantManagementService {
    
    void createTenant(String tenantId, String schema);
}