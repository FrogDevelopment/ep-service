package fr.frogdevelopment.ep.application.xls.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class XlsVolunteer {

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
}
