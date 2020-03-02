package fr.frogdevelopment.ep.implementation.xls;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ToString
@Component
@ConfigurationProperties("glide.xls")
public class ExcelParameters {

    private Team team;
    private Planning planning;

    @Getter
    @Setter
    @ToString
    public static class Team {

        private String sheetName;
        private int firstRow;
    }

    @Getter
    @Setter
    @ToString
    public static class Planning {

        private String sheetName;
        private int firstRow;
        private Day friday;
        private Day saturday;
        private Day sunday;

        @Getter
        @Setter
        @ToString
        public static class Day {

            private String date;
            private int start;
            private int end;
        }
    }

}
