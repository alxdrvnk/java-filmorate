package ru.yandex.practicum.filmorate.aspects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.impl.EventsDbStorage;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class EventHandlerAspect {
    private final EventsDbStorage eventsDbStorage;
    private final ReviewService reviewService;

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

        addEvent(userId,
                entityId,
                String.valueOf(annotation.eventType()),
                String.valueOf(annotation.eventOperation()));

    }

    @Around("@annotation(HandleFilmorateCreateEvent)")
    public Object addFilmorateEvent(ProceedingJoinPoint call) throws Throwable {
        MethodSignature signature = (MethodSignature) call.getSignature();
        HandleFilmorateCreateEvent annotation =
                Arrays.stream(signature.getMethod().getAnnotationsByType(HandleFilmorateCreateEvent.class)).findAny().get();

        Object ret = call.proceed(call.getArgs());
        Long userId;
        Long entityId;

        if (ret.getClass() == Review.class) {
            userId = ((Review) ret).getUserId();
            entityId = ((Review) ret).getReviewId();
        } else {
            throw new RuntimeException(
                    String.format("Метод %s не может быть аннотирован HandleFilmorateCreateEvent",
                            call.getSignature().getName()));
        }

        addEvent(userId,
                entityId,
                String.valueOf(annotation.eventType()),
                String.valueOf(annotation.eventOperation()));

        return ret;
    }

    @Before("@annotation(HandleFilmorateDeleteEvent)")
    public void addDeleteEvent(JoinPoint call) {
        var t = (Long) call.getArgs()[0];
        Review review = reviewService.get(t);

        MethodSignature signature = (MethodSignature) call.getSignature();
        HandleFilmorateDeleteEvent annotation =
                Arrays.stream(signature.getMethod().getAnnotationsByType(HandleFilmorateDeleteEvent.class)).findAny().get();


        addEvent(review.getUserId(),
                review.getReviewId(),
                String.valueOf(annotation.eventType()),
                String.valueOf(annotation.eventOperation()));
    }

    private void addEvent(Long userId, Long entityId, String type, String operation) {
        Event event = Event.builder()
                .userId(userId)
                .entityId(entityId)
                .eventType(type)
                .operation(operation)
                .timestamp(Timestamp.valueOf(LocalDateTime.now()).getTime())
                .build();
        event = eventsDbStorage.create(event);
        log.info(String.format("EventHandler: Add Event. Data: %s", event));
    }
}
