package ru.yandex.practicum.filmorate.dao


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.jdbc.Sql
import ru.yandex.practicum.filmorate.FilmorateApplication
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage
import ru.yandex.practicum.filmorate.model.User
import spock.lang.Specification

import java.time.LocalDate


@SpringBootTest
@AutoConfigureTestDatabase
@TestPropertySource(locations = "/application-integrationtest.properties")
class UserDbStorageTests extends Specification {

    @Autowired
    private UserDbStorage userDbStorage

    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["/cleanup.sql", "/populate.sql"])
    def "can get list of user"() {
        when:
        def users = userDbStorage.getAll()

        then:
        with(users) {
            name == ["user1-name", "user2-name", "user3-name"]
        }
    }

    def "can get user by id"() {
        when:
        def user = userDbStorage.getBy(1)

        then:
        with(user.get()) {
            name == "user1-name"
            login == "user1-login"
            email == "user1@email.mail"
            birthday == LocalDate.of(1944, 05, 14)
        }
    }

    def "can insert a user object"() {
        given:
        def user = User.builder()
                .email("user@mail.mail")
                .login("userLogin")
                .birthday(LocalDate.of(2000, 01, 01)).build()
        userDbStorage.create(user)

        when:
        def userDb = userDbStorage.getBy(4)

        then:
        with(userDb.get()) {
            id == 4
            login == "userLogin"
            name == null
        }

    }

    def "can update user"() {
        given:
        def user = userDbStorage.getBy(4)
        def updateUser = user.get().withName("TestName")

        when:
        def userDB = userDbStorage.update(updateUser)

        then:
        with(userDB) {
            id == 4
            login == "userLogin"
            name == "TestName"
            birthday == LocalDate.of(2000,01,01)
            email == "user@mail.mail"
        }
    }

    def "can delete user by id" () {
        when:
        userDbStorage.deleteBy(4)

        then:
        def users = userDbStorage.getAll()

        with(users){
            id == [1,2,3]
        }
    }
}