package se.callista.blog.service.domain.entity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("product")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Builder
    public Product(Long id, String name, Integer version) {
        this.id = id;
        this.name = name;
        this.version = version;
    }

    @Id
    @Column("id")
    protected Long id;

    @Column("name")
    @NotNull
    @Size(max = 255)
    private String name;

    @Version
    @Column("version")
    protected Integer version;

}
