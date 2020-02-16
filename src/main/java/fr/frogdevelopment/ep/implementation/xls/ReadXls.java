package fr.frogdevelopment.ep.implementation.xls;

import static java.util.regex.Pattern.compile;

import fr.frogdevelopment.ep.implementation.xls.ExcelParameters.Planning.Day;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReadXls {

    private static final Pattern DATE_PATTERN = compile("(?<from>\\d{1,2}:\\d{1,2}) . (?<to>\\d{1,2}:\\d{1,2})");

    private final List<Team> teams = new ArrayList<>();
    private final List<Member> members = new ArrayList<>();
    private final List<Schedule> schedules = new ArrayList<>();

    private final ExcelParameters parameters;

    public ReadXls(ExcelParameters parameters) {
        this.parameters = parameters;
    }

    public void call(InputStream inputStream) {
        try (Workbook workbook = new HSSFWorkbook(inputStream)) {
            readTeams(workbook, parameters);
            readMembers(workbook, parameters);

            Planning planning = Planning.builder()
                    .teams(teams)
                    .members(members)
                    .schedules(schedules)
                    .build();

            log.info("{}", planning);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void readTeams(Workbook workbook,
                           ExcelParameters parameters) {
        log.info("Reading '{}'", parameters.getTeam().getSheetName());
        var datatypeSheet = workbook.getSheet(parameters.getTeam().getSheetName());

        var rowNum = parameters.getTeam().getFirstRow();
        while (true) {
            var row = datatypeSheet.getRow(rowNum++);

            if (row == null) {
                break;
            }

            var cellRef = getCellStringValue(row, 0);
            var cellName = getCellStringValue(row, 1);
            var cellAbb = getCellStringValue(row, 2);
            teams.add(Team.builder()
                    .name(cellName)
                    .abbreviation(cellAbb)
                    .referents(cellRef)
                    .build());
        }
    }

    private void readMembers(Workbook workbook,
                             ExcelParameters parameters) {
        log.info("Reading '{}'", parameters.getPlanning().getSheetName());
        var datatypeSheet = workbook.getSheet(parameters.getPlanning().getSheetName());

        var rowNum = parameters.getPlanning().getFirstRow();
        var rowHeader = datatypeSheet.getRow(rowNum++);
        var dateTimes = new HashMap<Integer, Pair<String, String>>();

        Day friday = parameters.getPlanning().getFriday();
        dateTimes.putAll(toDates(rowHeader, friday.getDate(), friday.getStart(), friday.getEnd()));

        Day saturday = parameters.getPlanning().getSaturday();
        dateTimes.putAll(toDates(rowHeader, saturday.getDate(), saturday.getStart(), saturday.getEnd()));

        Day sunday = parameters.getPlanning().getSunday();
        dateTimes.putAll(toDates(rowHeader, sunday.getDate(), sunday.getStart(), sunday.getEnd()));

        while (true) {
            var row = datatypeSheet.getRow(rowNum++);

            if (row == null) {
                break;
            }

            var cellLastName = getCellStringValue(row, 0);
            var cellFirstName = getCellStringValue(row, 1);
            var cellTeam = getCellStringValue(row, 2);

            if (StringUtils.isAnyBlank(cellLastName)) {
                continue;
            }

            var uuid = UUID.randomUUID().toString();
            members.add(Member.builder()
                    .id(uuid)
                    .firstName(cellFirstName)
                    .lastName(cellLastName)
                    .team(cellTeam)
                    .build());

            for (var i = friday.getStart(); i <= sunday.getEnd(); i++) {
                var value = getCellStringValue(row, i);
                switch (value) {
                    case "F":
                    case "B":
                        Pair<String, String> schedule = dateTimes.get(i);
                        schedules.add(Schedule.builder()
                                .from(schedule.getLeft())
                                .to(schedule.getRight())
                                .who(uuid)
                                .where(getWhere(value))
                                .build());
                        break;
                    default:
                }
            }
        }
    }

    private String getWhere(String abb) {
        switch (abb) {
            case "F":
                return "Fouille";
            case "B":
                return "Bracelet";
            case "L":
                return "Litiges";
            default:
                return "Inconnu";
        }
    }

    private Map<Integer, Pair<String, String>> toDates(Row rowHeader, String dayDate, int from, int to) {
        var dates = new HashMap<Integer, Pair<String, String>>();
        for (var i = from; i <= to; i++) {
            var cellValue = rowHeader.getCell(i).getRichStringCellValue().toString();
            var matcher = DATE_PATTERN.matcher(cellValue);
            if (matcher.find()) {
                dates.put(i, Pair.of(format(dayDate, matcher.group("from")), format(dayDate, matcher.group("to"))));
            } else {
                log.warn("No date matching with {}", cellValue);
            }
        }

        return dates;
    }

    private String format(String dayDate, String split) {
        return String.format("%s %s", dayDate, split.trim());
    }

    private String getCellStringValue(Row row, int i) {
        Cell cell = row.getCell(i);
        return cell != null ? cell.getStringCellValue() : "";
    }

}
