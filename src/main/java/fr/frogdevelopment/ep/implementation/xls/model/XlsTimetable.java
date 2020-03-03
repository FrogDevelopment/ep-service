package fr.frogdevelopment.ep.implementation.xls.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class XlsTimetable {

    private String location;
    private String volunteerRef;

}
