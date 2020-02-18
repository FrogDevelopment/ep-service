package fr.frogdevelopment.ep.implementation;

import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.upperCase;

import fr.frogdevelopment.ep.model.Member;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateMember {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public UpdateMember(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void call(Member member) {
        var sql = "UPDATE members SET"
                + " first_name = :firstName,"
                + " last_name = :lastName,"
                + " phone_number = :phoneNumber,"
                + " email = :email,"
                + " team_code = :teamCode,"
                + " referent = :referent"
                + " WHERE member_id = :id";
        var paramSource = new MapSqlParameterSource()
                .addValue("id", member.getId())
                .addValue("firstName", capitalize(lowerCase(member.getFirstName())))
                .addValue("lastName", upperCase(member.getLastName()))
                .addValue("phoneNumber", member.getPhoneNumber())
                .addValue("email", lowerCase(member.getEmail()))
                .addValue("team_code", member.getTeamCode())
                .addValue("referent", member.isReferent());

        namedParameterJdbcTemplate.update(sql, paramSource);
    }

}
