package fr.frogdevelopment.ep.implementation.teams;

import static java.util.Collections.emptySet;

import fr.frogdevelopment.ep.model.Schedule;
import fr.frogdevelopment.ep.model.Team;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class TeamsRepository {

    private final JdbcTemplate jdbcTemplate;

    public TeamsRepository(JdbcTemplate jdbcTemplate) {
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
    public List<Team> getAllWithInformation(
            Map<String, Set<Schedule>> teamsWithSchedules) {
        var sql = "WITH referents AS (SELECT concat_ws(' ', v.last_name, v.first_name) AS names,\n"
                + "                          v.team_code\n"
                + "                   FROM volunteers v\n"
                + "                   WHERE v.referent IS TRUE),\n"
                + "\n"
                + "     teams_data AS (SELECT t.team_id,\n"
                + "                           t.name,\n"
                + "                           t.code,\n"
                + "                           count(v.volunteer_ref) AS countmembers\n"
                + "                    FROM teams t\n"
                + "                             INNER JOIN volunteers v ON t.code = v.team_code\n"
                + "                    GROUP BY t.team_id, t.name, t.code\n"
                + "                    ORDER BY code)\n"
                + "\n"
                + "SELECT td.team_id, td.name, td.code, td.countmembers, string_agg(r.names, ', ') AS referents\n"
                + "FROM teams_data td\n"
                + "LEFT OUTER JOIN referents r ON r.team_code = td.code\n"
                + "GROUP BY td.team_id, td.name, td.code, td.countmembers;";

        return jdbcTemplate.query(sql, (rs, rowNum) -> Team.builder()
                .id(rs.getInt("team_id"))
                .name(rs.getString("name"))
                .code(rs.getString("code"))
                .referents(rs.getString("referents"))
                .countMembers(rs.getInt("countMembers"))
                .schedules(teamsWithSchedules.getOrDefault(rs.getString("code"), emptySet()))
                .build());
    }

}
