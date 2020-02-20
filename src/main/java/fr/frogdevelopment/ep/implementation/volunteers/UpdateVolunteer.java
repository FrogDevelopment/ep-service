package fr.frogdevelopment.ep.implementation.volunteers;

import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.upperCase;

import fr.frogdevelopment.ep.model.Volunteer;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UpdateVolunteer {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public UpdateVolunteer(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void call(Volunteer volunteer) {
        var sql = "UPDATE volunteerrs SET"
                + " first_name = :firstName,"
                + " last_name = :lastName,"
                + " phone_number = :phoneNumber,"
                + " email = :email,"
                + " team_code = :teamCode,"
                + " referent = :referent"
                + " WHERE volunteer_id = :id";
        var paramSource = new MapSqlParameterSource()
                .addValue("id", volunteer.getId())
                .addValue("firstName", capitalize(lowerCase(volunteer.getFirstName())))
                .addValue("lastName", upperCase(volunteer.getLastName()))
                .addValue("phoneNumber", volunteer.getPhoneNumber())
                .addValue("email", lowerCase(volunteer.getEmail()))
                .addValue("team_code", volunteer.getTeamCode())
                .addValue("referent", volunteer.isReferent());

        namedParameterJdbcTemplate.update(sql, paramSource);
    }

}
