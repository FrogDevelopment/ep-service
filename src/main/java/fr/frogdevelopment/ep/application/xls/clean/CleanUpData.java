package fr.frogdevelopment.ep.application.xls.clean;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CleanUpData {

    private final CleanUpExport cleanUpExport;
    private final CleanUpTeams cleanUpTeams;
    private final CleanUpSchedules cleanUpSchedules;
    private final CleanUpVolunteers cleanUpVolunteers;
    private final CleanUpTimetables cleanUpTimetables;

    public CleanUpData(CleanUpExport cleanUpExport,
                       CleanUpTeams cleanUpTeams,
                       CleanUpSchedules cleanUpSchedules,
                       CleanUpVolunteers cleanUpVolunteers,
                       CleanUpTimetables cleanUpTimetables) {
        this.cleanUpExport = cleanUpExport;
        this.cleanUpTeams = cleanUpTeams;
        this.cleanUpSchedules = cleanUpSchedules;
        this.cleanUpVolunteers = cleanUpVolunteers;
        this.cleanUpTimetables = cleanUpTimetables;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void call() {
        cleanUpExport.call();
        cleanUpSchedules.call();
        cleanUpTimetables.call();
        cleanUpVolunteers.call();
        cleanUpTeams.call();
    }
}
