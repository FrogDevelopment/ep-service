package fr.frogdevelopment.ep.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import fr.frogdevelopment.ep.implementation.stats.StatsRepository;
import fr.frogdevelopment.ep.implementation.stats.StatsRepository.TimeSlot;
import fr.frogdevelopment.ep.model.Volunteer;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "stats", produces = APPLICATION_JSON_VALUE)
public class StatsController {

    private final StatsRepository statsRepository;

    public StatsController(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @GetMapping("/time-slots")
    public List<TimeSlot> getTimeSlots() {
        return statsRepository.getTimeSlots();
    }

    @GetMapping("/with-all-schedules")
    public List<Volunteer> getAllWithSchedules() {
        return statsRepository.getAllWithSchedules();
    }
}
