package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserDao extends FilmorateDao<User>{
    List<Long> getFriends(Long id);
}
