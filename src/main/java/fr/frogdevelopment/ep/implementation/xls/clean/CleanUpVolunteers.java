package fr.frogdevelopment.ep.implementation.xls.clean;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
class CleanUpVolunteers {

    private final JdbcTemplate jdbcTemplate;

    CleanUpVolunteers(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    void call() {
        jdbcTemplate.update("DELETE FROM volunteers");
        jdbcTemplate.update("ALTER SEQUENCE volunteers_volunteer_id_seq RESTART WITH 1");
    }
}
