package se.callista.blog.service.multitenancy.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import se.callista.blog.service.multitenancy.domain.entity.Tenant;

@RequiredArgsConstructor
@Repository
public class TenantRepositoryImpl implements TenantRepository {

    private static final String SQL_SELECT_ALL = "SELECT * FROM Tenant";
    private static final String SQL_SELECT_TENANT = "SELECT * FROM Tenant WHERE tenant_id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Tenant> findAll() {
        return jdbcTemplate.query(SQL_SELECT_ALL, this::mapRow);
    }

    @Override
    public Optional<Tenant> findByTenantId(String tenantId) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(SQL_SELECT_TENANT, this::mapRow, tenantId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private Tenant mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Tenant.builder()
            .tenantId(rs.getString("tenant_id"))
            .db(rs.getString("db"))
            .password(rs.getString("password"))
            .build();
    }
}
