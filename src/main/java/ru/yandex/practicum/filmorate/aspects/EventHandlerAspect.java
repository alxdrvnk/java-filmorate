package ru.yandex.practicum.filmorate.aspects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.impl.EventsDbStorage;

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
        log.info(Arrays.toString(joinPoint.getArgs()));
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        HandleFilmorateEvent annotation = Arrays.stream(signature.getMethod().getAnnotationsByType(HandleFilmorateEvent.class)).findAny().get();
        log.info(annotation.eventOperation());
        log.info(annotation.eventType());
    }

}
