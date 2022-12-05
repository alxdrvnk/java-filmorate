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

        def expected = Film.builder()
                .id(0)
                .name("Film")
                .description("Film Description")
                .duration(121)
                .releaseDate(LocalDate.of(1977, 5, 25)).build()

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
}
