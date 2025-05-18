package ru.yandex.practicum.filmorate.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Primary
@Repository
public class UserDbStorage implements UserStorage {

    private static final Logger log = LoggerFactory.getLogger(UserDbStorage.class);

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        String sql = "INSERT INTO users (name, email, login, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getLogin());
            ps.setObject(4, user.getBirthday());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            user.setId(key.intValue());
        } else {
            log.error("Failed to retrieve generated ID for user {}", user);
            throw new RuntimeException("Failed to add user");
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, login = ?, birthday = ? WHERE id = ?";
        int updatedRows = jdbcTemplate.update(sql, user.getName(), user.getEmail(), user.getLogin(), user.getBirthday(), user.getId());
        if (updatedRows == 0) {
            log.warn("No user updated with id {}", user.getId());
        }
        return user;
    }

    @Override
    public User getUser(Integer id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, new UserRowMapper(), id);

            // Загружаем друзей (ID)
            List<Integer> friendIds = jdbcTemplate.queryForList(
                    "SELECT friend_id FROM user_friends WHERE user_id = ?", Integer.class, id);
            user.setFriends(new HashSet<>(friendIds));

            return user;
        } catch (EmptyResultDataAccessException e) {
            log.warn("User with id {} not found", id);
            return null;
        }
    }

    @Override
    public List<User> getUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("Нельзя добавить себя в друзья");
        }
        String sql = "INSERT INTO user_friends (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        String sql = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    public List<User> getFriends(Integer userId) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN user_friends f ON u.id = f.friend_id " +
                "WHERE f.user_id = ?";
        return jdbcTemplate.query(sql, new UserRowMapper(), userId);
    }

    @Override
    public List<Integer> getCommonFriends(Integer userId1, Integer userId2) {
        String sql = "SELECT friend_id FROM user_friends WHERE user_id = ? " +
                "INTERSECT " +
                "SELECT friend_id FROM user_friends WHERE user_id = ?";
        return jdbcTemplate.queryForList(sql, Integer.class, userId1, userId2);
    }

    @Override
    public boolean userExists(Integer userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null && count > 0;
    }

    @Override
    public Set<User> getUsersByIds(Set<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptySet();
        }

        // Построение параметров IN с вопросиками
        String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = "SELECT * FROM users WHERE id IN (" + inSql + ")";

        return new HashSet<>(jdbcTemplate.query(
                sql,
                ids.toArray(),
                new UserRowMapper()
        ));
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            return user;
        }
    }
}
