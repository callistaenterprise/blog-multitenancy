package se.callista.blog.service.multitenancy.listener;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import se.callista.blog.service.multitenancy.domain.entity.TenantAware;
import se.callista.blog.service.multitenancy.util.TenantContext;

public class TenantListener {

    @PreUpdate
    @PreRemove
    @PrePersist
    public void setTenant(TenantAware entity) {
        final String tenantId = TenantContext.getTenantId();
        entity.setTenantId(tenantId);
    }
}
