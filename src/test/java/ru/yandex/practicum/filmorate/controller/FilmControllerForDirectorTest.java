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

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(value = {"classpath:testDirector/dataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"classpath:testDirector/cleanDataForTestDirector.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class FilmControllerForDirectorTest {
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
                .genres(Set.of(Genre.builder().id(2L).name("Драма").build(),
                        Genre.builder().id(6L).name("Боевик").build()))
                .directors(new ArrayList<>(Arrays.asList(director1, director2)))
                .build();
    }

    @Test

    @DisplayName("Check list of directors when search by id")
    void findFilmByIdWithADirectorsTest() {
        initDirectors();
        initFilms();
        Long id = 1L;
        Film film = filmController.findFilmBy(id);
        assertThat(film).isEqualTo(film1);
        assertThat(film.getDirectors()).contains(director1);
        assertThat(film.getDirectors()).contains(director2);
    }

    @Test
    @DisplayName("Check list of directors when update film")
    void updateFilmDirectorTest() {
        initDirectors();
        initFilms();
        Long id = 1L;
        int size2 = 2;
        int size0 = 0;
        Film film = filmController.findFilmBy(id);
        assertThat(film.getDirectors()).hasSize(size2);
        assertThat(film.getDirectors()).contains(director1);
        assertThat(film.getDirectors()).contains(director2);
        filmDao.deleteDirectorForFilm(id);
        Film filmEmptyListDirectors = filmController.findFilmBy(id);
        assertThat(filmEmptyListDirectors.getDirectors()).isEmpty();
        assertThat(filmEmptyListDirectors.getDirectors()).hasSize(size0);
        filmDao.addDirectorForFilm(film1);
        Film filmUpdate = filmController.findFilmBy(id);
        assertThat(filmUpdate).isEqualTo(film1);
        assertThat(filmUpdate.getDirectors()).contains(director1);
        assertThat(filmUpdate.getDirectors()).contains(director2);
    }

    @Test
    @DisplayName("Getting list of all films")
    void findAllFilmDirectorTest() {
        initDirectors();
        int index0 = 0;
        int index1 = 1;
        int index2 = 2;
        int index3 = 3;
        int index4 = 4;
        int index5 = 5;
        int index6 = 6;
        List<Film> allFilms = filmController.findAll();
        assertThat(allFilms.get(index0).getDirectors()).contains(director1);
        assertThat(allFilms.get(index0).getDirectors()).contains(director2);
        assertThat(allFilms.get(index1).getDirectors()).contains(director2);
        assertThat(allFilms.get(index2).getDirectors()).contains(director1);
        assertThat(allFilms.get(index3).getDirectors()).isEmpty();
        assertThat(allFilms.get(index4).getDirectors()).isEmpty();
        assertThat(allFilms.get(index5).getDirectors()).isEmpty();
        assertThat(allFilms.get(index6).getDirectors()).isEmpty();
    }

    @Test
    @DisplayName("Remove directors from film")
    void deleteFilmByDirectorTest() {
        initDirectors();
        initFilms();
        Long id = 1L;
        int size2 = 2;
        int size0 = 0;
        Film film = filmController.findFilmBy(id);
        assertThat(film.getDirectors()).hasSize(size2);
        assertThat(film.getDirectors()).contains(director1);
        assertThat(film.getDirectors()).contains(director2);
        filmDao.deleteDirectorForFilm(id);
        Film filmEmptyListDirectors = filmController.findFilmBy(id);
        assertThat(filmEmptyListDirectors.getDirectors()).isEmpty();
        assertThat(filmEmptyListDirectors.getDirectors()).hasSize(size0);
    }

    @Test
    @DisplayName("Sort films by likes and directors id")
    void getFilmsDirectorBySortLikesTest() {
        initDirectors();
        int idDirector = 1;
        int index0 = 0;
        int index1 = 1;
        int like1 = 1;
        int like5 = 5;
        Collection<Film> films = filmController.getFilmsDirectorBySort(idDirector, "likes");
        assertThat(new ArrayList<>(films).get(index0).getRate()).isEqualTo(like5);
        assertThat(new ArrayList<>(films).get(index1).getRate()).isEqualTo(like1);
        assertThat(new ArrayList<>(films).get(index0).getDirectors()).contains(director1);
        assertThat(new ArrayList<>(films).get(index1).getDirectors()).contains(director1);
    }

    @Test
    @DisplayName("Sort films by years and directors id")
    void getFilmsDirectorBySortYearTest() {
        initDirectors();
        int idDirector = 1;
        int index0 = 0;
        int index1 = 1;
        int year1 = 1987;
        int year2 = 1989;
        Collection<Film> films = filmController.getFilmsDirectorBySort(idDirector, "year");
        assertThat(new ArrayList<>(films).get(index0).getReleaseDate().getYear()).isEqualTo(year1);
        assertThat(new ArrayList<>(films).get(index1).getReleaseDate().getYear()).isEqualTo(year2);
        assertThat(new ArrayList<>(films).get(index0).getDirectors()).contains(director1);
        assertThat(new ArrayList<>(films).get(index1).getDirectors()).contains(director1);
    }

    @Test
    @DisplayName("Check getting popular films")
    void getPopularFilms() {
        initDirectors();
        initFilms();
        int count = 1;
        int size = 1;
        List<Film> films = filmController.getPopularFilms(count, null, null);

        assertThat(films).hasSize(size).contains(film1);
    }
}
