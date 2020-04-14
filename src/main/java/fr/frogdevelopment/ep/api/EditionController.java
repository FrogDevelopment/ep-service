package fr.frogdevelopment.ep.api;

import fr.frogdevelopment.ep.application.timetables.TimetablesRepository;
import java.time.LocalDate;
import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "edition")
public class EditionController {

    private final TimetablesRepository timetablesRepository;

    public EditionController(TimetablesRepository timetablesRepository) {
        this.timetablesRepository = timetablesRepository;
    }

    @GetMapping
    public LocalDate getEdition() {
        return timetablesRepository.getEdition();
    }

    @PutMapping
    public void setEdition(@RequestBody @Valid @NotNull @Future LocalDate localDate) {
        timetablesRepository.setEdition(localDate);
    }
}
