package se.callista.blog.service.multi_tenancy.callback;

import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import se.callista.blog.service.multi_tenancy.domain.entity.TenantAware;
import se.callista.blog.service.multi_tenancy.util.TenantContext;

@Component
public class TenantAwareEntityCallback implements BeforeConvertCallback<TenantAware> {

  @Override
  public Publisher<TenantAware> onBeforeConvert(TenantAware entity, SqlIdentifier table) {
    return TenantContext.getTenantId()
        .map(tenantId -> {
          entity.setTenantId((String) tenantId);
          return entity;
        });
  }
}
