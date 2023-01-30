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
import ru.yandex.practicum.filmorate.model.Director
import ru.yandex.practicum.filmorate.model.Film
import ru.yandex.practicum.filmorate.model.Genre
import ru.yandex.practicum.filmorate.model.Mpa
import spock.lang.Specification

import java.time.LocalDate

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureTestDatabase
@TestPropertySource(locations = "/application-integrationtest.properties")
class FilmControllerTest extends Specification {

    @Autowired
    private MockMvc mvc

    @Autowired
    private ObjectMapper objectMapper

    def "Should return code 400 when try get not available film"() {
        expect:
        mvc.perform(MockMvcRequestBuilders.get("/films/9999"))
                .andExpect(status().isNotFound())
    }

    def "Should add film to service then return code 200 and film with id"() {
        given:
        def film = Film.builder()
                .name("Film")
                .description("Film Description")
                .duration(121)
                .releaseDate(LocalDate.of(1977, 5, 25))
                .mpa(Mpa.builder().id(1).name("G").build()).build()

        def expected = film.withId(1)

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
        mvc.perform(MockMvcRequestBuilders.put("/films/1/like/1"))
                .andExpect(status().isOk())
    }

    def "Should return code 200 when user delete like"() {
        expect:
        mvc.perform(MockMvcRequestBuilders.delete("/films/1/like/1"))
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

    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["/cleanup.sql", "/populate.sql"])
    def "Should return film when get popular films with filter by genre"() {
        given:
        Film film = Film.builder()
                .id(3)
                .name("The Shawshank Redemption")
                .description("The Shawshank Redemption description")
                .releaseDate(LocalDate.of(1994, 9, 10))
                .duration(142)
                .rate(0)
                .mpa(Mpa.builder()
                        .id(1)
                        .name("G")
                        .build())
                .genres(List.of(Genre.builder()
                        .id(2)
                        .name("Драма")
                        .build()))
                .build()


        expect:
        mvc.perform(MockMvcRequestBuilders.get("/films/popular?genreId=2"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(film))))
    }

    def "Should return film when get popular films with filter by year"() {
        given:
        Film film = Film.builder()
                .id(1)
                .name("SW")
                .description("SW description")
                .releaseDate(LocalDate.of(1977, 5, 25))
                .duration(121)
                .rate(0)
                .mpa(Mpa.builder()
                        .id(2)
                        .name("PG")
                        .build())
                .genres(List.of(Genre.builder()
                        .id(6)
                        .name("Боевик")
                        .build()))
                .build()


        expect:
        mvc.perform(MockMvcRequestBuilders.get("/films/popular?year=1977"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(film))))
    }

    def "Should return empty list when all movies don't meet filter requirements"() {
        expect:
        mvc.perform(MockMvcRequestBuilders.get("/films/popular?year=1977&genreId=1"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(Collections.emptyList())))
    }
}
