package fr.frogdevelopment.ep.model;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    // UI
    private DayOfWeek dayOfWeek;
    private LocalTime start;
    private LocalTime end;

    // UI
    private LocalDateTime startFull;
    private LocalDateTime endFull;
}
