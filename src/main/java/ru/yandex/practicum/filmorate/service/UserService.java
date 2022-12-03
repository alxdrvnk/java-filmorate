package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmorateValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private final Map<Integer, User> users = new HashMap<>();

    private static Integer id = 0;

    private static Integer getNextId() {
        return id++;
    }

    public User create(User user) {
        validateUserBirthday(user);
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        int id = getNextId();
        user.setId(id);

        users.put(id, user);
        return user;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUserBy(int id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new FilmorateNotFoundException(String.format("Пользователь с id: %d не найден.", id));
        }
    }

    public User update(User user) {
        validateUserBirthday(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        } else {
            throw new FilmorateNotFoundException(String.format("Пользователь с id: %d не найден.", user.getId()));
        }
    }

    private void validateUserBirthday(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new FilmorateValidationException("День рождения не может быть в будущем.");
        }
    }
}
