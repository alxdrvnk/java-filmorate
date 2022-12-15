package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {

    User create(User user);

    User update(User user);

    User get(Long id);

    List<User> getAllUsers();

    User addFriend(Long userId, Long friendId);

    User removeFriend(Long userId, Long friendId);

    Set<Long> getUserFriends(Long userId);

    List<Long> getMutualFriends(Long userId, Long otherUserId);
}
