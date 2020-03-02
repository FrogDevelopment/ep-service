package fr.frogdevelopment.ep.implementation.schedules;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CleanUpSchedules {

    private final JdbcTemplate jdbcTemplate;

    public CleanUpSchedules(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void call() {
        jdbcTemplate.update("DELETE FROM schedules");
        jdbcTemplate.update("ALTER SEQUENCE schedules_schedule_id_seq RESTART WITH 1");
    }
}
