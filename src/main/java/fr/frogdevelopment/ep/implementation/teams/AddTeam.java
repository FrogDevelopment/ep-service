package fr.frogdevelopment.ep.implementation.teams;

import fr.frogdevelopment.ep.model.Team;
import javax.sql.DataSource;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AddTeam {

    private final SimpleJdbcInsert simpleJdbcInsert;

    public AddTeam(DataSource dataSource) {
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .usingGeneratedKeyColumns("team_id")
                .withTableName("teams");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Team call(Team team) {
        var parameterSource = new BeanPropertySqlParameterSource(team);
        var returnedKey = simpleJdbcInsert.executeAndReturnKey(parameterSource);

        team.setId(returnedKey.intValue());

        return team;
    }

}
