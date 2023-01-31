package ru.yandex.practicum.filmorate.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import ru.yandex.practicum.filmorate.model.Review
import spock.lang.Specification

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureTestDatabase
@TestPropertySource(locations = "/application-integrationtest.properties")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["/cleanup.sql", "/populate.sql"])
class ReviewControllerTest extends Specification {
    @Autowired
    private MockMvc mvc

    @Autowired
    private ObjectMapper objectMapper

    def "Should add review then return code 200 and json object"() {
        given:
        def review = Review.builder()
                .reviewId(4)
                .userId(1)
                .filmId(1)
                .content("test content")
                .isPositive(true)
                .build()

        expect:
        mvc.perform(MockMvcRequestBuilders.post("/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(review)))
    }

    def "Should add like to review then return code 200"() {
        given:
        mvc.perform(MockMvcRequestBuilders.put("/reviews/1/like/1"))
                .andExpect(status().isOk())
    }

    def "Should add dislike to review then return code 200"() {
        given:
        mvc.perform(MockMvcRequestBuilders.put("/reviews/1/dislike/1"))
                .andExpect(status().isOk())
    }

    def "Should remove like from review then return code 200"() {
        given:
        mvc.perform(MockMvcRequestBuilders.delete("/reviews/1/like/1"))
                .andExpect(status().isOk())
    }

    def "Should remove dislike from review then return code 200"() {
        given:
        mvc.perform(MockMvcRequestBuilders.delete("/reviews/1/dislike/1"))
                .andExpect(status().isOk())
    }

    def "Should return code 404 when userId is incorrect"() {
        given:
        def review = Review.builder()
                .reviewId(1)
                .userId(-1)
                .filmId(1)
                .content("test content")
                .isPositive(false)
                .build()

        expect:
        mvc.perform(MockMvcRequestBuilders.post("/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isNotFound())
    }

    def "Should return code 400 when userId is null"() {
        given:
        def review = Review.builder()
                .reviewId(1)
                .userId(null)
                .filmId(1)
                .content("test content")
                .isPositive(false)
                .build()

        expect:
        mvc.perform(MockMvcRequestBuilders.post("/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isBadRequest())
    }

    def "Should return code 404 when filmId is incorrect"() {
        given:
        def review = Review.builder()
                .reviewId(1)
                .userId(1)
                .filmId(-1)
                .content("test content")
                .isPositive(false)
                .build()

        expect:
        mvc.perform(MockMvcRequestBuilders.post("/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isNotFound())
    }

    def "Should return code 400 when filmId is null"() {
        given:
        def review = Review.builder()
                .reviewId(1)
                .userId(1)
                .filmId(null)
                .content("test content")
                .isPositive(false)
                .build()

        expect:
        mvc.perform(MockMvcRequestBuilders.post("/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isBadRequest())
    }

    def "Should return code 400 when content is null"() {
        given:
        def review = Review.builder()
                .reviewId(1)
                .userId(1)
                .filmId(1)
                .content(null)
                .isPositive(false)
                .build()
        expect:
        mvc.perform(MockMvcRequestBuilders.post("/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isBadRequest())
    }

    def "Should return code 400 when content is blank"() {
        given:
        def review = Review.builder()
                .reviewId(1)
                .userId(1)
                .filmId(1)
                .content("")
                .isPositive(false)
                .build()
        expect:
        mvc.perform(MockMvcRequestBuilders.post("/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isBadRequest())
    }

    def "Should return code 400 when isPositive is null"() {
        given:
        def review = Review.builder()
                .reviewId(1)
                .userId(1)
                .filmId(1)
                .content("test content")
                .isPositive(null)
                .build()
        expect:
        mvc.perform(MockMvcRequestBuilders.post("/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isBadRequest())
    }
}
