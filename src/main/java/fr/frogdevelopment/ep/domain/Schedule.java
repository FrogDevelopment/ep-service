package fr.frogdevelopment.ep.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Schedule implements Serializable {

    private int id;
    private LocalDateTime from;
    private LocalDateTime to;
    private String where;
    private int who;
}
