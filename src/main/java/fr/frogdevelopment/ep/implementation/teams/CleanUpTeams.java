package fr.frogdevelopment.ep.implementation.teams;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CleanUpTeams {

    private final JdbcTemplate jdbcTemplate;

    public CleanUpTeams(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void call() {
        jdbcTemplate.update("DELETE FROM teams");
        jdbcTemplate.update("ALTER SEQUENCE teams_team_id_seq RESTART WITH 1");
    }
}
