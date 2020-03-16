package fr.frogdevelopment.ep.client;

import fr.frogdevelopment.ep.api.TeamsController;
import fr.frogdevelopment.ep.model.Team;
import java.util.List;
import org.springframework.stereotype.Component;

// fixme tmp component to prepare split back/front => to migrate to Feign
@Component
public class TeamsClient {

    private final TeamsController teamsController;

    public TeamsClient(TeamsController teamsController) {
        this.teamsController = teamsController;
    }

    public List<Team> getAll() {
        return teamsController.getAll();
    }

    public Team add(Team team) {
        return teamsController.add(team);
    }

    public void delete(Team team) {
        teamsController.delete(team);
    }

    public void update(Team team) {
        teamsController.update(team);
    }
}
