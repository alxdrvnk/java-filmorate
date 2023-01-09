package ru.yandex.practicum.filmorate


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage
import ru.yandex.practicum.filmorate.dao.impl.GenreDb
import ru.yandex.practicum.filmorate.dao.impl.MpaDb
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage
import ru.yandex.practicum.filmorate.model.User
import ru.yandex.practicum.filmorate.service.FilmService
import ru.yandex.practicum.filmorate.service.UserService
import spock.lang.Specification

import java.time.LocalDate

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@TestPropertySource(locations = "/application-integrationtest.properties")
class FilmorateApplicationTests extends Specification {

    @Autowired
    private FilmDbStorage filmDbStorage

    @Autowired
    private UserDbStorage userDbStorage

    @Autowired
    private UserService userService

    @Autowired
    private FilmService filmService

    @Autowired
    private GenreDb genreStorage

    @Autowired
    private MpaDb mpaStorage

    def "can get list of user"() {
        when:
        def users = userDbStorage.getAll()

        then:
        with(users) {
            name == ["user1-name", "user2-name", "user3-name"]
        }
    }

    def "can get user by id"() {
        when:
        def user = userDbStorage.getBy(1)

        then:
        with(user.get()) {
            name == "user1-name"
            login == "user1-login"
            email == "user1@email.mail"
            birthday == LocalDate.of(1944, 05, 14)
        }
    }

    def "can insert a user object"() {
        given:
        def user = User.builder()
                .email("user@mail.mail")
                .login("userLogin")
                .birthday(LocalDate.of(2000, 01, 01)).build()
        userDbStorage.create(user)

        when:
        def userDb = userDbStorage.getBy(4)

        then:
        with(userDb.get()) {
            id == 4
            login == "userLogin"
            name == null
        }

    }

    def "can update user"() {
        given:
        def user = userDbStorage.getBy(4)
        def updateUser = user.get().withName("TestName")

        when:
        def userDB = userDbStorage.update(updateUser)

        then:
        with(userDB) {
            id == 4
            login == "userLogin"
            name == "TestName"
            birthday == LocalDate.of(2000,01,01)
            email == "user@mail.mail"
        }
    }

    def "can add friends"() {
        given:
        userService.addFriend(4, 1)
        userService.addFriend(4, 2)

        when:
        def friends = userService.getUserFriends(4)

        then:
        with(friends) {
            id == [1,2]
        }
    }

    def "can get all films"() {
        when:
        def films = filmDbStorage.getAll()

        then:
        with(films) {
            id == [1,2,3]
            name == ["SW", "Indiana Jones and the Raiders of the Lost Ark", "The Shawshank Redemption"]
        }
    }

    def "can get all genres"() {
        when:
        def genres = genreStorage.getAll()

        then:
        with(genres) {
            id == [1,2,3,4,5,6]
            name == ["Комедия", "Драма", "Мультфильм", "Триллер", "Документальный", "Боевик"]
        }
    }

    def "can get all Mpa"() {
        when:
        def mpa = mpaStorage.getAll()

        then:
        with(mpa) {
            id == [1,2,3,4,5]
            name == ["G", "PG", "PG-13", "R", "NC-17"]
        }
    }

    def "can get popular films"() {
        given:
        filmService.setFilmLike(3, 1)
        filmService.setFilmLike(3, 2)
        filmService.setFilmLike(1, 3)

        when:
        def popularFilms = filmService.getPopularFilms(3)

        then:
        with(popularFilms) {
            id == [3,1,2]
        }

    }

    def "can add like to film"() {

    }
}