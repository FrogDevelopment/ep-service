package fr.frogdevelopment.ep.model;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Timetable implements Serializable {

    private int id;
    @NonNull
    private String ref;
    @NonNull
    private DayOfWeek dayOfWeek;
    @NonNull
    private LocalTime startTime;
    @NonNull
    private LocalTime endTime;
    private int expectedBracelet;
    private int expectedFouille;
    private int expectedLitiges;
    private String description;

    private String title;
    private String duration;
    private int expectedTotal;

    private int actualBracelet;
    private int actualFouille;
    private int actualLitiges;
    private int actualTotal;
}
