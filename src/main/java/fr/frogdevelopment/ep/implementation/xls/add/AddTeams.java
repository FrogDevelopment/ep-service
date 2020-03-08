package fr.frogdevelopment.ep.implementation.xls.add;

import fr.frogdevelopment.ep.implementation.xls.model.XlsTeam;
import java.util.Collection;
import javax.sql.DataSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class AddTeams {

    private final SimpleJdbcInsert simpleJdbcInsert;

    public AddTeams(DataSource dataSource) {
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .usingGeneratedKeyColumns("team_id")
                .withTableName("teams");
    }

    public void call(Collection<XlsTeam> teams) {
        teams.forEach(this::add);
    }

    private void add(XlsTeam team) {
        var paramSource = new MapSqlParameterSource()
                .addValue("name", team.getName())
                .addValue("code", team.getCode());

        simpleJdbcInsert.execute(paramSource);
    }

}
