package fr.frogdevelopment.ep.domain;

import java.io.Serializable;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Singular;

@Data
@Builder
public class Team implements Serializable {

    private int id;
    @NonNull
    private String name;
    @NonNull
    private String abbreviation;
    @Singular("referent")
    private Set<Member> referents;
}
