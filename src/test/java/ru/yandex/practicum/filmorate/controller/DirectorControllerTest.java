package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DirectorControllerTest {
    private final JdbcTemplate jdbcTemplate;
    private final DirectorController directorController;
    private Director director1;
    private Director director2;
    private Director director3;

    private void initDirectors() {
        director1 = Director.builder().id(1).name("Стивен Спилберг").build();
        director2 = Director.builder().id(2).name("Питер Джексон").build();
        director3 = Director.builder().id(3).name("Джеймс Кэмерон").build();
    }

    @Test
    @Sql(value = {"classpath:testDirector/dataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"classpath:testDirector/cleanDataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Получение списка всех директоров")
    void getAllDirectors() {
        initDirectors();
        final int size = 3;
        List<Director> directors = directorController.getAllDirectors();
        assertThat(directors.size() == size).isTrue();
        assertThat(directors.contains(director1)).isTrue();
        assertThat(directors.contains(director2)).isTrue();
        assertThat(directors.contains(director3)).isTrue();
    }

    @Test
    @Sql(value = {"classpath:testDirector/cleanDataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @DisplayName("Получение списка всех директоров, пустая база")
    void getAllDirectorsIsEmptyList() {
        initDirectors();
        final int size = 0;
        List<Director> directors = directorController.getAllDirectors();
        assertThat(directors.size() == size).isTrue();
        assertThat(directors.contains(director1)).isFalse();
        assertThat(directors.contains(director2)).isFalse();
        assertThat(directors.contains(director3)).isFalse();
    }

    @Test
    @Sql(value = {"classpath:testDirector/dataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"classpath:testDirector/cleanDataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Получение директора по id")
    void getDirectorById() {
        initDirectors();
        final int id = 1;
        Director directorForTest = directorController.getDirectorById(id);
        assertThat(directorForTest.equals(director1)).isTrue();
        Optional<Director> addDirectorOptional = Optional.ofNullable(directorForTest);
        assertThat(addDirectorOptional)
                .isPresent()
                .hasValueSatisfying(d -> assertThat(d).hasFieldOrPropertyWithValue("id", directorForTest.getId()))
                .hasValueSatisfying(d -> assertThat(d).hasFieldOrPropertyWithValue("name", directorForTest.getName()));

    }

    @Test
    @Sql(value = {"classpath:testDirector/dataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"classpath:testDirector/cleanDataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Получение директора по несуществующему id")
    void getDirectorByBadId() {
        initDirectors();
        final int id = 999;
        assertThrows(FilmorateNotFoundException.class, () -> {
            directorController.getDirectorById(id);
        });
    }

    @Test
    @Sql(value = {"classpath:testDirector/cleanDataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"classpath:testDirector/cleanDataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Создание директора")
    void createDirector() {
       initDirectors();
        Director directorCreate = directorController.createDirector(director1);
        Optional<Director> addDirectorOptional = Optional.ofNullable(directorCreate);
        assertThat(addDirectorOptional)
                .isPresent()
                .hasValueSatisfying(d -> assertThat(d).hasFieldOrPropertyWithValue("id", directorCreate.getId()))
                .hasValueSatisfying(d -> assertThat(d).hasFieldOrPropertyWithValue("name", directorCreate.getName()));
        final int id = 1;
        Director directorDatabase = directorController.getDirectorById(id);
        assertThat(directorCreate.equals(directorDatabase)).isTrue();
    }
    @Test
    @Sql(value = {"classpath:testDirector/dataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"classpath:testDirector/cleanDataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Обновление директора")
    void updateDirector() {
      initDirectors();
        final int id = 1;
        Director directorDatabase = directorController.getDirectorById(id);
        assertThat(director1.equals(directorDatabase)).isTrue();
        director1 = Director.builder().id(1).name("!!!Steven Spielberg!!!").build();
        Director updateDirector = directorController.updateDirector(director1);
        Director directorDatabaseNew = directorController.getDirectorById(id);
        assertThat(updateDirector.equals(directorDatabaseNew)).isTrue();
        assertThat(director1.equals(updateDirector)).isTrue();
        assertThat(director1.equals(directorDatabaseNew)).isTrue();
    }

    @Test
    @Sql(value = {"classpath:testDirector/dataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"classpath:testDirector/cleanDataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Удаление директора")
    void deleteDirectorById() {
        initDirectors();
        final int id = 1;
        List<Director> directors = directorController.getAllDirectors();
        assertThat(directors.contains(director1)).isTrue();
        assertThat(directors.contains(director2)).isTrue();
        assertThat(directors.contains(director3)).isTrue();
        directorController.deleteDirectorById(id);
        List<Director> directorsDelete = directorController.getAllDirectors();
        assertThat(directorsDelete.contains(director1)).isFalse();
        assertThat(directorsDelete.contains(director2)).isTrue();
        assertThat(directorsDelete.contains(director3)).isTrue();

    }
    @Test
    @Sql(value = {"classpath:testDirector/dataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"classpath:testDirector/cleanDataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Удаление директора c неверным id")
    void deleteDirectorByBadId() {
        final int id = 999;
        assertThrows(FilmorateNotFoundException.class, () -> {
            directorController.deleteDirectorById(id);
        });
    }
}