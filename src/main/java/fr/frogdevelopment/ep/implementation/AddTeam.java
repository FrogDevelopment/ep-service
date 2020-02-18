package fr.frogdevelopment.ep.implementation;

import fr.frogdevelopment.ep.model.Team;
import javax.sql.DataSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

@Component
public class AddTeam {

    private final SimpleJdbcInsert simpleJdbcInsert;

    public AddTeam(DataSource dataSource) {
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .usingGeneratedKeyColumns("team_id")
                .withTableName("teams");
    }

    public void call(Team team) {
        var paramSource = new MapSqlParameterSource()
                .addValue("name", team.getName())
                .addValue("code", team.getCode());

        var returnedKey = simpleJdbcInsert.executeAndReturnKey(paramSource);

        team.setId(returnedKey.intValue());
    }

}
