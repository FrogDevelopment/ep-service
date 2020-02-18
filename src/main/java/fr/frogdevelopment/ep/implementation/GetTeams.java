package fr.frogdevelopment.ep.implementation;

import fr.frogdevelopment.ep.domain.Team;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GetTeams {

    private final JdbcTemplate jdbcTemplate;

    public GetTeams(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Team> call() {
        var sql = "SELECT * FROM teams";

        return jdbcTemplate.query(sql, (rs, rowNum) -> Team.builder()
                .id(rs.getInt("team_id"))
                .name(rs.getString("name"))
                .abbreviation(rs.getString("abbreviation"))
                .build());
    }
}