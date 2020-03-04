package fr.frogdevelopment.ep.implementation.xls.model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class XlsTimetable {

    @NonNull
    private String ref;
    @NonNull
    private DayOfWeek dayOfWeek;
    @NonNull
    private LocalTime start;
    @NonNull
    private LocalTime end;
    private int expectedBracelet;
    private int expectedFouille;
    private int expectedLitiges;
    private String description;
}
