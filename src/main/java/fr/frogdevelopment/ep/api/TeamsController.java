package fr.frogdevelopment.ep.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import fr.frogdevelopment.ep.implementation.teams.AddTeam;
import fr.frogdevelopment.ep.implementation.teams.DeleteTeam;
import fr.frogdevelopment.ep.implementation.teams.TeamsRepository;
import fr.frogdevelopment.ep.implementation.teams.UpdateTeam;
import fr.frogdevelopment.ep.model.Team;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "teams", produces = APPLICATION_JSON_VALUE)
public class TeamsController {

    private final TeamsRepository teamsRepository;
    private final AddTeam addTeam;
    private final DeleteTeam deleteTeam;
    private final UpdateTeam updateTeam;

    public TeamsController(TeamsRepository teamsRepository, AddTeam addTeam,
                           DeleteTeam deleteTeam, UpdateTeam updateTeam) {
        this.teamsRepository = teamsRepository;
        this.addTeam = addTeam;
        this.deleteTeam = deleteTeam;
        this.updateTeam = updateTeam;
    }

    @GetMapping
    public List<Team> getAll() {
        return teamsRepository.getAll();
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
