package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FriendListDao;
import ru.yandex.practicum.filmorate.dao.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class FriendListDb implements FriendListDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(Long userId, Long friendId) {
        try {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("friend_list");
            simpleJdbcInsert.execute(friendsToParameters(userId, friendId, false));
        } catch (DuplicateKeyException e) {
            log.debug(
                    String.format("FriendList: trying to add duplicate friend User id: %d and Friend id: %d",
                            userId,
                            friendId));
        }
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        String query = "DELETE FROM friend_list WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(query, userId, friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        String query =
                "SELECT * FROM users " +
                "WHERE id IN(SELECT fl.friend_id FROM friend_list AS fl " +
                            "WHERE fl.user_id = ? " +
                            "UNION  " +
                            "SELECT fl.user_id FROM friend_list AS fl " +
                            "WHERE fl.friend_id = ? AND fl.state = true)";
        return jdbcTemplate.query(query, new UserMapper(), userId, userId);
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        String query =
                "SELECT * FROM users " +
                "WHERE id IN( " +
                "SELECT fu.friends_id FROM ( " +
                        "SELECT fl.friend_id AS friends_id FROM friend_list AS fl " +
                        "WHERE fl.user_id = ? " +
                        "UNION "+
                        "SELECT fl.user_id FROM friend_list AS fl " +
                        "WHERE fl.friend_id = ? AND fl.state = true) AS fu " +
                "INNER JOIN ( " +
                        "SELECT fl.friend_id AS friends_id FROM friend_list AS fl " +
                        "WHERE fl.user_id = ? " +
                        "UNION " +
                        "SELECT fl.user_id FROM friend_list AS fl " +
                        "WHERE fl.friend_id = ? AND fl.state = true) AS fou " +
                "ON fou.friends_id = fu.friends_id)";
        return jdbcTemplate.query(query, new UserMapper(), userId, userId, otherUserId, otherUserId);
    }

    private Map<String, Object> friendsToParameters(Long userId, Long friendId, boolean state) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user_id", userId);
        parameters.put("friend_id", friendId);
        parameters.put("state", state);
        return parameters;
    }
}
