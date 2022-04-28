package se.callista.blog.service.multi_tenancy.domain.entity;

import java.io.Serializable;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractBaseEntity implements TenantAware, Serializable {
    private static final long serialVersionUID = 1L;

    @Size(max = 30)
    @Column("tenant_id")
    private String tenantId;

}
