package fr.frogdevelopment.ep.implementation;

import java.io.Serializable;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Team implements Serializable {

    private int id;
    private String name;
    private String abbreviation;
    private Set<Member> referents;
}
