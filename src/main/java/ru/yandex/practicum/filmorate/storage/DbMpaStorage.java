package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Repository
public class DbMpaStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbMpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa getMpa(Integer id) {
        String sql = "SELECT * FROM mpa WHERE id = ?";
        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                return new Mpa(rs.getInt("id"), rs.getString("name"));
            }
            return null;
        }, id);
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa ORDER BY id";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Mpa(rs.getInt("id"), rs.getString("name")));
    }

    @Override
    public boolean existsById(int id) {
        String sql = "SELECT COUNT(*) FROM mpa WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}


