package fr.frogdevelopment.ep.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class Team implements Serializable {

    private int id;
    @NonNull
    private String name;
    @NonNull
    private String code;
    @Default
    private Set<Volunteer> referents = new HashSet<>();
    @Default
    private Set<Volunteer> volunteers = new HashSet<>();
    @Default
    private Set<Schedule> schedules = new HashSet<>();

    public String getFullName() {
        return String.join(" - ", code, name);
    }
}
