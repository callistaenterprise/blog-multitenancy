package se.callista.blog.service.multi_tenancy.domain.entity;

public interface TenantAware {

    void setTenantId(String tenantId);

}
