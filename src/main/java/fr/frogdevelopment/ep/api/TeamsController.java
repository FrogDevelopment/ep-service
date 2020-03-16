package fr.frogdevelopment.ep.api;

import fr.frogdevelopment.ep.implementation.teams.AddTeam;
import fr.frogdevelopment.ep.implementation.teams.DeleteTeam;
import fr.frogdevelopment.ep.implementation.teams.GetTeams;
import fr.frogdevelopment.ep.implementation.teams.UpdateTeam;
import fr.frogdevelopment.ep.model.Team;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

//@RestController
//@RequestMapping(path = "teams", produces = APPLICATION_JSON_VALUE)
@Component
public class TeamsController {

    private final GetTeams getTeams;
    private final AddTeam addTeam;
    private final DeleteTeam deleteTeam;
    private final UpdateTeam updateTeam;

    public TeamsController(GetTeams getTeams, AddTeam addTeam,
                           DeleteTeam deleteTeam, UpdateTeam updateTeam) {
        this.getTeams = getTeams;
        this.addTeam = addTeam;
        this.deleteTeam = deleteTeam;
        this.updateTeam = updateTeam;
    }

    @GetMapping
    public List<Team> getAll() {
        return getTeams.getAll();
    }

    @PostMapping
    public Team add(Team team) {
        return addTeam.call(team);
    }

    @DeleteMapping
    public void delete(Team team) {
        deleteTeam.call(team);
    }

    @PutMapping
    public void update(Team team) {
        updateTeam.call(team);
    }
}
