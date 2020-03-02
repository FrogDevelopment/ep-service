package fr.frogdevelopment.ep.client;

import fr.frogdevelopment.ep.api.VolunteersController;
import fr.frogdevelopment.ep.model.Volunteer;
import java.util.List;
import org.springframework.stereotype.Component;

// fixme tmp component to prepare split back/front => to migrate to Feign
@Component
public class VolunteersClient {

    private final VolunteersController controller;

    public VolunteersClient(VolunteersController controller) {
        this.controller = controller;
    }

    public List<Volunteer> getAll() {
        return controller.getAll();
    }

    public List<Volunteer> getAll(String teamCode) {
        return controller.getAll(teamCode);
    }

    public void create(Volunteer volunteer) {
        controller.create(volunteer);
    }

    public void update(Volunteer volunteer) {
        controller.update(volunteer);
    }

    public void delete(Volunteer volunteer) {
        controller.delete(volunteer);
    }

}
