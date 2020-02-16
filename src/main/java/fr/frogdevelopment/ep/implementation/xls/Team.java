package fr.frogdevelopment.ep.implementation.xls;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Team {

    private String name;
    private String abbreviation;
    private String referents;
}
