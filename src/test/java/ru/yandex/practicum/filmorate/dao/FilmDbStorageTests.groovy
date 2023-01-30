package ru.yandex.practicum.filmorate.dao


import com.github.springtestdbunit.annotation.ExpectedDatabase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.jdbc.Sql
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage
import ru.yandex.practicum.filmorate.model.Film
import ru.yandex.practicum.filmorate.model.Mpa
import spock.lang.Specification

import java.time.LocalDate

@SpringBootTest
@TestPropertySource(locations = "/application-integrationtest.properties")
@AutoConfigureTestDatabase
class FilmDbStorageTests extends Specification {

    @Autowired
    private FilmDbStorage filmDbStorage

    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["/cleanup.sql", "/populate.sql"])
    def "can insert a film object"(){
        given:
        def film = Film.builder()
                .name("Test Film Name")
                .description("Test film description")
                .releaseDate(LocalDate.of(2000, 01, 01))
                .duration(100)
                .mpa(Mpa.builder().id(1).build()).build()

        def filmId = filmDbStorage.create(film).getId()

        def filmDB = filmDbStorage.getBy(filmId)

        expect:
        with(filmDB.get()) {
            id == 4
            name == "Test Film Name"
            description == "Test film description"
            releaseDate == LocalDate.of(2000, 01, 01)
            mpa.id == 1
            mpa.name == "G"
        }
    }

    def "can get film by id"() {

        given:
        def film = filmDbStorage.getBy(1)

        expect:
        with(film.get()) {
            name == "SW"
        }

    }

    def "can update film"(){

        given:
        def newMpa = Mpa.builder()
                .id(3).build()
        def film = filmDbStorage.getBy(1)
        def filmUpdate = filmDbStorage.update(film.get().withMpa(newMpa))

        expect:
        with(filmUpdate) {
            filmUpdate.mpa.id == 3
        }
    }

    def "can get all films"() {

        when:
        def films = filmDbStorage.getAll()

        then:
        with(films) {
            id == [1, 2, 3, 4]
            name == ["SW", "Indiana Jones and the Raiders of the Lost Ark", "The Shawshank Redemption", "Test Film Name"]
        }
    }

    def "can delete film by id"() {

        when:
        filmDbStorage.deleteBy(4)

        then:
        def films = filmDbStorage.getAll()
        with(films) {
            id == [1, 2, 3]
        }
    }
}