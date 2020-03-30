package fr.frogdevelopment.ep.implementation.stats;

import fr.frogdevelopment.ep.implementation.teams.TeamsRepository;
import fr.frogdevelopment.ep.model.Team;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class GetTeamStats {

    private final TeamsRepository teamsRepository;
    private final StatsRepository statsRepository;

    public GetTeamStats(TeamsRepository teamsRepository, StatsRepository statsRepository) {
        this.teamsRepository = teamsRepository;
        this.statsRepository = statsRepository;
    }

    public List<Team> call() {
        var teamsWithSchedules = statsRepository.getTeamsWithSchedules()
                .stream()
                .collect(Collectors.toMap(TeamStats::getCode, TeamStats::getSchedules));

        return teamsRepository.getAllWithInformation(teamsWithSchedules);
    }
}
