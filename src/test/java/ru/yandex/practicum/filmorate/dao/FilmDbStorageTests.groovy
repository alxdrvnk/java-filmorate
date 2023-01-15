package ru.yandex.practicum.filmorate.dao

import com.github.springtestdbunit.DbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import com.github.springtestdbunit.annotation.ExpectedDatabase
import com.github.springtestdbunit.assertion.DatabaseAssertionMode
import org.springframework.beans.factory.annotation.Autowire
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.support.DirtiesContextTestExecutionListener
import org.springframework.test.context.transaction.TransactionalTestExecutionListener
import org.springframework.transaction.annotation.Transactional
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage
import ru.yandex.practicum.filmorate.model.Film
import ru.yandex.practicum.filmorate.model.Mpa
import spock.lang.Specification

import java.time.LocalDate
@SpringBootTest
@AutoConfigureTestDatabase
@TestExecutionListeners([ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class ])
@DbUnitConfiguration(databaseConnection = "dbUnit")
class FilmDbStorageTests extends Specification {

    @Autowired
    private FilmDbStorage filmDbStorage

    @ExpectedDatabase(value = "/dbunit/data.xml", table = "films", assertionMode = DatabaseAssertionMode.NON_STRICT)
    def "can insert a film object"(){
        given:
        def film = Film.builder()
        .name("Test Film Name")
        .description("Test film description")
        .releaseDate(LocalDate.of(2000,01,01))
        .duration(100)
        .mpa(Mpa.builder().id(1).build()).build()

        def filmDB= filmDbStorage.create(film)

        expect:
        with(filmDB){
            id == 1
            name == "Test Film Name"
            description == "Test film description"
            releaseDate == LocalDate.of(2000,01,01)
            mpa.id == 1
            mpa.name == null
        }
    }

    def "can get film by id" () {

        given:
        def film = filmDbStorage.getBy(1)

        expect:
        with(film.get()) {
            name == "Test Film Name"
        }

    }

    @ExpectedDatabase(value = "/dbunit/update-film.xml", table = "films")
    def "can update film"(){

        given:
        def newMpa = Mpa.builder()
        .id(3).build()
        def film = filmDbStorage.getBy(1)
        def filmUpdate = filmDbStorage.update(film.get().withMpa(newMpa))

        expect:
        with(filmUpdate){
            filmUpdate.mpa.id == 3
        }
    }

    def "can get all films" () {

        when:
        def films = filmDbStorage.getAll()

        then:
        with(films) {
            id == [1,2,3,4]
            name == ["SW", "Indiana Jones and the Raiders of the Lost Ark", "The Shawshank Redemption", "Test Film Name"]
        }
    }

    def "can delete film by id" (){

        when:
        filmDbStorage.deleteBy(4)

        then:
        def films = filmDbStorage.getAll()
        with(films) {
            id == [1,2,3]
        }
    }
}