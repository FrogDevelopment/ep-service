package fr.frogdevelopment.ep.implementation.stats;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TimeSlot implements Serializable {

    DayOfWeek dayOfWeek;
    LocalTime start;
    LocalTime end;
}
