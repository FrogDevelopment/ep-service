package fr.frogdevelopment.ep.application.teams;

import static org.assertj.core.api.Assertions.assertThat;

import fr.frogdevelopment.ep.model.Team;
import fr.frogdevelopment.ep.utils.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

class AddTeamsTest extends IntegrationTest {

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
