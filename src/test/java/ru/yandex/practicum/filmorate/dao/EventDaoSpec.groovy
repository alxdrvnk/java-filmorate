package ru.yandex.practicum.filmorate.dao

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import groovy.sql.Sql
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener

import ru.yandex.practicum.filmorate.configuration.EventDaoTestConfig
import ru.yandex.practicum.filmorate.dao.impl.EventsDbStorage
import ru.yandex.practicum.filmorate.model.Event
import spock.lang.Specification
import spock.lang.Subject

import javax.sql.DataSource
import java.sql.Timestamp
import java.time.LocalDateTime

@Subject(EventsDbStorage)
@ContextConfiguration(classes = [EventDaoTestConfig])
@TestExecutionListeners([TransactionDbUnitTestExecutionListener, DependencyInjectionTestExecutionListener])
class EventDaoSpec extends Specification {

    @Autowired
    private EventDao eventDao

    @Autowired
    private DataSource dataSource

    private Sql db

    def setup() {
        db = Sql.newInstance(dataSource)
    }

    @DatabaseSetup(value = "classpath:database/set_user.xml", connection = "dbUnitDatabaseConnection")
    def "Should create new event with generated id"() {
        def event = Event.builder()
                .userId(1)
                .entityId(1)
                .eventType("FRIEND")
                .operation("ADD")
                .timestamp(
                        Timestamp.valueOf(
                                LocalDateTime.of(2000, 1, 1, 1, 1, 1)).getTime())
                .build()

        when:
        def id = eventDao.create(event).getEventId()

        then:
        id > 0
        def res = db.rows("select * from events where id = ${id}")
        res[0]["ID"] == id
        res[0]["USER_ID"] == event.getUserId()
        res[0]["TYPE"] == event.getEventType()
        res[0]["OPERATION"] == event.getOperation()
    }
}