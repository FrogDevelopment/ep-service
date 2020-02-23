package fr.frogdevelopment.ep.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule implements Serializable {

    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Location location;
    private String teamCode;
    @EqualsAndHashCode.Exclude
    private String volunteerRef;

    public enum Location {
        FOUILLES("F"), BRACELET("B"), LITIGES("L"), AUTRES("-");

        @Getter
        private final String code;

        Location(String code) {
            this.code = code;
        }
    }
}
