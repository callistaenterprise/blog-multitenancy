package se.callista.blog.service.multi_tenancy.domain.entity;

public interface TenantAware {

    String getTenantId();

    void setTenantId(String tenantId);
}
