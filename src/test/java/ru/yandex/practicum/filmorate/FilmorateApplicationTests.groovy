package ru.yandex.practicum.filmorate

import com.github.springtestdbunit.DbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.transaction.TransactionalTestExecutionListener
import ru.yandex.practicum.filmorate.dao.impl.GenreDb
import ru.yandex.practicum.filmorate.dao.impl.MpaDb
import ru.yandex.practicum.filmorate.model.User
import ru.yandex.practicum.filmorate.service.FilmService
import ru.yandex.practicum.filmorate.service.UserService
import spock.lang.Specification

import java.time.LocalDate

@SpringBootTest
@AutoConfigureTestDatabase
@DbUnitConfiguration(databaseConnection = "dbUnit")
class FilmorateApplicationTests extends Specification {

    @Autowired
    private UserService userService

    @Autowired
    private FilmService filmService

    @Autowired
    private GenreDb genreStorage

    @Autowired
    private MpaDb mpaStorage

    def "can add friends"() {
        given:
        def user = User.builder()
                .email("user@mail.mail")
                .login("userLogin")
                .birthday(LocalDate.of(2000, 01, 01)).build()
        userService.create(user)
        userService.addFriend(4, 1)
        userService.addFriend(4, 2)

        when:
        def friends = userService.getUserFriends(4)

        then:
        with(friends) {
            id == [1,2]
        }
    }

    def "can remove user from friends"() {
        given:
        userService.addFriend(1, 4)

        expect:
        def friends = userService.getUserFriends(1)
        with(friends) {
            size() == 1
        }

        when:
        userService.removeFriend(1, 4)

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

    def "can remove like from film"(){
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
            id == [3,1,2]
        }
    }
}