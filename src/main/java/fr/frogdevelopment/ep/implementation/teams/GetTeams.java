package fr.frogdevelopment.ep.implementation.teams;

import fr.frogdevelopment.ep.model.Team;
import fr.frogdevelopment.ep.model.Volunteer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
public class GetTeams {

    private final JdbcTemplate jdbcTemplate;

    public GetTeams(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Team> getAll() {
        var sql = "SELECT * FROM teams ORDER BY code";

        return jdbcTemplate.query(sql, (rs, rowNum) -> Team.builder()
                .id(rs.getInt("team_id"))
                .name(rs.getString("name"))
                .code(rs.getString("code"))
                .build());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Stream<Team> getAllWithMembers() {
        var sql = "SELECT t.*, v.last_name, v.first_name, v.referent"
                + " FROM teams t"
                + " INNER JOIN volunteers v ON t.code = v.team_code";

        return jdbcTemplate.query(sql, rs -> {
            var teams = new HashMap<String, Team>();

            while (rs.next()) {
                var code = rs.getString("code");
                teams.computeIfAbsent(code, key -> Team.builder()
                        .id(getInt(rs, "team_id"))
                        .name(getString(rs, "name"))
                        .code(key)
                        .build())
                        .getVolunteers().add(Volunteer.builder()
                        .lastName(rs.getString("last_name"))
                        .firstName(rs.getString("first_name"))
                        .referent(rs.getBoolean("referent"))
                        .build());
            }

            return teams.values().stream();
        });
    }

    private static int getInt(ResultSet rs, String column) {
        try {
            return rs.getInt(column);
        } catch (SQLException e) {
            log.error("", e);
            return 0;
        }
    }

    private static String getString(ResultSet rs, String column) {
        try {
            return rs.getString(column);
        } catch (SQLException e) {
            log.error("", e);
            return null;
        }
    }
}
