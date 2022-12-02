package ru.yandex.practicum.filmorate.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import ru.yandex.practicum.filmorate.model.Film
import spock.lang.Specification

import java.time.LocalDate

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = FilmController.class)
class FilmControllerTest extends Specification {

    @Autowired
    private MockMvc mvc

    @Autowired
    private ObjectMapper objectMapper

    def "Should return 404 and message when try get not available film"() {
        expect:
        mvc.perform(MockMvcRequestBuilders.get("/films/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Фильм с id: 1 не найден."))
    }

    def "Should add film to service then return ok and film with id"() {
        given:
        def film = Film.builder()
                .name("Film")
                .description("Film Description")
                .duration(121)
                .releaseDate(new LocalDate(1977, 5, 25)).build()

        def expected = Film.builder()
                .id(0)
                .name("Film")
                .description("Film Description")
                .duration(121)
                .releaseDate(new LocalDate(1977, 5, 25)).build()

        expect:
        mvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)))
    }

    def "Should return error when film duration is negative"() {
        given:
        def film = Film.builder()
            .name("Film")
            .description("Film description")
            .duration(-200)
            .releaseDate(new LocalDate(2000,1,1)).build()

        expect:
        mvc.perform(MockMvcRequestBuilders.post("/films")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(film)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Продолжительность фильма должна быть положительной"))

    }
}
