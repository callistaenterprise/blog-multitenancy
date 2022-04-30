package se.callista.blog.service.multitenancy.callback;

import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import se.callista.blog.service.multitenancy.domain.entity.TenantAware;
import se.callista.blog.service.multitenancy.util.TenantContext;

@Component
public class TenantAwareEntityCallback implements BeforeConvertCallback<TenantAware> {

  @Override
  public Publisher<TenantAware> onBeforeConvert(TenantAware entity, SqlIdentifier table) {
    return TenantContext.getTenantId()
        .map(tenantId -> {
          entity.setTenantId(tenantId);
          return entity;
        });
  }
}
