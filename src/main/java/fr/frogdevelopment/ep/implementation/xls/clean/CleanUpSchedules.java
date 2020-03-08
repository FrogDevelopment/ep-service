package fr.frogdevelopment.ep.implementation.xls.clean;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
class CleanUpSchedules {

    private final JdbcTemplate jdbcTemplate;

    CleanUpSchedules(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    void call() {
        jdbcTemplate.update("DELETE FROM schedules");
        jdbcTemplate.update("ALTER SEQUENCE schedules_schedule_id_seq RESTART WITH 1");
    }
}
