package se.callista.blog.service.multitenancy.domain.entity;

public interface TenantAware {

    String getTenantId();

    void setTenantId(String tenantId);
}
