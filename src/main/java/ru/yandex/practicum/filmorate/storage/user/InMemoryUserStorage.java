package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    public List<User> getUserFriends(Long userId) {
//        User user = get(userId);
//        return user.getFriends().stream()
//                .filter(users::containsKey)
//                .map(users::get)
//                .collect(Collectors.toList());
        return null;
    }

    private Long getNextId() {
        return id++;
    }
}
