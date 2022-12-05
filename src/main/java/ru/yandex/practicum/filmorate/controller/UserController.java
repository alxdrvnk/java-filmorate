package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    public UserController() {
        userService = new UserService();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info(String.format("UserController: получен POST запрос. Data: %s", user));
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info(String.format("UserController: получен PUT запрос. Data: %s", user));
        return userService.update(user);
    }

    @GetMapping
    public List<User> findAll() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User findUserBy(@PathVariable("id") Integer id) {
        return userService.getUserBy(id);
    }
}
