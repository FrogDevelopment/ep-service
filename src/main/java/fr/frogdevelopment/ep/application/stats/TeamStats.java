package fr.frogdevelopment.ep.application.stats;

import fr.frogdevelopment.ep.model.Schedule;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamStats {

    @NonNull
    private String code;
    private Set<Schedule> schedules;

}
