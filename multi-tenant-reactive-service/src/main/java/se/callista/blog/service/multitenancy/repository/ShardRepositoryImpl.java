package se.callista.blog.service.multitenancy.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import se.callista.blog.service.multitenancy.domain.entity.Shard;

@RequiredArgsConstructor
@Repository
public class ShardRepositoryImpl implements ShardRepository {

    private static final String SQL_SELECT_ALL = "SELECT * FROM Shard";
    private static final String SQL_SELECT_SHARD = "SELECT * FROM Shard WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Shard> findAll() {
        return jdbcTemplate.query(SQL_SELECT_ALL, this::mapRow);
    }

    @Override
    public Optional<Shard> findById(Integer id) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(SQL_SELECT_SHARD, this::mapRow, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private Shard mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Shard.builder()
            .id(rs.getInt("id"))
            .db(rs.getString("db"))
            .build();
    }
}
