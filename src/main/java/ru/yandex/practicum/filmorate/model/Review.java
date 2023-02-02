package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
@Builder
@EqualsAndHashCode
@Jacksonized
public class Review {
    @With
    @EqualsAndHashCode.Exclude
    Long reviewId;
    @NotNull Long userId;
    @NotNull Long filmId;
    @NotNull
    @NotBlank(message = "Поле \"Содержание\" должно быть заполнено")
    String content;
    @NotNull Boolean isPositive;
    @Builder.Default
    @With
    int useful = 0;
}
