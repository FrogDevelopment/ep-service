package fr.frogdevelopment.ep.implementation.teams;

import static org.assertj.core.api.Assertions.assertThat;

import fr.frogdevelopment.ep.model.Team;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@Tag("integrationTest")
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class AddTeamsTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AddTeam addTeam;

    @Test
    void shouldInsertNewTeamToTheTable() {
        // given
        var countRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate, "teams");
        assertThat(countRowsInTable).isEqualTo(0);

        var team = Team.builder()
                .name("Ma Super Team")
                .code("MST")
                .build();

        // when
        addTeam.call(team);

        // then
        assertThat(team.getId()).isNotNull();
        countRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate, "teams");
        assertThat(countRowsInTable).isEqualTo(1);
    }
}
