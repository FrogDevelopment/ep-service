package fr.frogdevelopment.ep.implementation.volunteers;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CleanUpVolunteers {

    private final JdbcTemplate jdbcTemplate;

    public CleanUpVolunteers(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void call() {
        jdbcTemplate.update("DELETE FROM volunteers");
        jdbcTemplate.update("ALTER SEQUENCE volunteers_volunteer_id_seq RESTART WITH 1");
    }
}
