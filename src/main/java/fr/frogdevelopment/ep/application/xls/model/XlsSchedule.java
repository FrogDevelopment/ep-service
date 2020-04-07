package fr.frogdevelopment.ep.application.xls.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class XlsSchedule {

    private String location;
    private String timetableRef;
    private String volunteerRef;

}
