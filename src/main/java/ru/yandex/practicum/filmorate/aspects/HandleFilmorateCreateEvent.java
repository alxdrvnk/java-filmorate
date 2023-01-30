package ru.yandex.practicum.filmorate.aspects;

import ru.yandex.practicum.filmorate.utils.FilmorateEventOperation;
import ru.yandex.practicum.filmorate.utils.FilmorateEventType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HandleFilmorateCreateEvent {
    FilmorateEventType eventType();
    FilmorateEventOperation eventOperation();
}
