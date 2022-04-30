package se.callista.blog.service.multitenancy.domain.entity;

import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Tenant {

    @Size(max = 30)
    private String tenantId;

    @Size(max = 30)
    private String db;

    @Size(max = 30)
    private String password;

}