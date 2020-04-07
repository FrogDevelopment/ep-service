package fr.frogdevelopment.ep.application.teams;

import static org.assertj.core.api.Assertions.assertThat;

import fr.frogdevelopment.ep.utils.IntegrationTest;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

class TeamsRepositoryTest extends IntegrationTest {

    @Value("classpath:sql/teams.sql")
    private Resource resource;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TeamsRepository teamsRepository;

    @Test
    void shouldFetchTeams() {
        // given
        DatabasePopulatorUtils.execute(new ResourceDatabasePopulator(resource), dataSource);

        // when
        var teams = teamsRepository.getAll();

        // then
        assertThat(teams).hasSize(2);
        assertThat(teams.get(0).getCode()).isEqualTo("T1");
        assertThat(teams.get(0).getName()).isEqualTo("Team 1");
        assertThat(teams.get(1).getCode()).isEqualTo("T2");
        assertThat(teams.get(1).getName()).isEqualTo("Team 2");
    }

}
