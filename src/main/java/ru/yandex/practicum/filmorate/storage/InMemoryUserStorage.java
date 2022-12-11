package ru.yandex.practicum.filmorate.storage;


import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryUserStorage implements UserStorage{

    private final HashMap<Long, User> users = new HashMap<>();

    private static Long id = 1L;

    @Override
    public User create(User user) {
        return null;
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public User get(Long id) {
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return null;
    }

    @Override
    public User addFriend(Long fromUserId, Long toUserId) {
        return null;
    }

    @Override
    public User removeFriend(Long fromUserId, Long toUserId) {
        return null;
    }
}
