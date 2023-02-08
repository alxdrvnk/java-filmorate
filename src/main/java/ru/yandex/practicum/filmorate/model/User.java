package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.springframework.boot.context.properties.bind.DefaultValue;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Value
@Builder
public class User {
    @With
    Long id;

    @Email(regexp = "^[a-zA-Z0-9.]+[^._]@[^.\\-_]+[a-zA-Z0-9.]+[a-zA-Z0-9]$", message = "Email введен некорректно")
    @NotNull
    String email;

    @NotNull
    @NotBlank(message = "Поле Login не должно быть пустым")
    @Pattern(regexp = "\\S+")
    String login;

    @With
    String name;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate birthday;

    @With
    @Builder.Default
    Boolean deleted = false;

}

