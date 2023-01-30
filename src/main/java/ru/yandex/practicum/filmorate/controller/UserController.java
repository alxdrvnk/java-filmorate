package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmorateValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info(String.format("UserController: create User request. Data: %s", user));

        validateUserBirthday(user);
        user = validateUserName(user);
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info(String.format("UserController: update User request. Data: %s", user));
        validateUserBirthday(user);
        user = validateUserName(user);
        return userService.update(user);
    }

    @GetMapping
    public List<User> findAll() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User findUserBy(@PathVariable("id") Long id) {
        return userService.getUserBy(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Long id, @PathVariable("friendId") Long friendId) {
        userService.addFriend(id, friendId);
        log.info(String.format("UserController: User with %d id add friend with id %d", id, friendId));
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Long id, @PathVariable("friendId") Long friendId) {
        userService.removeFriend(id, friendId);
        log.info(String.format("UserController: User with %d id remove friend with id %d", id, friendId));
    }

    @PutMapping("/{id}/friends/{friedId}/approve")
    public void approveFriend(@PathVariable("id") Long id, @PathVariable("friendId") Long friendId) {
        userService.approveFriend(id, friendId);
        log.info(String.format("UserController: User with id: %d approve friend with id: %d", id, friendId));
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable("id") Long id) {
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable("id") Long userId, @PathVariable("otherId") Long otherUserId) {
        return userService.getMutualFriends(userId, otherUserId);
    }

    private void validateUserBirthday(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new FilmorateValidationException("День рождения не может быть в будущем.");
        }
    }

    private User validateUserName(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user = user.withName(user.getLogin());
        }
        return user;
    }
}
