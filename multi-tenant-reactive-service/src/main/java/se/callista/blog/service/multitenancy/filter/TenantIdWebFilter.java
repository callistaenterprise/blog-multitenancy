package se.callista.blog.service.multitenancy.filter;

import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import se.callista.blog.service.multitenancy.util.TenantContext;

@Component
public class TenantIdWebFilter implements WebFilter {

    public static final String X_TENANT_ID = "X-TENANT-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {

        List<String> headerValues = serverWebExchange.getRequest().getHeaders().get(X_TENANT_ID);

        if(headerValues == null || headerValues.isEmpty()) {
            return webFilterChain.filter(serverWebExchange);
        }

        String tenantId = headerValues.get(0);

        return TenantContext.withTenantId(
            tenantId,
            webFilterChain.filter(serverWebExchange)
        );
    }
}