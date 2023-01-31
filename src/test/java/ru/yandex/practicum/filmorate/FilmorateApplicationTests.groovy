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
import ru.yandex.practicum.filmorate.model.Review
import ru.yandex.practicum.filmorate.model.User
import ru.yandex.practicum.filmorate.service.FilmService
import ru.yandex.practicum.filmorate.service.ReviewService
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
    private ReviewService reviewService

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
        def popularFilms = filmService.getPopularFilms(3, null, null)

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

        userService.addFriend(friendId, userId)
        userService.addFriend(friendOfFriendId, friendId)

        then:
        def eventList = userService.getFeed(userId as Long)
        eventList.size() == 2
        eventList[1].getEventId() == 2L
        eventList[1].getEntityId() == 3L
        eventList[1].getEventType() == "FRIEND"
        eventList[1].getOperation() == "ADD"


    }

    def "Should add event if user like film"() {
        given:
        def film = Film.builder()
                .name("testFilm")
                .description("test")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(199)
                .mpa(Mpa.builder().id(1).build()).build()

        when:
        def filmID = filmService.create(film).getId()
        filmService.setFilmLike(filmID, 2)

        then:
        def eventList = userService.getFeed(1)
        eventList.size() == 3

        eventList[1].getEventId() == 2L
        eventList[1].getEntityId() == 3L
        eventList[1].getEventType() == "FRIEND"
        eventList[1].getOperation() == "ADD"

        eventList[2].getEventId() == 3L
        eventList[2].getEntityId() == 1L
        eventList[2].getEventType() == "LIKE"
        eventList[2].getOperation() == "ADD"
    }

    def "Should add event when remove friend"() {
        when:
        userService.removeFriend(3, 2)

        then:
        def eventList = userService.getFeed(1)
        eventList.size() == 4

        eventList[1].getEventId() == 2L
        eventList[1].getEntityId() == 3L
        eventList[1].getEventType() == "FRIEND"
        eventList[1].getOperation() == "ADD"

        eventList[2].getEventId() == 3L
        eventList[2].getEntityId() == 1L
        eventList[2].getEventType() == "LIKE"
        eventList[2].getOperation() == "ADD"

        eventList[3].getEventId() == 4L
        eventList[3].getEntityId() == 3L
        eventList[3].getEventType() == "FRIEND"
        eventList[3].getOperation() == "REMOVE"
    }

    def "Should add event when remove like from film"() {
        when:
        filmService.removeFilmLike(1, 2)

        then:
        def eventList = userService.getFeed(1)
        eventList.size() == 5

        eventList[1].getEventId() == 2L
        eventList[1].getEntityId() == 3L
        eventList[1].getEventType() == "FRIEND"
        eventList[1].getOperation() == "ADD"

        eventList[2].getEventId() == 3L
        eventList[2].getEntityId() == 1L
        eventList[2].getEventType() == "LIKE"
        eventList[2].getOperation() == "ADD"

        eventList[3].getEventId() == 4L
        eventList[3].getEntityId() == 3L
        eventList[3].getEventType() == "FRIEND"
        eventList[3].getOperation() == "REMOVE"

        eventList[4].getEventId() == 5L
        eventList[4].getEntityId() == 1L
        eventList[4].getEventType() == "LIKE"
        eventList[4].getOperation() == "REMOVE"
    }

    def "Shouldn't add event when try to add unknown friend"() {
        when:
        userService.addFriend(9999, 2)

        then:
        def e = thrown(FilmorateNotFoundException)
        e.message == "Пользователь с id: 2 или id: 9999 не найден."

        def eventList = userService.getFeed(1)
        eventList.size() == 5
    }

    def "Shouldn't add event when try to remove unknown film"() {
        when:
        userService.removeFriend(9999, 2)

        then:
        def e = thrown(FilmorateNotFoundException)
        e.message == "Пользователь не найден."

        def eventList = userService.getFeed(1)
        eventList.size() == 5
    }

    def "Shouldn't add event when try to like unknown film"() {
        when:
        filmService.setFilmLike(9999, 2)

        then:
        def e = thrown(FilmorateNotFoundException)
        e.message == "Фильм с id: 9999 не найден."

        def eventList = userService.getFeed(1)
        eventList.size() == 5
    }

    def "Shouldn't add event when try to dislike unknown film"() {
        when:
        filmService.removeFilmLike(9999, 2)

        then:
        def e = thrown(FilmorateNotFoundException)
        e.message == "Фильм с id: 9999 не найден."

        def eventList = userService.getFeed(1)
        eventList.size() == 5
    }

    def "Should add event when user add review"() {
        given:
        def review = Review.builder()
                .userId(2)
                .filmId(1)
                .content("Test content")
                .isPositive(true).build()

        when:
        userService.getUserBy(2)
        reviewService.create(review).getReviewId()

        then:
        def eventList = userService.getFeed(1)
        eventList.size() == 6
        eventList[5].getEventType() == "REVIEW"
        eventList[5].getOperation() == "ADD"
        eventList[5].getEntityId() == 1
    }

    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["/cleanup.sql", "/populate.sql"])
    def "can add like to review"() {
        when:
        reviewService.addLike(1, 1)

        then:
        def likesCount = reviewService.get(1).getUseful()
        likesCount == 1
    }

    def "cannot add like twice"() {
        when:
        reviewService.addLike(1, 1)

        then:
        def likesCount = reviewService.get(1).getUseful()
        likesCount == 1
    }

    def "can remove like from review"() {
        when:
        reviewService.removeLike(1, 1)

        then:
        def likesCount = reviewService.get(1).getUseful()
        likesCount == 0
    }

    def "cannot remove like twice"() {
        when:
        reviewService.removeLike(1, 1)

        then:
        def likesCount = reviewService.get(1).getUseful()
        likesCount == 0
    }

    def "can add dislike to review"() {
        when:
        reviewService.addDislike(1, 1)

        then:
        def likesCount = reviewService.get(1).getUseful()
        likesCount == -1
    }

    def "cannot add dislike twice"() {
        when:
        reviewService.addDislike(1, 1)

        then:
        def likesCount = reviewService.get(1).getUseful()
        likesCount == -1
    }

    def "can remove dislike from review"() {
        when:
        reviewService.removeDislike(1, 1)

        then:
        def likesCount = reviewService.get(1).getUseful()
        likesCount == 0
    }

    def "cannot remove dislike twice"() {
        when:
        reviewService.removeDislike(1, 1)

        then:
        def likeCount = reviewService.get(1).getUseful()
        likeCount == 0
    }

    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["/cleanup.sql", "/populate.sql"])
    def "should return 200 and list of recommendations"() {
        when:
        def films = userService.getRecommendations(3)

        then:
        with(films) {
            id == [2]
        }
    }

    def "should return 200 and empty list of recommendations"() {
        when:
        def films = userService.getRecommendations(2)

        then:
        films.size() == 0
    }
}