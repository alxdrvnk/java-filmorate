package ru.yandex.practicum.filmorate.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import ru.yandex.practicum.filmorate.model.Film
import spock.lang.Specification

import java.time.LocalDate

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest extends Specification {

    @Autowired
    private MockMvc mvc

    @Autowired
    private ObjectMapper objectMapper

    def "Should return code 400 when try get not available film"() {
        expect:
        mvc.perform(MockMvcRequestBuilders.get("/films/1"))
                .andExpect(status().isNotFound())
    }

    def "Should add film to service then return code 200 and film with id"() {
        given:
        def film = Film.builder()
                .name("Film")
                .description("Film Description")
                .duration(121)
                .releaseDate(LocalDate.of(1977, 5, 25)).build()

        def expected = film.withId(1).withRate(0)

        expect:
        mvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)))
    }

    def "Should return code 400 when film duration is negative"() {
        given:
        def film = Film.builder()
                .name("Film")
                .description("Film description")
                .duration(-200)
                .releaseDate(LocalDate.of(2000, 1, 1)).build()

        expect:
        mvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
    }

    def "Should return code 400 when add film with description more then 200 symbols"() {
        given:
        def film = Film.builder()
                .name("Film")
                .description("A" * 201)
                .duration(1)
                .releaseDate(LocalDate.of(2000, 1, 1)).build()
        expect:
        mvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
    }

    def "Should return code 400 when add film with release date earlier than 1895-12-28"() {
        given:
        def film = Film.builder()
                .name("Film")
                .description("Film description")
                .duration(1)
                .releaseDate(LocalDate.of(1895, 12, 27)).build()
        expect:
        mvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
    }

    def "Should return code 200 when user put like"() {
        expect:
        mvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())

        mvc.perform(MockMvcRequestBuilders.put("/films/2/like/3"))
                .andExpect(status().isOk())
    }

    def "Should return code 200 when user delete like"() {
        expect:
        mvc.perform(MockMvcRequestBuilders.delete("/films/2/like/3"))
                .andExpect(status().isOk())
    }

    def "Should return code 200 and list of 5 films when get popular films"() {
        given:
        def filmsList = new ArrayList()
        for (int i = 1; i < 11; i++) {
            filmsList.add(
                    Film.builder()
                            .id(i + 2)
                            .name("Film " + i)
                            .description("Film " + i + "description")
                            .duration(i * 20)
                            .releaseDate(LocalDate.of(2000 + i, i, i)).build())
        }
        expect:
        for (final def i in filmsList) {
            mvc.perform(MockMvcRequestBuilders.post("/films")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(i)))
                    .andExpect(status().isOk())
        }

        mvc.perform(MockMvcRequestBuilders.get("/films/popular").param("count", "5"))
                .andExpect(status().isOk())
    }

    def "Should return code 200 and list of 10 films when send request without params"() {
        given:
        def expectFilmsList = new ArrayList()
        for (int i = 1; i < 11; i++) {
            expectFilmsList.add(
                    Film.builder()
                            .id(i + 2)
                            .name("Film " + i)
                            .description("Film " + i + "description")
                            .duration(i * 20)
                            .releaseDate(LocalDate.of(2000 + i, i, i)).build())
        }
        expect:
        mvc.perform(MockMvcRequestBuilders.get("/films/popular"))
                .andExpect(status().isOk())
    }

    def "Should return 404 when non-expect user put like to film"() {
        expect:
        mvc.perform(MockMvcRequestBuilders.put("/films/2/like/9999"))
                .andExpect(status().isNotFound())
    }
}
