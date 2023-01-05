package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FriendListDao;

import java.util.List;

@Component
public class FriendListDb implements FriendListDao {

    JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(Long userId, Long friendId) {
        String sql = "INSERT INTO friends_list (user_id, friend_id, state) VALUES (?, ?, FALSE)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        String sql = "DELETE FROM friends_list WHERE user_id = ? AND friends_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<Long> getFriends(Long userId) {
        String sql = "SELECT friends_id FROM friends_list WHERE user_id = ?";
        return jdbcTemplate.query(sql,(rs, rowNum) -> rs.getLong("friend_id"), userId);
    }
}
