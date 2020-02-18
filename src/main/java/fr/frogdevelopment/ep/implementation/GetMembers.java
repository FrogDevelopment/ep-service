package fr.frogdevelopment.ep.implementation;

import fr.frogdevelopment.ep.domain.Member;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GetMembers {

    private final JdbcTemplate jdbcTemplate;

    public GetMembers(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Member> call() {
        var sql = "SELECT m.member_id,"
                + " m.last_name,"
                + " m.first_name,"
                + " m.email,"
                + " m.phone_number,"
                + " m.referent,"
                + " t.abbreviation AS team"
                + " FROM members m"
                + " INNER JOIN teams t ON m.team_id = t.team_id"
                + " ORDER BY last_name, first_name";

        return jdbcTemplate.query(sql, (rs, rowNum) -> Member.builder()
                .id(rs.getInt("member_id"))
                .lastName(rs.getString("last_name"))
                .firstName(rs.getString("first_name"))
                .email(rs.getString("email"))
                .phoneNumber(rs.getString("phone_number"))
                .team(rs.getString("team"))
                .referent(rs.getBoolean("referent"))
                .build());
    }
}
