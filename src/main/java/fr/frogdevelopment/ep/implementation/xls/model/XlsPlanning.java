package fr.frogdevelopment.ep.implementation.xls.model;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Singular;

@Data
@Builder
public class XlsPlanning {

    @NonNull
    private LocalDateTime start;
    @NonNull
    private LocalDateTime end;
    private int expectedBracelet;
    private int expectedFouille;
    private int expectedLitiges;
    private String description;

    @Singular("schedule")
    private List<XlsSchedule> schedules;
}
