package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final List<User> userStorage;

    public UserController() { userStorage = new ArrayList<>(); }

    @PostMapping
    public User create(@RequestBody User user){
        log.info("User controller get method POST. Data: " + user);
        userStorage.add(user);
        return user;
    }

    @GetMapping
    public List<User> findAll() {
        return userStorage;
    }

    @GetMapping("/{id}")
    public User findUserBy(@PathVariable("id") Integer id) {
        return userStorage.get(id);
    }
}
