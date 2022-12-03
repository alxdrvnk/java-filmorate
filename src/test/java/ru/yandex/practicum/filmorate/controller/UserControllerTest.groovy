package ru.yandex.practicum.filmorate.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import ru.yandex.practicum.filmorate.model.User
import spock.lang.Specification

import java.time.LocalDate

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = UserController.class)
class UserControllerTest extends Specification {

    @Autowired
    private MockMvc mvc

    @Autowired
    private ObjectMapper objectMapper

    def "Should add user then return code 200 and json object"() {
        given:
        def user = User.builder()
                .id(0)
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
        def user = User.builder()
                .id(1)
                .name("")
                .birthday(LocalDate.of(1944, 5, 14))
                .login("UserLogin")
                .email("email@email.email").build()
        expect:
        user.setName("UserLogin")
        mvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(user)))
    }
}
