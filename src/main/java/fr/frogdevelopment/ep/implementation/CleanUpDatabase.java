package fr.frogdevelopment.ep.implementation;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
class CleanUpDatabase {

    private final JdbcTemplate jdbcTemplate;

    CleanUpDatabase(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    void call() {
        jdbcTemplate.update("DELETE FROM schedules");
        jdbcTemplate.update("DELETE FROM teams");
        jdbcTemplate.update("DELETE FROM members");
    }
}
