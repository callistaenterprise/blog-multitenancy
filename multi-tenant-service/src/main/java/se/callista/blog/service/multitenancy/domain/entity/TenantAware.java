package se.callista.blog.service.multitenancy.domain.entity;

public interface TenantAware {

    void setTenantId(String tenantId);

}
