package fr.frogdevelopment.ep.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
    private String lastName;
    @NonNull
    private String firstName;
    @NonNull
    private String phoneNumber;
    @NonNull
    private String email;
    private String teamCode;
    private boolean referent;
}
