package fr.frogdevelopment.ep.application.export;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ExportData implements Serializable {

    LocalDateTime startSchedule;
    LocalDateTime endSchedule;
    String location;
}
