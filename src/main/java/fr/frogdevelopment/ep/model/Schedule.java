package fr.frogdevelopment.ep.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
public class Schedule implements Serializable {

    private int id;
    private LocalDateTime from;
    private LocalDateTime to;
    private Location where;
    private String teamCode;
    @EqualsAndHashCode.Exclude
    private String volunteerRef;

    public enum Location {
        FOUILLES, BRACELET, LITIGES, AUTRES
    }
}
