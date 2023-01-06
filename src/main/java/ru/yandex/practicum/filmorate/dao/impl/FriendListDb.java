package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FriendListDao;
import ru.yandex.practicum.filmorate.dao.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FriendListDb implements FriendListDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(Long userId, Long friendId) {
        String query = "INSERT INTO friend_list (user_id, friend_id, state) VALUES (?, ?, FALSE)";
        jdbcTemplate.update(query, userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        String query = "DELETE FROM friend_list WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(query, userId, friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        String query = "SELECT * FROM users " +
                "WHERE id IN(SELECT fl.friend_id " +
                             "FROM friend_list AS fl " +
                             "WHERE fl.user_id = ?)";
        return jdbcTemplate.query(query, new UserMapper(), userId);
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherUserId) {
         String query =
                 "SELECT * FROM users " +
                 "WHERE id IN (SELECT fl.friend_id FROM friend_list AS fl " +
                              "INNER JOIN " +
                                  "(SELECT friend_id FROM friend_list " +
                                  "WHERE user_id = ?) AS ffl " +
                              "ON ffl.friend_id = fl.friend_id " +
                              "WHERE fl.user_id = ?)";
         return jdbcTemplate.query(query, new UserMapper(), userId, otherUserId);
    }


}
