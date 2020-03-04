package fr.frogdevelopment.ep.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule implements Serializable {

    private int id;
    private Location location;
    private String timeTableRef;
    private String volunteerRef;
}
