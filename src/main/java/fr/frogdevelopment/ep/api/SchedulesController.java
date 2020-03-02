package fr.frogdevelopment.ep.api;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import fr.frogdevelopment.ep.implementation.schedules.SchedulesRepository;
import fr.frogdevelopment.ep.model.Schedule;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

//@RestController
//@RequestMapping(path = "schedules", produces = APPLICATION_JSON_VALUE)
@Component
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

    @PutMapping("/location")
    @ResponseStatus(NO_CONTENT)
    public void changeLocation(@RequestBody Schedule schedule) {
        schedulesRepository.changeLocation(schedule);
    }
}
