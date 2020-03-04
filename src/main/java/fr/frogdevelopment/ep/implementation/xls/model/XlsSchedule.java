package fr.frogdevelopment.ep.implementation.xls.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class XlsSchedule {

    private String location;
    private String timetableRef;
    private String volunteerRef;

}
