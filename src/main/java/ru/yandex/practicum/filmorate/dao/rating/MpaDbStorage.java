package ru.yandex.practicum.filmorate.dao.rating;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.rating.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mpa getById(int id) {
        String sql = "SELECT * FROM MPA WHERE MPA_ID = ?";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sql, id);

        if (!mpaRows.next()) {
            log.warn("Rating MPA {} is not found.", id);
            throw new NotFoundException("Mpa id " + id + " is not found");
        }
        return jdbcTemplate.queryForObject(sql, this::makeMpa, id);
    }

    @Override
    public List<Mpa> getAll() {
        String sql = "SELECT * FROM MPA";
        return jdbcTemplate.query(sql, this::makeMpa);
    }

    private Mpa makeMpa(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("MPA_ID"))
                .name(rs.getString("MPA_NAME"))
                .build();
    }
}
