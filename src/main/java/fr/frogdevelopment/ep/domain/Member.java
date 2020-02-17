package fr.frogdevelopment.ep.domain;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Member implements Serializable {

    private int id;
    private String lastName;
    private String firstName;
    private String phoneNumber;
    private String email;
    private Integer teamId;
}
