package fr.frogdevelopment.ep.client;

import fr.frogdevelopment.ep.api.SchedulesController;
import fr.frogdevelopment.ep.model.Schedule;
import java.util.List;
import org.springframework.stereotype.Component;

// fixme tmp component to prepare split back/front => to migrate to Feign
@Component
public class SchedulesClient {

    private final SchedulesController schedulesController;

    public SchedulesClient(SchedulesController schedulesController) {
        this.schedulesController = schedulesController;
    }

    public List<Schedule> getGroupedSchedulesByTeam() {
        return schedulesController.getGroupedSchedulesByTeam();
    }

    public List<Schedule> getGroupedSchedulesByTeam(String teamCode) {
        return schedulesController.getGroupedSchedulesByTeam(teamCode);
    }
}
