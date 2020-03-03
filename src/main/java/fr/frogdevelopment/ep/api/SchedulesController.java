package fr.frogdevelopment.ep.api;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import fr.frogdevelopment.ep.implementation.timetables.TimetablesRepository;
import fr.frogdevelopment.ep.model.Timetable;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

//@RestController
//@RequestMapping(path = "timetables", produces = APPLICATION_JSON_VALUE)
@Component
public class SchedulesController {

    private final TimetablesRepository timetablesRepository;

    public SchedulesController(TimetablesRepository timetablesRepository) {
        this.timetablesRepository = timetablesRepository;
    }

    @GetMapping
    public List<Timetable> getGroupedSchedulesByTeam() {
        return timetablesRepository.getGroupedSchedulesByTeam();
    }

    @GetMapping("/{teamCode}")
    public List<Timetable> getGroupedSchedulesByTeam(@PathVariable String teamCode) {
        return timetablesRepository.getGroupedSchedulesByTeam(teamCode);
    }

    @PutMapping("/location")
    @ResponseStatus(NO_CONTENT)
    public void changeLocation(@RequestBody Timetable timetable) {
        timetablesRepository.changeLocation(timetable);
    }
}
