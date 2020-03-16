package fr.frogdevelopment.ep.implementation.teams;

import fr.frogdevelopment.ep.model.Team;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UpdateTeam {

    private final JdbcTemplate jdbcTemplate;

    public UpdateTeam(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void call(Team team) {
        var sql = "UPDATE teams SET name = :name, code = :code WHERE team_id = :id";

        jdbcTemplate.update(sql, new BeanPropertySqlParameterSource(team));
    }

}
