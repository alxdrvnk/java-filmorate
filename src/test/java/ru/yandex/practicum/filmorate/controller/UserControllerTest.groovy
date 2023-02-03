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
import ru.yandex.practicum.filmorate.model.User
import spock.lang.Specification

import java.time.LocalDate

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureTestDatabase
@TestPropertySource(locations = "/application-integrationtest.properties")
class UserControllerTest extends Specification {

    @Autowired
    private MockMvc mvc

    @Autowired
    private ObjectMapper objectMapper

    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    def "Should add user then return code 200 and json object"() {
        given:
        def user = User.builder()
                .id(1)
                .name("User")
                .birthday(LocalDate.of(1944, 5, 14))
                .login("UserLogin")
                .email("email@email.mail").build()

        expect:
        mvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)))

    }

    def "Should return code 400 when add user with incorrect email"() {
        given:
        def user = User.builder()
                .id(0)
                .name("User")
                .birthday(LocalDate.of(1944, 5, 14))
                .login("UserLogin")
                .email("email.email@").build()
        expect:
        mvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
    }

    def "Should return code 400 when add user with birthday in future"() {
        given:
        def user = User.builder()
                .id(0)
                .name("User")
                .birthday(LocalDate.now().plusDays(1))
                .login("UserLogin")
                .email("email@email.email").build()
        expect:
        mvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
    }

    def "Should return 200 and user with login name when name is empty"() {
        given:
        def user = User.builder()
                .id(2)
                .birthday(LocalDate.of(1944, 5, 14))
                .login("User_Login")
                .email("email@email.email").build()
        def expectUser = user.withName("User_Login")

        expect:
        mvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expectUser)))
    }

    def "Should return 200 when add friend"() {
        given:
        def userP = User.builder()
                .birthday(LocalDate.of(1990, 1, 1))
                .login("Pupa")
                .email("pupa@mail.mail").build()

        def userL = User.builder()
                .birthday(LocalDate.of(1990, 1, 2))
                .login("Lupa")
                .email("lupa@nemail.mail").build()

        def expectUserP = userP.withId(3).withName("Pupa")
        def expectUserL = userL.withId(4).withName("Lupa")

        expect:
        mvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userP)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expectUserP)))

        mvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userL)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expectUserL)))

        mvc.perform(MockMvcRequestBuilders.put("/users/3/friends/4"))
                .andExpect(status().isOk())

        mvc.perform(MockMvcRequestBuilders.get("/users/3/friends"))
                .andExpect(status().isOk())

        mvc.perform(MockMvcRequestBuilders.get("/users/4/friends"))
                .andExpect(status().isOk())
    }

    def "Should return 200 when remove friend"() {
        expect:
        mvc.perform(MockMvcRequestBuilders.delete("/users/3/friends/4"))
                .andExpect(status().isOk())

        mvc.perform(MockMvcRequestBuilders.get("/users/4/friends"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(Collections.EMPTY_LIST)))
    }

    def "Should return 200 and list of friends IDs"() {
        expect:
        mvc.perform(MockMvcRequestBuilders.put("/users/3/friends/4"))
                .andExpect(status().isOk())

        mvc.perform(MockMvcRequestBuilders.put("/users/3/friends/1"))
                .andExpect(status().isOk())

        mvc.perform(MockMvcRequestBuilders.get("/users/3/friends"))
                .andExpect(status().isOk())

        mvc.perform(MockMvcRequestBuilders.get("/users/4/friends"))
                .andExpect(status().isOk())
    }

    def "Should return 200 and list of mutual friends Ids"() {
        expect:
        mvc.perform(MockMvcRequestBuilders.get("/users/4/friends/common/1"))
                .andExpect(status().isOk())
    }

    def "Should return 404 when try to add non-exists user to friend"() {
        expect:
        mvc.perform(MockMvcRequestBuilders.put("/users/3/friends/9999"))
                .andExpect(status().isNotFound())
    }

    def "Should return 404 when try to delete non-exists user from friend"() {
        expect:
        mvc.perform(MockMvcRequestBuilders.delete("/users/3/friends/9999"))
                .andExpect(status().isNotFound())
    }

    def "Should delete user by id then return code 200"(){
        expect:
        mvc.perform(MockMvcRequestBuilders.delete("/users/2"))
                .andExpect(status().isOk())
    }

    def "should return 200 and list of recommendation"() {
        expect:
        mvc.perform(MockMvcRequestBuilders.get("/users/1/recommendations"))
                .andExpect(status().isOk())
    }
}
