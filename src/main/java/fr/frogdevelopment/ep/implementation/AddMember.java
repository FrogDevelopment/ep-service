package fr.frogdevelopment.ep.implementation;

import fr.frogdevelopment.ep.domain.Member;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

@Component
public class AddMember {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public AddMember(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public void call(Member member) {
        var sql = "INSERT INTO members(first_name, last_name, phone_number, email, team_id) VALUES (:firstName, :lastName, :phoneNumber, :email, :teamId)";

        var paramSource = new MapSqlParameterSource()
                .addValue("firstName", member.getFirstName())
                .addValue("lastName", member.getLastName())
                .addValue("phoneNumber", member.getPhoneNumber())
                .addValue("email", member.getEmail())
                .addValue("teamId", member.getTeamId());

        var generatedKeyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, paramSource, generatedKeyHolder);

//        member.setId(generatedKeyHolder.getKey().intValue());
    }

}
