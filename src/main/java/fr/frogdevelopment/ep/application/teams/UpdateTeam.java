package fr.frogdevelopment.ep.application.teams;

import fr.frogdevelopment.ep.model.Team;
import java.sql.Types;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UpdateTeam {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public UpdateTeam(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void call(Team team) {
        var sql = "UPDATE teams SET name = :name, code = :code WHERE team_id = :id";

        var beanPropertySqlParameterSource = new BeanPropertySqlParameterSource(team);
        beanPropertySqlParameterSource.registerSqlType("id", Types.INTEGER);

        jdbcTemplate.update(sql, beanPropertySqlParameterSource);
    }

}
