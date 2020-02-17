package fr.frogdevelopment.ep.domain;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class Member implements Serializable {

    private int id;
    @NonNull
    private String lastName;
    @NonNull
    private String firstName;
    @NonNull
    private String phoneNumber;
    @NonNull
    private String email;
    private Integer teamId;
}
