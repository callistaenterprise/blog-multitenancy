package se.callista.blog.service.multitenancy.interceptor;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;
import se.callista.blog.service.multitenancy.util.TenantContext;

@Component
public class TenantInterceptor implements WebRequestInterceptor {

    public static final String X_TENANT_ID = "X-TENANT-ID";

    @Override
    public void preHandle(WebRequest request) {
        if (request.getHeader(X_TENANT_ID) != null) {
            String tenantId = request.getHeader(X_TENANT_ID);
            TenantContext.setTenantId(tenantId);
        }
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
