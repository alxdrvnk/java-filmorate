package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Value
@Builder
@EqualsAndHashCode
@Jacksonized
public class Film {

    @With
    @EqualsAndHashCode.Exclude
    Long id;

    @NotNull
    @NotBlank(message = "Поле \"Название\" должно быть заполнено")
    String title;

    @Size(max = 200, message = "Максимальное кол-во символов для описания: 200")
    String description;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate releaseDate;

    @NotNull
    @Positive(message = "Продолжительность фильма должна быть положительной")
    int duration;

    @With
    int mpa;

    @With
    Set<Genre> genres;

    @With
    @Builder.Default
    Long rate = 0L;
}