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
public class Shard {

    private Integer id;

    @Size(max = 30)
    private String db;

}