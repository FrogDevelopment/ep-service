package fr.frogdevelopment.ep.implementation.xls;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Member {

    private String id;
    private String lastName;
    private String firstName;
    private String team;
}
