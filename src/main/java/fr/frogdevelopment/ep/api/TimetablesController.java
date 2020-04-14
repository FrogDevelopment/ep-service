package fr.frogdevelopment.ep.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import fr.frogdevelopment.ep.application.timetables.TimetablesRepository;
import fr.frogdevelopment.ep.model.Timetable;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "timetables", produces = APPLICATION_JSON_VALUE)
public class TimetablesController {

    private final TimetablesRepository timetablesRepository;

    public TimetablesController(TimetablesRepository timetablesRepository) {
        this.timetablesRepository = timetablesRepository;
    }

    @GetMapping
    public List<Timetable> getPlanning() {
        return timetablesRepository.getPlanning();
    }
}
