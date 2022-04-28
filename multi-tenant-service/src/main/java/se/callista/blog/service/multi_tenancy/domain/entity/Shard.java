package se.callista.blog.service.multi_tenancy.domain.entity;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Shard {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 30)
    @Column(name = "db")
    private String db;

    @Column(name = "no_of_tenants")
    private int numberOfTenants;

    @OneToMany(
        mappedBy = "shard",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @Builder.Default
    private Set<Tenant> tenants = new HashSet<>();
    
    public void addTenant(Tenant tenant) {
        tenants.add(tenant);
        numberOfTenants++;
        tenant.setShard(this);
    }

    public void removeTenant(Tenant tenant) {
        if (tenants.remove(tenant)) {
            numberOfTenants--;
            tenant.setShard(null);
        } else {
            throw new IllegalStateException(MessageFormat.format("Tenant {0} not found in shard", tenant));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Shard that = (Shard) o;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
    
}