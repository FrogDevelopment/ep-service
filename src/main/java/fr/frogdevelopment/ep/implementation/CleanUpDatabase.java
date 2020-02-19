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
    private final CleanUpTeams cleanUpTeams;
    private final CleanUpVolunteers cleanUpVolunteers;

    CleanUpDatabase(CleanUpSchedules cleanUpSchedules,
                    CleanUpTeams cleanUpTeams,
                    CleanUpVolunteers cleanUpVolunteers) {
        this.cleanUpSchedules = cleanUpSchedules;
        this.cleanUpTeams = cleanUpTeams;
        this.cleanUpVolunteers = cleanUpVolunteers;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void call() {
        cleanUpSchedules.call();
        cleanUpTeams.call();
        cleanUpVolunteers.call();
    }
}
