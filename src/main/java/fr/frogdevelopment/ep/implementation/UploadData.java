package fr.frogdevelopment.ep.implementation;

import static org.springframework.beans.BeanUtils.copyProperties;

import fr.frogdevelopment.ep.implementation.clean.CleanUpData;
import fr.frogdevelopment.ep.implementation.schedules.AddSchedule;
import fr.frogdevelopment.ep.implementation.teams.AddTeam;
import fr.frogdevelopment.ep.implementation.timetables.AddTimetable;
import fr.frogdevelopment.ep.implementation.volunteers.AddVolunteer;
import fr.frogdevelopment.ep.implementation.xls.model.XlsSchedule;
import fr.frogdevelopment.ep.implementation.xls.model.XlsTeam;
import fr.frogdevelopment.ep.implementation.xls.model.XlsTimetable;
import fr.frogdevelopment.ep.implementation.xls.model.XlsVolunteer;
import fr.frogdevelopment.ep.implementation.xls.parser.ExcelParser;
import fr.frogdevelopment.ep.model.Schedule;
import fr.frogdevelopment.ep.model.Team;
import fr.frogdevelopment.ep.model.Timetable;
import fr.frogdevelopment.ep.model.Volunteer;
import java.io.InputStream;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UploadData {

    private final CleanUpData cleanUpData;
    private final AddTimetable addTimetable;
    private final AddTeam addTeam;
    private final AddVolunteer addVolunteer;
    private final AddSchedule addSchedule;

    public UploadData(CleanUpData cleanUpData,
                      AddTimetable addSchedules,
                      AddTeam addTeam,
                      AddVolunteer addVolunteer,
                      AddSchedule addSchedule) {
        this.cleanUpData = cleanUpData;
        this.addTimetable = addSchedules;
        this.addTeam = addTeam;
        this.addVolunteer = addVolunteer;
        this.addSchedule = addSchedule;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void call(InputStream inputStream) {
        var result = ExcelParser.read(inputStream);

        cleanUpData.call();

        result.getTeams()
                .stream()
                .map(this::toTeam)
                .forEach(addTeam::call);

        result.getVolunteers()
                .stream()
                .map(this::toVolunteer)
                .forEach(addVolunteer::call);

        result.getTimetables()
                .stream()
                .map(this::toTimetable)
                .forEach(addTimetable::call);

        result.getSchedules()
                .stream()
                .map(this::toSchedule)
                .forEach(addSchedule::call);
    }

    private Team toTeam(XlsTeam source) {
        var target = Team.builder().build();
        copyProperties(source, target);
        return target;
    }

    private Volunteer toVolunteer(XlsVolunteer source) {
        var target = Volunteer.builder().build();
        copyProperties(source, target);
        return target;
    }

    private Timetable toTimetable(XlsTimetable source) {
        var target = Timetable.builder().build();
        copyProperties(source, target);
        return target;
    }

    private Schedule toSchedule(XlsSchedule source) {
        var target = Schedule.builder().build();
        copyProperties(source, target);
        return target;
    }
}
