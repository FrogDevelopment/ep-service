package fr.frogdevelopment.ep.model;

import lombok.Getter;

public enum Location {
    FOUILLES("F"), BRACELET("B"), LITIGES("L"), AUTRES("-");

    @Getter
    private final String code;

    Location(String code) {
        this.code = code;
    }
}
