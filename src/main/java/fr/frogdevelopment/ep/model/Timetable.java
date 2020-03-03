package fr.frogdevelopment.ep.model;

import java.io.Serializable;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Timetable implements Serializable {

    private int id;
    private Location location;
    private String scheduleRef;
    private String volunteerRef;

    @Singular("volunteer")
    private Set<String> volunteers;

}
