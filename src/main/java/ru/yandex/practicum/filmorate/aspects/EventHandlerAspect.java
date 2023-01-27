package ru.yandex.practicum.filmorate.aspects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.impl.EventsDbStorage;
import ru.yandex.practicum.filmorate.model.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class EventHandlerAspect {
    private final EventsDbStorage eventsDbStorage;

    @Pointcut("@annotation(HandleFilmorateEvent)")
    public void eventPointcut() {
    }

    @AfterReturning("eventPointcut()")
    public void addFilmorateEvent(JoinPoint joinPoint) {
        Long userId = (Long) joinPoint.getArgs()[1];
        Long entityId = (Long) joinPoint.getArgs()[0];

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        HandleFilmorateEvent annotation =
                Arrays.stream(signature.getMethod().getAnnotationsByType(HandleFilmorateEvent.class)).findAny().get();
        Event event = Event.builder()
                .userId(userId)
                .entityId(entityId)
                .type(String.valueOf(annotation.eventType()))
                .operation(String.valueOf(annotation.eventOperation()))
                .timestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
        event = eventsDbStorage.create(event);
        log.info(String.format("EventHandler: Add Event. Data: %s", event));
    }
}
