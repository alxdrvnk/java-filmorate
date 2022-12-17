package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmorateValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Long, User> users = new HashMap<>();

    private static Long id = 1L;

    @Override
    public User create(User user) {
        validateUserBirthday(user);
        Long id = getNextId();
        User newUser = user.withId(id);
        if (user.getName() == null || user.getName().isEmpty()) {
            newUser = newUser.withName(user.getLogin());
        }
        users.put(id, newUser);
        return newUser;
    }

    @Override
    public User update(User user) {
        validateUserBirthday(user);
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

    @Override
    public List<User> getMutualFriends(Long userId, Long otherUserId) {
        User user = get(userId);
        User otherUser = get(otherUserId);

        return user.getFriends().stream()
                .filter(f -> otherUser.getFriends().contains(f))
                .map(users::get)
                .collect(Collectors.toList());

    }

    private static Long getNextId() {
        return id++;
    }
    private void validateUserBirthday(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new FilmorateValidationException("День рождения не может быть в будущем.");
        }
    }
}
