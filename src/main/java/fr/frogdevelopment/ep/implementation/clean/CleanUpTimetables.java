package fr.frogdevelopment.ep.implementation.clean;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
class CleanUpTimetables {

    private final JdbcTemplate jdbcTemplate;

    CleanUpTimetables(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    void call() {
        jdbcTemplate.update("DELETE FROM timetables");
        jdbcTemplate.update("ALTER SEQUENCE timetables_timetable_id_seq RESTART WITH 1");
    }
}
