package fr.frogdevelopment.ep.implementation.xls.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class XlsTeam {

    @NonNull
    private String name;
    @NonNull
    private String code;
}
