package fr.frogdevelopment.ep.application.xls.clean;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
class CleanUpExport {

    private final JdbcTemplate jdbcTemplate;

    CleanUpExport(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    void call() {
        jdbcTemplate.update("DELETE FROM export_schedules");
    }
}
