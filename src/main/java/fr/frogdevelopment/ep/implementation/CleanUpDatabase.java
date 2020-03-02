package fr.frogdevelopment.ep.implementation;

import fr.frogdevelopment.ep.implementation.schedules.CleanUpSchedules;
import fr.frogdevelopment.ep.implementation.teams.CleanUpTeams;
import fr.frogdevelopment.ep.implementation.volunteers.CleanUpVolunteers;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
class CleanUpDatabase {

    private final CleanUpSchedules cleanUpSchedules;
    private final CleanUpVolunteers cleanUpVolunteers;
    private final CleanUpTeams cleanUpTeams;

    CleanUpDatabase(CleanUpSchedules cleanUpSchedules,
                    CleanUpVolunteers cleanUpVolunteers,
                    CleanUpTeams cleanUpTeams) {
        this.cleanUpSchedules = cleanUpSchedules;
        this.cleanUpVolunteers = cleanUpVolunteers;
        this.cleanUpTeams = cleanUpTeams;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void call() {
        cleanUpSchedules.call();
        cleanUpVolunteers.call();
        cleanUpTeams.call();
    }
}
