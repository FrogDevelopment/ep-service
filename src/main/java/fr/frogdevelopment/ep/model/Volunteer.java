package fr.frogdevelopment.ep.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Volunteer implements Serializable {

    private int id;
    @NonNull
    private String ref;
    @NonNull
    private String lastName;
    @NonNull
    private String firstName;
    private String phoneNumber;
    private String email;
    private String teamCode;
    private String friendsGroup;
    private boolean referent;

    @Default
    private Set<Schedule> schedules = new HashSet<>();

    public String getFullName() {
        return String.join(" ", lastName, firstName);
    }
}
