package fr.frogdevelopment.ep.implementation;

import fr.frogdevelopment.ep.implementation.planning.PlanningRepository;
import fr.frogdevelopment.ep.implementation.schedules.AddSchedule;
import fr.frogdevelopment.ep.implementation.teams.AddTeam;
import fr.frogdevelopment.ep.implementation.volunteers.AddVolunteer;
import fr.frogdevelopment.ep.implementation.xls.ExcelReader;
import java.io.InputStream;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UploadData {

    private final CleanUpDatabase cleanUpDatabase;
    private final PlanningRepository planningRepository;
    private final AddTeam addTeam;
    private final AddVolunteer addVolunteer;
    private final AddSchedule addSchedule;

    public UploadData(CleanUpDatabase cleanUpDatabase,
                      PlanningRepository planningRepository,
                      AddTeam addTeam,
                      AddVolunteer addVolunteer,
                      AddSchedule addSchedule) {
        this.cleanUpDatabase = cleanUpDatabase;
        this.planningRepository = planningRepository;
        this.addTeam = addTeam;
        this.addVolunteer = addVolunteer;
        this.addSchedule = addSchedule;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void call(InputStream inputStream) {
        cleanUpDatabase.call();
        var result = ExcelReader.read(inputStream);
    }
}
