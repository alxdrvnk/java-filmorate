package ru.yandex.practicum.filmorate.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import ru.yandex.practicum.filmorate.model.User
import spock.lang.Specification

import java.time.LocalDate

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest extends Specification {

    @Autowired
    private MockMvc mvc

    @Autowired
    private ObjectMapper objectMapper

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

    def "Should set name from login value when name is empty"() {
        given:
        def user = User.builder()
                .id(2)
                .birthday(LocalDate.of(1944, 5, 14))
                .login("UserLogin")
                .email("email@email.email").build()
        def expectUser = user.withName("UserLogin")
        expect:
        mvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expectUser)))
    }

    def "Should return 200 and json when add friend"() {
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

        def expectUserPWithFriend = expectUserP.withFriends(Set.of(4L))
        expect:
        userL.getFriends() == Collections.EMPTY_SET
        userP.getFriends() == Collections.EMPTY_SET

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

        mvc.perform(MockMvcRequestBuilders.put("/users/3/friends/4")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expectUserPWithFriend)))

        mvc.perform(MockMvcRequestBuilders.get("/users/4/friends")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(3))))
    }

    def "Sould retunr 200 and user when remove friend"() {
        given:
        def expectUserP = User.builder()
                .id(3)
                .birthday(LocalDate.of(1990, 1, 1))
                .login("Pupa")
                .name("Pupa")
                .email("pupa@mail.mail").build()

        expect:
        mvc.perform(MockMvcRequestBuilders.delete("/users/3/friends/4")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expectUserP)))

        mvc.perform(MockMvcRequestBuilders.get("/users/4/friends")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(Collections.EMPTY_LIST)))
    }

    def "Should return 200 and list of friends IDs"() {
        given:
        def userFriendsList = List.of(1, 4)

        expect:
        mvc.perform(MockMvcRequestBuilders.put("/users/3/friends/4"))
                .andExpect(status().isOk())

        mvc.perform(MockMvcRequestBuilders.put("/users/3/friends/1"))
                .andExpect(status().isOk())

        mvc.perform(MockMvcRequestBuilders.get("/users/3/friends")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(userFriendsList)))

        mvc.perform(MockMvcRequestBuilders.get("/users/4/friends")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(3))))

        mvc.perform(MockMvcRequestBuilders.get("/users/1/friends")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(3))))
    }
    //TODO: Implement ME!!!
    def "Should return 200 and list of mutual friends Ids"() {
        given:
        def mutualFriends = List.of(3)

        expect:
        mvc.perform(MockMvcRequestBuilders.get("/users/4/friends/common/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(mutualFriends)))
    }
}
