package fr.frogdevelopment.ep.implementation.teams;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@Tag("integrationTest")
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class TeamsRepositoryTest {

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
        var teams = teamsRepository.getAllWithInformation(Collections.emptyMap());

        // then
        assertThat(teams).hasSize(2);
        assertThat(teams.get(0).getCode()).isEqualTo("T1");
        assertThat(teams.get(0).getName()).isEqualTo("Team 1");
        assertThat(teams.get(1).getCode()).isEqualTo("T2");
        assertThat(teams.get(1).getName()).isEqualTo("Team 2");
    }

}
