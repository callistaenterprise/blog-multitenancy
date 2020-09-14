package se.callista.blog.service.multi_tenancy.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;
import se.callista.blog.service.multi_tenancy.util.TenantContext;

@Component
public class TenantInterceptor implements WebRequestInterceptor {

    private final String defaultTenant;

    @Autowired
    public TenantInterceptor(
            @Value("${multitenancy.tenant.default-tenant:#{null}}") String defaultTenant) {
        this.defaultTenant = defaultTenant;
    }

    @Override
    public void preHandle(WebRequest request) {
        String tenantId;
        if (request.getHeader("X-TENANT-ID") != null) {
            tenantId = request.getHeader("X-TENANT-ID");
        } else if (this.defaultTenant != null) {
            tenantId = this.defaultTenant;
        } else {
            tenantId = ((ServletWebRequest)request).getRequest().getServerName().split("\\.")[0];
        }
        TenantContext.setTenantId(tenantId);
    }

    @Override
    public void postHandle(@NonNull WebRequest request, ModelMap model) {
        TenantContext.clear();
    }

    @Override
    public void afterCompletion(@NonNull WebRequest request, Exception ex) {
        // NOOP
    }
}
