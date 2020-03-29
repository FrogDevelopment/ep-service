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
public class Team implements Serializable {

    private int id;
    @NonNull
    private String name;
    @NonNull
    private String code;

    // for UI
    private int countMembers;
    private String referents;
}
