package fr.frogdevelopment.ep.implementation.xls.add;

import fr.frogdevelopment.ep.implementation.xls.Result;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AddData {

    private final AddTimetables addTimetables;
    private final AddTeams addTeams;
    private final AddVolunteers addVolunteers;
    private final AddSchedules addSchedules;

    public AddData(AddTimetables addTimetables,
                   AddTeams addTeams,
                   AddVolunteers addVolunteers,
                   AddSchedules addSchedules) {
        this.addTimetables = addTimetables;
        this.addTeams = addTeams;
        this.addVolunteers = addVolunteers;
        this.addSchedules = addSchedules;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void call(Result result) {
        addTeams.call(result.getTeams());
        addVolunteers.call(result.getVolunteers());
        addTimetables.call(result.getTimetables());
        addSchedules.call(result.getSchedules());
    }
}
