package fr.frogdevelopment.ep.application.xls.clean;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
class CleanUpTeams {

    private final JdbcTemplate jdbcTemplate;

    CleanUpTeams(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    void call() {
        jdbcTemplate.update("DELETE FROM teams");
        jdbcTemplate.update("ALTER SEQUENCE teams_team_id_seq RESTART WITH 1");
    }
}
