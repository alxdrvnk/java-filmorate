package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Long, User> users = new HashMap<>();

    private Long id = 1L;

    @Override
    public User create(User user) {
        Long id = getNextId();
        User newUser = user.withId(id);
        users.put(id, newUser);
        return newUser;
    }

    @Override
    public User update(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        } else {
            throw new FilmorateNotFoundException(String.format("Пользователь с таким id: %d не найдент.", user.getId()));
        }
    }

    @Override
    public User get(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new FilmorateNotFoundException(String.format("Пользователь с id: %d не найден.", id));
        }
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addFriend(Long userId, Long friendId) {
        User user = get(userId);
        User friend = get(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        return user;
    }

    @Override
    public User removeFriend(Long userId, Long friendId) {
        User user = get(userId);
        User friend = get(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        return user;
    }

    @Override
    public List<User> getUserFriends(Long userId) {
        User user = get(userId);
        return user.getFriends().stream()
                .filter(users::containsKey)
                .map(users::get)
                .collect(Collectors.toList());
    }

    private Long getNextId() {
        return id++;
    }
}
