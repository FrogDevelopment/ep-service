package fr.frogdevelopment.ep.implementation.xls;

import fr.frogdevelopment.ep.model.Schedule;
import fr.frogdevelopment.ep.model.Team;
import fr.frogdevelopment.ep.model.Volunteer;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Planning {

    private List<Team> teams;
    private List<Volunteer> volunteers;
    private List<Schedule> schedules;

}
