package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dao.impl.FriendListDb;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.FilmorateEventOperation;
import ru.yandex.practicum.filmorate.utils.FilmorateEventType;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao storage;
    private final FriendListDb friendListDb;
    private final EventsService eventsService;

    public User create(User user) {
        return storage.create(user);
    }

    public List<User> getAllUsers() {
        return storage.getAll();
    }

    public User getUserBy(Long id) {
        return storage.getBy(id).orElseThrow(() -> new FilmorateNotFoundException("Пользователь не найден."));
    }

    public User update(User user) {
        getUserBy(user.getId());
        return storage.update(user);
    }

    public void addFriend(Long friendId, Long userId) {
        friendListDb.addFriend(userId, friendId);

        eventsService.create(userId, friendId, FilmorateEventType.FRIEND, FilmorateEventOperation.ADD);
    }

    public void removeFriend(Long friendId, Long userId) {
        getUserBy(userId);
        getUserBy(friendId);
        friendListDb.removeFriend(userId, friendId);

        eventsService.create(userId, friendId, FilmorateEventType.FRIEND, FilmorateEventOperation.REMOVE);
    }

    public List<User> getUserFriends(Long userId) {
        getUserBy(userId);
        return friendListDb.getFriends(userId);
    }

    public void approveFriend(Long friendId, Long userId) {
        friendListDb.approveFriend(userId, friendId);

        eventsService.create(userId, friendId, FilmorateEventType.FRIEND, FilmorateEventOperation.UPDATE);
    }

    public List<User> getMutualFriends(Long userId, Long otherUserId) {
        getUserBy(userId);
        getUserBy(otherUserId);
        return friendListDb.getCommonFriends(userId, otherUserId);
    }

    public List<Event> getFeed(Long userId) {
        getUserBy(userId);
        return eventsService.getFeed(userId);
    }
}