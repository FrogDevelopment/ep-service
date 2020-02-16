package fr.frogdevelopment.ep.implementation.xls;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Schedule {

    private String from;
    private String to;
    private String who;
    private String where;
}
