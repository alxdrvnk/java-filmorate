package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserDao extends FilmorateDao<User>{
    Map<Long, List<Long>> getAllLikes();
}
