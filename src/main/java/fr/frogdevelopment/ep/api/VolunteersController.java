package fr.frogdevelopment.ep.api;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import fr.frogdevelopment.ep.implementation.volunteers.AddVolunteer;
import fr.frogdevelopment.ep.implementation.volunteers.DeleteVolunteer;
import fr.frogdevelopment.ep.implementation.volunteers.GetVolunteers;
import fr.frogdevelopment.ep.implementation.volunteers.UpdateVolunteer;
import fr.frogdevelopment.ep.model.Volunteer;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

//@RestController
//@RequestMapping(path = "volunteers", produces = APPLICATION_JSON_VALUE)
@Component
public class VolunteersController {

    private final GetVolunteers getVolunteers;
    private final AddVolunteer addVolunteer;
    private final UpdateVolunteer updateVolunteer;
    private final DeleteVolunteer deleteVolunteer;

    public VolunteersController(GetVolunteers getVolunteers,
                                AddVolunteer addVolunteer,
                                UpdateVolunteer updateVolunteer,
                                DeleteVolunteer deleteVolunteer) {
        this.getVolunteers = getVolunteers;
        this.addVolunteer = addVolunteer;
        this.updateVolunteer = updateVolunteer;
        this.deleteVolunteer = deleteVolunteer;
    }

    @GetMapping
    public List<Volunteer> getAll() {
        return getVolunteers.getAll();
    }

    @GetMapping("/{teamCode}")
    public List<Volunteer> getAll(@PathVariable String teamCode) {
        return getVolunteers.getAll(teamCode);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Volunteer create(@RequestBody Volunteer volunteer) {
        return addVolunteer.call(volunteer);
    }

    @PutMapping
    @ResponseStatus(NO_CONTENT)
    public void update(@RequestBody Volunteer volunteer) {
        updateVolunteer.call(volunteer);
    }

    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    public void delete(@RequestBody Volunteer volunteer) {
        deleteVolunteer.call(volunteer);
    }

}
