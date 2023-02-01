package ru.yandex.practicum.filmorate.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.dao.impl.EventsDbStorage;
@TestConfiguration
@Import(DbUnitConfig.class)
public class EventDaoTestConfig {

    @Bean
    public EventDao eventDao(JdbcTemplate jdbcTemplate) {
        return new EventsDbStorage(jdbcTemplate);
    }
}
