package fr.frogdevelopment.ep.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import fr.frogdevelopment.ep.implementation.timetables.TimetablesRepository;
import fr.frogdevelopment.ep.model.Timetable;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "timetables", produces = APPLICATION_JSON_VALUE)
public class PlanningController {

    private final TimetablesRepository timetablesRepository;

    public PlanningController(TimetablesRepository timetablesRepository) {
        this.timetablesRepository = timetablesRepository;
    }

    @GetMapping("edition")
    public LocalDate getEdition() {
        return timetablesRepository.getEdition();
    }

    @PutMapping("edition")
    public void setEdition(LocalDate localDate) {
        timetablesRepository.setEdition(localDate);
    }

    @GetMapping
    public List<Timetable> getPlanning() {
        return timetablesRepository.getPlanning();
    }
}
