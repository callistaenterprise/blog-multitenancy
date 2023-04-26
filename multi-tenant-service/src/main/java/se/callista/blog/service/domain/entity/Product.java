package se.callista.blog.service.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.callista.blog.service.multitenancy.domain.entity.AbstractBaseEntity;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
public class Product extends AbstractBaseEntity {

    @Builder
    public Product(Long id, String name, Integer version, String tenantId) {
        super(tenantId);
        this.id = id;
        this.name = name;
        this.version = version;
    }

    @Id
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "name", length = 255, nullable = false)
    @NotNull
    @Size(max = 255)
    private String name;

    @Version
    @Column(name = "version", nullable = false, columnDefinition = "int default 0")
    protected Integer version;

}
