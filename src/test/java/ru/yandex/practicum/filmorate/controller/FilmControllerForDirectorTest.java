package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmControllerForDirectorTest {
    private final FilmController filmController;
    private final FilmDao filmDao;
    private Director director1;
    private Director director2;
    private Film film1;

    private void initDirectors() {
        director1 = Director.builder().id(1).name("Стивен Спилберг").build();
        director2 = Director.builder().id(2).name("Питер Джексон").build();
    }

    private void initFilms() {
        film1 = Film.builder()
                .id(1L)
                .name("Фильм-1")
                .description("film1")
                .releaseDate(LocalDate.parse("1987-02-01"))
                .duration(50)
                .rate(5)
                .mpa(Mpa.builder()
                        .id(1L)
                        .name("G")
                        .build())
                .genres(new ArrayList<>(Arrays.asList(Genre.builder().id(2L).name("Драма").build(),
                        Genre.builder().id(6L).name("Боевик").build())))
                .directors(new ArrayList<>(Arrays.asList(director1, director2)))
                .build();
    }

    @Test
    @Sql(value = {"classpath:testDirector/dataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"classpath:testDirector/cleanDataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Проверка списка директоров при поиске фильма по id ")
    void findFilmByIdWithADirectorsTest() {
        initDirectors();
        initFilms();
        Long id = 1L;
        Film film = filmController.findFilmBy(id);
        assertThat(film.equals(film1)).isTrue();
        assertThat(film.getDirectors().contains(director1)).isTrue();
        assertThat(film.getDirectors().contains(director2)).isTrue();
    }

    @Test
    @Sql(value = {"classpath:testDirector/dataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"classpath:testDirector/cleanDataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Проверка списка директоров при обновлении фильма")
    void updateFilmDirectorTest() {
        initDirectors();
        initFilms();
        Long id = 1L;
        int size2 = 2;
        int size0 = 0;
        Film film = filmController.findFilmBy(id);
        assertThat(film.getDirectors().size() == size2).isTrue();
        assertThat(film.getDirectors().contains(director1)).isTrue();
        assertThat(film.getDirectors().contains(director2)).isTrue();
        filmDao.deleteDirectorForFilm(id);
        Film filmEmptyListDirectors = filmController.findFilmBy(id);
        assertThat(filmEmptyListDirectors.getDirectors().isEmpty()).isTrue();
        assertThat(filmEmptyListDirectors.getDirectors().size() == size0).isTrue();
        filmDao.addDirectorForFilm(film1);
        Film filmUpdate = filmController.findFilmBy(id);
        assertThat(filmUpdate.equals(film1)).isTrue();
        assertThat(filmUpdate.getDirectors().contains(director1)).isTrue();
        assertThat(filmUpdate.getDirectors().contains(director2)).isTrue();
    }

    @Test
    @Sql(value = {"classpath:testDirector/dataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"classpath:testDirector/cleanDataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Получение всех фильмов")
    void findAllFilmDirectorTest() {
        initDirectors();
        final int index0 = 0;
        final int index1 = 1;
        final int index2 = 2;
        final int index3 = 3;
        final int index4 = 4;
        final int index5 = 5;
        final int index6 = 6;
        List<Film> allFilms = filmController.findAll();
        assertThat(allFilms.get(index0).getDirectors().contains(director1)).isTrue();
        assertThat(allFilms.get(index0).getDirectors().contains(director2)).isTrue();
        assertThat(allFilms.get(index1).getDirectors().contains(director2)).isTrue();
        assertThat(allFilms.get(index2).getDirectors().contains(director1)).isTrue();
        assertThat(allFilms.get(index3).getDirectors().isEmpty()).isTrue();
        assertThat(allFilms.get(index4).getDirectors().isEmpty()).isTrue();
        assertThat(allFilms.get(index5).getDirectors().isEmpty()).isTrue();
        assertThat(allFilms.get(index6).getDirectors().isEmpty()).isTrue();
    }

    @Test
    @Sql(value = {"classpath:testDirector/dataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"classpath:testDirector/cleanDataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Удаление директоров у фильма фильма")
    void deleteFilmByDirectorTest() {
        initDirectors();
        initFilms();
        Long id = 1L;
        int size2 = 2;
        int size0 = 0;
        Film film = filmController.findFilmBy(id);
        assertThat(film.getDirectors().size() == size2).isTrue();
        assertThat(film.getDirectors().contains(director1)).isTrue();
        assertThat(film.getDirectors().contains(director2)).isTrue();
        filmDao.deleteDirectorForFilm(id);
        Film filmEmptyListDirectors = filmController.findFilmBy(id);
        assertThat(filmEmptyListDirectors.getDirectors().isEmpty()).isTrue();
        assertThat(filmEmptyListDirectors.getDirectors().size() == size0).isTrue();
    }

    @Test
    @Sql(value = {"classpath:testDirector/dataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"classpath:testDirector/cleanDataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Сортировка фильмов по кол-ву лайков и id директора")
    void getFilmsDirectorBySortLikesTest() throws SQLException {
        initDirectors();
        final int idDirector = 1;
        final int index0 = 0;
        final int index1 = 1;
        final int like1 = 1;
        final int like5 = 5;
        Collection<Film> films = filmController.getFilmsDirectorBySort(idDirector, "likes");
        assertThat(films.stream().collect(Collectors.toList()).get(index0).getRate() == like5).isTrue();
        assertThat(films.stream().collect(Collectors.toList()).get(index1).getRate() == like1).isTrue();
        assertThat(films.stream().collect(Collectors.toList()).get(index0).getDirectors().contains(director1)).isTrue();
        assertThat(films.stream().collect(Collectors.toList()).get(index1).getDirectors().contains(director1)).isTrue();
    }

    @Test
    @Sql(value = {"classpath:testDirector/dataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"classpath:testDirector/cleanDataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Сортировка фильмов по годам и id директора")
    void getFilmsDirectorBySortYearTest() throws SQLException {
        initDirectors();
        final int idDirector = 1;
        final int index0 = 0;
        final int index1 = 1;
        final int year1 = 1987;
        final int year2 = 1989;
        Collection<Film> films = filmController.getFilmsDirectorBySort(idDirector, "year");
        assertThat(films.stream().collect(Collectors.toList()).get(index0).getReleaseDate().getYear() == year1).isTrue();
        assertThat(films.stream().collect(Collectors.toList()).get(index1).getReleaseDate().getYear() == year2).isTrue();
        assertThat(films.stream().collect(Collectors.toList()).get(index0).getDirectors().contains(director1)).isTrue();
        assertThat(films.stream().collect(Collectors.toList()).get(index1).getDirectors().contains(director1)).isTrue();
    }

    @Test
    @Sql(value = {"classpath:testDirector/dataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"classpath:testDirector/cleanDataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Популярные фильмы")
    void getPopularFilms() {
        initDirectors();
        initFilms();
        final int count = 1;
        final int size = 1;
        List<Film> films = filmController.getPopularFilms(count);
        assertThat(films.size() == size);
        assertThat(films.contains(film1)).isTrue();
    }
}
