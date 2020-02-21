package fr.frogdevelopment.ep.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private List<Volunteer> referents = new ArrayList<>();
    @Default
    private List<Volunteer> volunteers = new ArrayList<>();
    @Default
    private Set<Schedule> schedules = new HashSet<>();

    public String getFullName() {
        return String.join(" - ", code, name);
    }
}
