package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Repository("userDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    public UserDbStorage(JdbcTemplate jdbcTemplate, UserRowMapper userRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRowMapper = userRowMapper;
    }

    @Override
    public Collection<User> findAll() {
        String sql = "SELECT * FROM users";
        List<User> users = jdbcTemplate.query(sql, userRowMapper);
        for (User user : users) {
            loadFriends(user);
        }
        return users;
    }

    @Override
    public User findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> result = jdbcTemplate.query(sql, userRowMapper, id);
        if (result.isEmpty()) {
            return null;
        }
        User user = result.get(0);
        loadFriends(user);
        return user;
    }

    @Override
    public User create(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, user.getBirthday() != null ? Date.valueOf(user.getBirthday()) : null);
            return ps;
        }, keyHolder);

        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        user.setId(id);

        saveFriends(user);

        return user;
    }

    @Override
    public User update(User user) {

        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";

        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday() != null ? Date.valueOf(user.getBirthday()) : null,
                user.getId()
        );

        jdbcTemplate.update("DELETE FROM friendships WHERE user_id = ?", user.getId());
        saveFriends(user);

        return user;
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM friendships WHERE user_id = ?", id);
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
    }

    @Override
    public boolean exists(Long id) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    // ========== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ==========

    private void loadFriends(User user) {
        String sql = "SELECT friend_id FROM friendships WHERE user_id = ?";
        List<Long> friends = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("friend_id"), user.getId());
        user.setFriends(new HashSet<>(friends));
    }

    private void saveFriends(User user) {
        if (user.getFriends() == null || user.getFriends().isEmpty()) {
            return;
        }

        String sql = "INSERT INTO friendships (user_id, friend_id, status_id) VALUES (?, ?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();
        for (Long friendId : user.getFriends()) {
            batchArgs.add(new Object[]{user.getId(), friendId, 1});
        }
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}
