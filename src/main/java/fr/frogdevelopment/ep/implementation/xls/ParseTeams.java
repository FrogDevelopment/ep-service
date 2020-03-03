package fr.frogdevelopment.ep.implementation.xls;

import static fr.frogdevelopment.ep.implementation.xls.ExcelReaderUtils.getCellStringValue;

import fr.frogdevelopment.ep.implementation.xls.model.XlsTeam;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;

@Slf4j
public class ParseTeams {

    private static final int TEAM_START_ROW = 3;
    private static final int TEAM_NAME_COLUMN = 1;
    private static final int TEAM_CODE_COLUMN = 2;

    static List<XlsTeam> readTeams(Workbook workbook) {
        log.info("Parsing Teams");
        var teams = new ArrayList<XlsTeam>();
        var datatypeSheet = workbook.getSheet("Equipes");

        var rowNum = TEAM_START_ROW;
        while (true) {
            var row = datatypeSheet.getRow(rowNum++);

            if (row == null) {
                break;
            }

            var team = XlsTeam.builder()
                    .name(getCellStringValue(row, TEAM_NAME_COLUMN))
                    .code(getCellStringValue(row, TEAM_CODE_COLUMN))
                    .build();

            // fixme
            if ("Litiges".equals(team.getCode())) {
                team.setCode("LC");
            } else if ("Chefs".equals(team.getCode())) {
                team.setCode("Chef");
            }

            teams.add(team);
        }

        return teams;
    }

}
