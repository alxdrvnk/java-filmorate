package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Value
@Builder
@EqualsAndHashCode
public class Film {

    @With
    @EqualsAndHashCode.Exclude
    Long id;
    @NotNull
    @NotBlank(message = "Поле \"Имя\" должно быть заполнено")
    String name;

    @Size(max = 200, message = "Максимальное кол-во символов для описания: 200")
    String description;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate releaseDate;

    @NotNull
    @Positive(message = "Продолжительность фильма должна быть положительной")
    int duration;

    @Builder.Default
    Set<Long> likes = new HashSet<>();

}