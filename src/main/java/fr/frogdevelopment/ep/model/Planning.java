package fr.frogdevelopment.ep.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Planning {

    private int id;
    @NonNull
    private LocalDateTime start;
    @NonNull
    private LocalDateTime end;
    private int expectedBracelet;
    private int expectedFouille;
    private int expectedLitiges;
    private String description;

    private String title;
    private double duration;
    private int expectedTotal;
}
