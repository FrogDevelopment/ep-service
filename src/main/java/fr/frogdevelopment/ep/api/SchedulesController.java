package fr.frogdevelopment.ep.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import fr.frogdevelopment.ep.application.schedules.SchedulesRepository;
import fr.frogdevelopment.ep.model.Schedule;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "schedules", produces = APPLICATION_JSON_VALUE)
public class SchedulesController {

    private final SchedulesRepository schedulesRepository;

    public SchedulesController(SchedulesRepository schedulesRepository) {
        this.schedulesRepository = schedulesRepository;
    }

    @GetMapping
    public List<Schedule> getGroupedSchedulesByTeam() {
        return schedulesRepository.getGroupedSchedulesByTeam();
    }

    @GetMapping("/{teamCode}")
    public List<Schedule> getGroupedSchedulesByTeam(@PathVariable String teamCode) {
        return schedulesRepository.getGroupedSchedulesByTeam(teamCode);
    }
}
