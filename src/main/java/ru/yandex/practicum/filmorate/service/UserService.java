package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.FilmLikeDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dao.impl.FriendListDb;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao storage;
    private final FriendListDb friendListDb;
    private final FilmDao filmDao;
    private final FilmLikeDao likeDao;

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

    public void addFriend(Long userId, Long friendId) {
        friendListDb.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        getUserBy(userId);
        getUserBy(friendId);
        friendListDb.removeFriend(userId, friendId);
    }

    public List<User> getUserFriends(Long userId) {
        getUserBy(userId);
        return friendListDb.getFriends(userId);
    }

    public void approveFriend(Long userId, Long friendID) {
        friendListDb.approveFriend(userId, friendID);
    }

    public List<User> getMutualFriends(Long userId, Long otherUserId) {
        getUserBy(userId);
        getUserBy(otherUserId);
        return friendListDb.getCommonFriends(userId, otherUserId);
    }

    public void deleteUserBy(Long id) {
        if (storage.deleteBy(id) == 0) {
            throw new FilmorateNotFoundException(
                    String.format("Пользователь с id: %d не найден.", id));
        }

    public List<Film> getRecommendations(Long userId) {
        // достаем всех пользователей у которых есть хотя бы одно пересечение по лайкам с текущим пользователем
        Map<Long, List<Long>> likes = likeDao.getSameLikesByUser(userId);
        List<Long> userFilms = likes.remove(userId);

        // если у текущего пользователя нет лайков или нет пересечений с другими, то и рекоммендаций быть не должно
        if (userFilms == null || likes.isEmpty()) {
            return Collections.emptyList();
        }

        // заполняем мапу с количеством пересечений (count:[userId]) с сортировкой по убыванию
        Map<Integer, List<Long>> intersects = new TreeMap<>(Comparator.reverseOrder());
        for (Map.Entry<Long, List<Long>> entry : likes.entrySet()) {
            int intersectsCount = countingIntersects(userFilms, entry.getValue());
            intersects.putIfAbsent(intersectsCount, new ArrayList<>());
            intersects.get(intersectsCount).add(entry.getKey());
        }

        // находим пользователя с максимальным количеством пересечений,
        // если количество пересечений совпадает с количеством фильмов,
        // то этот юзер нам не подходит т.к. нечего рекомендовать, берем следующего
        List<Long> similarUserFilms = new ArrayList<>();
        for (Map.Entry<Integer, List<Long>> entry : intersects.entrySet()) {
            for (Long currentUserId : entry.getValue()) {
                List<Long> currentUserFilms = likes.get(currentUserId);
                if (entry.getKey() < currentUserFilms.size()){
                    similarUserFilms = currentUserFilms;
                    break;
                }
            }
            if (!similarUserFilms.isEmpty()){
                break;
            }
        }

        // находим фильмы, которые не пролайкал текущий пользователь.
        List<Long> difference = findDifference(userFilms, similarUserFilms);
        return filmDao.getByIds(difference);
    }

    private int countingIntersects(List<Long> userFilms, List<Long> anotherUserFilms) {
        int count = 0;
        for (Long filmId : userFilms) {
            if (anotherUserFilms.contains(filmId)) {
                count++;
            }
        }
        return count;
    }

    private List<Long> findDifference(List<Long> userFilms, List<Long> anotherFilms) {
        List<Long> diff = new ArrayList<>();
        for (Long filmId : anotherFilms) {
            if (!userFilms.contains(filmId)) {
                diff.add(filmId);
            }
        }
        return diff;
    }
}