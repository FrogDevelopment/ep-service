package fr.frogdevelopment.ep.implementation.volunteers;

import fr.frogdevelopment.ep.model.Volunteer;
import java.util.List;
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
                .ref(rs.getString("volunteer_ref"))
                .lastName(rs.getString("last_name"))
                .firstName(rs.getString("first_name"))
                .email(rs.getString("email"))
                .phoneNumber(rs.getString("phone_number"))
                .teamCode(rs.getString("team_code"))
                .friendsGroup(rs.getString("friends_group"))
                .referent(rs.getBoolean("referent"))
                .build());
    }
}
