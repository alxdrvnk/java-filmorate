package ru.yandex.practicum.filmorate.dao

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.jdbc.Sql
import ru.yandex.practicum.filmorate.model.Review
import spock.lang.Specification

@SpringBootTest
@AutoConfigureTestDatabase
@TestPropertySource(locations = "/application-integrationtest.properties")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["/cleanup.sql", "/populate.sql"])
class ReviewDbStorageTests extends Specification {
    @Autowired
    private ReviewDao reviewDao

    def "Can get list of reviews"() {
        when:
        def reviews = reviewDao.getAll()

        then:
        with(reviews) {
            content == ["test content 1", "test content 2", "test content 3"]
        }
    }

    def "Can get list of reviews by film id 1"() {
        when:
        def reviews = reviewDao.getByFilm(1, 3)

        then:
        with(reviews) {
            content == ["test content 1", "test content 3"]
        }
    }

    def "Can get review by id"() {
        when:
        def review = reviewDao.getBy(1)

        then:
        with(review.get()) {
            userId == 1
            filmId == 1
            content == "test content 1"
            isPositive == true
            useful == 0
        }
    }

    def "Can insert new review"() {
        given:
        def review = Review.builder()
                .userId(1)
                .filmId(3)
                .content("test")
                .isPositive(false)
                .useful(0)
                .build()
        reviewDao.create(review)

        when:
        def fromDb = reviewDao.getBy(4)

        then:
        with(fromDb.get()) {
            userId == 1
            filmId == 3
            content == "test"
            isPositive == false
            useful == 0
        }
    }

    def "Can update review"() {
        given:
        def review = Review.builder()
                .reviewId(1)
                .userId(1)
                .filmId(1)
                .content("updated content")
                .isPositive(false)
                .useful(0)
                .build()

        when:
        def updated = reviewDao.update(review)

        then:
        with(updated) {
            reviewId == 1
            userId == 1
            filmId == 1
            content == "updated content"
            isPositive == false
            useful == 0
        }
    }

    def "Can delete review"() {
        when:
        reviewDao.deleteBy(1)

        then:
        def reviews = reviewDao.getAll()

        with(reviews) {
            reviewId == [2, 3]
        }
    }
}
