package fr.frogdevelopment.ep.client;

import fr.frogdevelopment.ep.api.StatsController;
import fr.frogdevelopment.ep.implementation.stats.StatsRepository.TimeSlot;
import fr.frogdevelopment.ep.model.Volunteer;
import java.util.List;
import org.springframework.stereotype.Component;

// fixme tmp component to prepare split back/front => to migrate to Feign
@Component
public class StatsClient {

    private final StatsController statsController;

    public StatsClient(StatsController statsController) {
        this.statsController = statsController;
    }

    public List<TimeSlot> getTimeSlots() {
        return statsController.getTimeSlots();
    }

    public List<Volunteer> getAllWithSchedules() {
        return statsController.getAllWithSchedules();
    }
}
