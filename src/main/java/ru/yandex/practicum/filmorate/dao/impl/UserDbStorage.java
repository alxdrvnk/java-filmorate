package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dao.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component("userDbStorage")
@Primary
public class UserDbStorage implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        Long userId = simpleJdbcInsert.executeAndReturnKey(userToParameters(user)).longValue();
        return user.withId(userId);
    }

    @Override
    public Optional<User> getBy(Long id) {
        String query = "SELECT * FROM users WHERE id = ? AND deleted = false";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(query, new UserMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> getAll() {
        String query = "SELECT * FROM users WHERE deleted = false";
        return jdbcTemplate.query(query, new UserMapper());
    }

    @Override
    public User update(User user) {
        String query = "UPDATE users SET email = ?, login  = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(query,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public int deleteBy(Long id) {
        String query = "UPDATE users SET deleted = true WHERE id = ?";
        return jdbcTemplate.update(query, id);
    }

    private Map<String, Object> userToParameters(User user) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", user.getId());
        parameters.put("email", user.getEmail());
        parameters.put("login", user.getLogin());
        parameters.put("name", user.getName());
        parameters.put("birthday", user.getBirthday());
        parameters.put("deleted", user.getDeleted());
        return parameters;
    }


}
