package fr.frogdevelopment.ep.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import fr.frogdevelopment.ep.implementation.teams.GetTeams;
import fr.frogdevelopment.ep.model.Team;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "teams", produces = APPLICATION_JSON_VALUE)
public class TeamsController {

    private final GetTeams getTeams;

    public TeamsController(GetTeams getTeams) {
        this.getTeams = getTeams;
    }

    @GetMapping
    public List<Team> getAll() {
        return getTeams.getAll();
    }

    @GetMapping("/with-members")
    public Stream<Team> getAllWithMembers() {
        return getTeams.getAllWithMembers();
    }
}
