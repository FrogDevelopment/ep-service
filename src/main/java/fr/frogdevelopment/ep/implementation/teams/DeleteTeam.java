package fr.frogdevelopment.ep.implementation.teams;

import fr.frogdevelopment.ep.model.Team;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class DeleteTeam {

    private final JdbcTemplate jdbcTemplate;

    public DeleteTeam(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void call(Team team) {
        var sql = "DELETE FROM teams WHERE code = :code";

        jdbcTemplate.update(sql, new MapSqlParameterSource("code", team.getCode()));
    }

}
