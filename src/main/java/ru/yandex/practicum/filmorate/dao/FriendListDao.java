package ru.yandex.practicum.filmorate.dao;

import java.util.List;

public interface FriendListDao {

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    List<Long> getFriends(Long userId);
}
