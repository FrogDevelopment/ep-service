package fr.frogdevelopment.ep.implementation.volunteers;

import fr.frogdevelopment.ep.model.Volunteer;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class GetVolunteers {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public GetVolunteers(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Volunteer> getAll() {
        var sql = "SELECT *"
                + " FROM volunteers"
                + " ORDER BY last_name, first_name";

        return jdbcTemplate.query(sql, (rs, rowNum) -> Volunteer.builder()
                .id(rs.getInt("volunteer_id"))
                .lastName(rs.getString("last_name"))
                .firstName(rs.getString("first_name"))
                .email(rs.getString("email"))
                .phoneNumber(rs.getString("phone_number"))
                .teamCode(rs.getString("team_code"))
                .referent(rs.getBoolean("referent"))
                .build());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Volunteer> getAllByTeam(String teamCode) {
        var sql = "SELECT last_name, first_name, referent"
                + " FROM volunteers"
                + " WHERE team_code = :teamCode"
                + " ORDER BY last_name, first_name";

        var paramSources = new MapSqlParameterSource("teamCode", teamCode);

        return jdbcTemplate.query(sql, paramSources, (rs, rowNum) -> Volunteer.builder()
                .lastName(rs.getString("last_name"))
                .firstName(rs.getString("first_name"))
                .referent(rs.getBoolean("referent"))
                .build());
    }
}
