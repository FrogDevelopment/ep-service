package fr.frogdevelopment.ep.implementation;

import fr.frogdevelopment.ep.model.Volunteer;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GetVolunteers {

    private final JdbcTemplate jdbcTemplate;

    public GetVolunteers(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Volunteer> call() {
        var sql = "SELECT * FROM volunteers ORDER BY last_name, first_name";

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
}
