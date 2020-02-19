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
        jdbcTemplate.update("ALTER SEQUENCE schedules_schedule_id_seq RESTART WITH 1");
        jdbcTemplate.update("DELETE FROM volunteers");
        jdbcTemplate.update("ALTER SEQUENCE volunteers_volunteer_id_seq RESTART WITH 1");
        jdbcTemplate.update("DELETE FROM teams");
        jdbcTemplate.update("ALTER SEQUENCE teams_team_id_seq RESTART WITH 1");
    }
}
