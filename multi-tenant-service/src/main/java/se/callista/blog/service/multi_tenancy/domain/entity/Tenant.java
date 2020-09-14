package se.callista.blog.service.multi_tenancy.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Size;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Tenant {

    @Id
    @Size(max = 30)
    @Column(name = "tenant_id")
    private String tenantId;

    @Size(max = 30)
    @Column(name = "schema")
    private String schema;

    @Size(max = 256)
    @Column(name = "url")
    private String url;

    @Size(max = 30)
    @Column(name = "password")
    private String password;

}