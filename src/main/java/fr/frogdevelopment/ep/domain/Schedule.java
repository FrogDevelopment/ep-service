package fr.frogdevelopment.ep.domain;

import java.io.Serializable;
import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Schedule implements Serializable {

    private int id;
    private OffsetDateTime from;
    private OffsetDateTime to;
    private String where;
    private int who;
}
