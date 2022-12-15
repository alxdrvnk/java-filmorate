package ru.yandex.practicum.filmorate.storage.user;


import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmorateValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
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
        if (user.getName() == null) {
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
        User user = users.get(userId);
        User friend = users.get(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        return user;
    }

    @Override
    public User removeFriend(Long userId, Long friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        return user;
    }

    @Override
    public Set<Long> getUserFriends(Long userId) {
        return users.get(userId).getFriends();
    }

    @Override
    public List<Long> getMutualFriends(Long userId, Long otherUserId) {
        User user = users.get(userId);
        User otherUser = users.get(otherUserId);

        return  user.getFriends().stream().filter(f -> otherUser.getFriends().contains(f)).collect(Collectors.toList());
    }

    private Long getNextId() {
        return id++;
    }
    private void validateUserBirthday(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new FilmorateValidationException("День рождения не может быть в будущем.");
        }
    }
}
