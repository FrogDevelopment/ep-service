package fr.frogdevelopment.ep.api;

import fr.frogdevelopment.ep.implementation.stats.StatsRepository;
import fr.frogdevelopment.ep.implementation.stats.TimeSlot;
import fr.frogdevelopment.ep.model.Volunteer;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

//@RestController
//@RequestMapping(path = "stats", produces = APPLICATION_JSON_VALUE)
@Component
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
        return statsRepository.getVolunteersWithSchedules();
    }
}
