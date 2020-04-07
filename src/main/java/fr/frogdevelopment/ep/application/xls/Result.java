package fr.frogdevelopment.ep.application.xls;

import fr.frogdevelopment.ep.application.xls.model.XlsSchedule;
import fr.frogdevelopment.ep.application.xls.model.XlsTeam;
import fr.frogdevelopment.ep.application.xls.model.XlsTimetable;
import fr.frogdevelopment.ep.application.xls.model.XlsVolunteer;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Result {

    private List<XlsTimetable> timetables;
    private List<XlsTeam> teams;
    private List<XlsVolunteer> volunteers;
    private List<XlsSchedule> schedules;
}
