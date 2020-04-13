package fr.frogdevelopment.ep.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import fr.frogdevelopment.ep.application.timetables.TimetablesRepository;
import fr.frogdevelopment.ep.model.Timetable;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public void setEdition(@RequestBody @Valid @NotNull @Future LocalDate localDate) {
        timetablesRepository.setEdition(localDate);
    }

    @GetMapping
    public List<Timetable> getPlanning() {
        return timetablesRepository.getPlanning();
    }
}
