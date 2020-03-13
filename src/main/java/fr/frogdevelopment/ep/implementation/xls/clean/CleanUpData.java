package fr.frogdevelopment.ep.implementation.xls.clean;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CleanUpData {

    private final CleanUpTeams cleanUpTeams;
    private final CleanUpSchedules cleanUpSchedules;
    private final CleanUpVolunteers cleanUpVolunteers;
    private final CleanUpTimetables cleanUpTimetables;

    public CleanUpData(CleanUpTeams cleanUpTeams,
                       CleanUpSchedules cleanUpSchedules,
                       CleanUpVolunteers cleanUpVolunteers,
                       CleanUpTimetables cleanUpTimetables) {
        this.cleanUpTeams = cleanUpTeams;
        this.cleanUpSchedules = cleanUpSchedules;
        this.cleanUpVolunteers = cleanUpVolunteers;
        this.cleanUpTimetables = cleanUpTimetables;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void call() {
        cleanUpSchedules.call();
        cleanUpTimetables.call();
        cleanUpVolunteers.call();
        cleanUpTeams.call();
    }
}
