package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userStorage;

    public UserController() { userStorage = new UserService(); }

    @PostMapping
    public User create(@RequestBody User user){
        log.info("User controller get method POST. Data: " + user);
        user = userStorage.create(user);
        return user;
    }

    @GetMapping
    public List<User> findAll() {
        return userStorage.getAllUsers();
    }

    @GetMapping("/{id}")
    public User findUserBy(@PathVariable("id") Integer id) {
        return userStorage.getUserBy(id);
    }
}
