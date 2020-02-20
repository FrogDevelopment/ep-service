package fr.frogdevelopment.ep.implementation.teams;

import fr.frogdevelopment.ep.model.Team;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class GetTeams {

    private final JdbcTemplate jdbcTemplate;

    public GetTeams(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Team> call() {
        var sql = "SELECT * FROM teams ORDER BY code";

        return jdbcTemplate.query(sql, (rs, rowNum) -> Team.builder()
                .id(rs.getInt("team_id"))
                .name(rs.getString("name"))
                .code(rs.getString("code"))
                .build());
    }
}
