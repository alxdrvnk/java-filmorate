package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.FilmLikeDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dao.impl.FriendListDbStorage;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.FilmorateEventOperation;
import ru.yandex.practicum.filmorate.utils.FilmorateEventType;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao storage;
    private final FriendListDbStorage friendListDbStorage;
    private final FilmDao filmDao;
    private final FilmLikeDao likeDao;
    private final EventsService eventsService;

    public User create(User user) {
        return storage.create(user.withDeleted(false));
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
        friendListDbStorage.addFriend(userId, friendId);

        eventsService.create(userId, friendId, FilmorateEventType.FRIEND, FilmorateEventOperation.ADD);
    }

    public void removeFriend(Long friendId, Long userId) {
        getUserBy(userId);
        getUserBy(friendId);
        friendListDbStorage.removeFriend(userId, friendId);

        eventsService.create(userId, friendId, FilmorateEventType.FRIEND, FilmorateEventOperation.REMOVE);
    }

    public List<User> getUserFriends(Long userId) {
        getUserBy(userId);
        return friendListDbStorage.getFriends(userId);
    }

    public void approveFriend(Long friendId, Long userId) {
        friendListDbStorage.approveFriend(userId, friendId);

        eventsService.create(userId, friendId, FilmorateEventType.FRIEND, FilmorateEventOperation.UPDATE);
    }

    public List<User> getMutualFriends(Long userId, Long otherUserId) {
        getUserBy(userId);
        getUserBy(otherUserId);
        return friendListDbStorage.getCommonFriends(userId, otherUserId);
    }

    public List<Event> getFeed(Long userId) {
        getUserBy(userId);
        return eventsService.getFeed(userId);
    }

    public void deleteUserBy(Long id) {
        if (storage.deleteBy(id) == 0) {
            throw new FilmorateNotFoundException(
                    String.format("Пользователь с id: %d не найден.", id));
        }
    }

    public List<Film> getRecommendations(Long userId, int count) {
        return likeDao.getSameLikesByUser(userId, count);
    }
}