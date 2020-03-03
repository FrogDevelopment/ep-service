package fr.frogdevelopment.ep.implementation;

import static org.springframework.beans.BeanUtils.copyProperties;

import fr.frogdevelopment.ep.implementation.clean.CleanUpData;
import fr.frogdevelopment.ep.implementation.schedules.AddSchedules;
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
    private final AddSchedules addSchedules;
    private final AddTeam addTeam;
    private final AddVolunteer addVolunteer;
    private final AddTimetable addTimetable;

    public UploadData(CleanUpData cleanUpData,
                      AddSchedules addSchedules,
                      AddTeam addTeam,
                      AddVolunteer addVolunteer,
                      AddTimetable addTimetable) {
        this.cleanUpData = cleanUpData;
        this.addSchedules = addSchedules;
        this.addTeam = addTeam;
        this.addVolunteer = addVolunteer;
        this.addTimetable = addTimetable;
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

        result.getSchedules()
                .stream()
                .map(this::toSchedule)
                .forEach(addSchedules::call);

        result.getTimetables()
                .stream()
                .map(this::toTimetable)
                .forEach(addTimetable::call);
    }

    private Team toTeam(XlsTeam xls) {
        var team = Team.builder().build();
        copyProperties(xls, team);
        return team;
    }

    private Volunteer toVolunteer(XlsVolunteer xls) {
        var volunteer = Volunteer.builder().build();
        copyProperties(xls, volunteer);
        return volunteer;
    }

    private Schedule toSchedule(XlsSchedule xls) {
        var schedule = Schedule.builder().build();
        copyProperties(xls, schedule);
        return schedule;
    }

    private Timetable toTimetable(XlsTimetable xls) {
        var timetable = Timetable.builder().build();
        copyProperties(xls, timetable);
        return timetable;
    }
}
