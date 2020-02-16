package fr.frogdevelopment.ep.implementation.xls;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Planning {

    private List<Team> teams;
    private List<Member> members;
    private List<Schedule> schedules;

}
