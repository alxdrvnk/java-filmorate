package ru.yandex.practicum.filmorate


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.jdbc.Sql
import ru.yandex.practicum.filmorate.dao.impl.GenreDb
import ru.yandex.practicum.filmorate.dao.impl.MpaDb
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException
import ru.yandex.practicum.filmorate.model.Film
import ru.yandex.practicum.filmorate.model.Mpa
import ru.yandex.practicum.filmorate.model.User
import ru.yandex.practicum.filmorate.service.FilmService
import ru.yandex.practicum.filmorate.service.UserService
import spock.lang.Specification

import java.time.LocalDate

@SpringBootTest
@AutoConfigureTestDatabase
@TestPropertySource(locations = "/application-integrationtest.properties")
class FilmorateApplicationTests extends Specification {

    @Autowired
    private UserService userService

    @Autowired
    private FilmService filmService

    @Autowired
    private GenreDb genreStorage

    @Autowired
    private MpaDb mpaStorage

    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["/cleanup.sql", "/populate.sql"])
    def "can add friends"() {
        given:
        def user = User.builder()
                .email("user@mail.mail")
                .login("userLogin")
                .birthday(LocalDate.of(2000, 01, 01)).build()
        userService.create(user)
        userService.addFriend(1, 4)
        userService.addFriend(2, 4)

        when:
        def friends = userService.getUserFriends(4)

        then:
        with(friends) {
            id == [1, 2]
        }
    }

    def "can remove user from friends"() {
        given:
        userService.addFriend(4, 1)

        expect:
        def friends = userService.getUserFriends(1)
        with(friends) {
            size() == 1
        }

        when:
        userService.removeFriend(4, 1)

        then:
        def friendsUpdate = userService.getUserFriends(1)
        with(friendsUpdate) {
            size() == 0
        }
    }

    def "can get all genres"() {
        when:
        def genres = genreStorage.getAll()

        then:
        with(genres) {
            id == [1, 2, 3, 4, 5, 6]
            name == ["Комедия", "Драма", "Мультфильм", "Триллер", "Документальный", "Боевик"]
        }
    }

    def "can get all Mpa"() {
        when:
        def mpa = mpaStorage.getAll()

        then:
        with(mpa) {
            id == [1, 2, 3, 4, 5]
            name == ["G", "PG", "PG-13", "R", "NC-17"]
        }
    }

    def "can add like to film"() {

        given:
        def likesCount = filmService.getFilmBy(3).getRate()

        expect:
        likesCount == 0

        when:
        filmService.setFilmLike(3, 1)

        then:
        def likesCountUpdate = filmService.getFilmBy(3).getRate()
        likesCountUpdate == 1

    }

    def "can remove like from film"() {
        given:
        def likesCount = filmService.getFilmsLikesCount(3)

        expect:
        likesCount == 1

        when:
        filmService.removeFilmLike(3, 1)

        then:
        def likesCountUpdate = filmService.getFilmsLikesCount(3)
        likesCountUpdate == 0
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
            id == [3, 1, 2]
        }
    }

    def "should return 404 when deleting unknown film from database"() {
        when:
        filmService.deleteFilmBy(9999)
        then:
        def e = thrown(FilmorateNotFoundException)
        e.message == "Фильм с id: 9999 не найден."
    }

    def "should return 404 when updating unknown film"() {
        given:
        def film = Film.builder()
                .id(9999)
                .name("Unknown")
                .description("None")
                .rate(9)
                .duration(10)
                .releaseDate(LocalDate.of(2000, 01, 01))
                .mpa(Mpa.builder().id(1).build()).build()

        when:
        filmService.update(film)

        then:
        def e = thrown(FilmorateNotFoundException)
        e.message == "Фильм с id: 9999 не найден."
    }

    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["/cleanup.sql"])
    def "Should add event if user add friend"() {
        given:
        def user = User.builder()
                .name("userName")
                .login("loginUser")
                .email("test@mail.test")
                .birthday(LocalDate.of(2000, 1, 1))
                .build()
        def friendUser = User.builder()
                .name("friendUserName")
                .login("friendLogin")
                .email("friend@mail.test")
                .birthday(LocalDate.of(2000, 2, 2))
                .build()
        def friendOfFriendUser = User.builder()
                .name("friendOfFriendUserName")
                .login("friendOfFriendLogin")
                .email("FoF@mail.test")
                .birthday(LocalDate.of(2000, 3, 3))
                .build()

        when:
        def userId = userService.create(user).getId()
        def friendId = userService.create(friendUser).getId()
        def friendOfFriendId = userService.create(friendOfFriendUser).getId()
        userService.addFriend(friendId as Long, userId as Long)
        userService.addFriend(friendOfFriendId as Long, friendId as Long)

        then:
        def eventList = userService.getFeed(userId as Long)
        eventList.size() == 1

    }
}