package fr.frogdevelopment.ep.implementation.xls;

import fr.frogdevelopment.ep.implementation.xls.model.XlsSchedule;
import fr.frogdevelopment.ep.implementation.xls.model.XlsTeam;
import fr.frogdevelopment.ep.implementation.xls.model.XlsVolunteer;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
class Result {

    @Singular("schedule")
    private List<XlsSchedule> schedules;
    private List<XlsTeam> teams;
    private List<XlsVolunteer> volunteers;
}
