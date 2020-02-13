package fr.frogdevelopment.ep.implementation;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Member implements Serializable {

    private String id;
    private String lastName;
    private String firstName;
    private int teamId;
}
