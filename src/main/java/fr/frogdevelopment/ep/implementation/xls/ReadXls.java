package fr.frogdevelopment.ep.implementation.xls;

import static java.util.regex.Pattern.compile;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.isAllBlank;
import static org.apache.commons.lang3.StringUtils.isAnyBlank;

import fr.frogdevelopment.ep.domain.Member;
import fr.frogdevelopment.ep.domain.Schedule;
import fr.frogdevelopment.ep.domain.Team;
import fr.frogdevelopment.ep.implementation.AddMember;
import fr.frogdevelopment.ep.implementation.AddSchedule;
import fr.frogdevelopment.ep.implementation.AddTeam;
import fr.frogdevelopment.ep.implementation.xls.ExcelParameters.Planning.Day;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReadXls {

    private static final Pattern DATE_PATTERN = compile("(?<from>\\d{1,2}:\\d{1,2}) . (?<to>\\d{1,2}:\\d{1,2})");

    private final ExcelParameters parameters;
    private final AddTeam addTeam;
    private final AddMember addMember;
    private final AddSchedule addSchedule;

    public ReadXls(ExcelParameters parameters,
                   AddTeam addTeam,
                   AddMember addMember,
                   AddSchedule addSchedule) {
        this.parameters = parameters;
        this.addTeam = addTeam;
        this.addMember = addMember;
        this.addSchedule = addSchedule;
    }

    public void call(InputStream inputStream) {
        var teams = new HashMap<String, Team>();

        try (Workbook workbook = new HSSFWorkbook(inputStream)) {
            readTeams(workbook, parameters, teams);
            readMembers(workbook, parameters, teams);
            log.info("Done: {}", teams);

//            Planning planning = Planning.builder()
//                    .teams(teams)
//                    .members(members)
//                    .schedules(schedules)
//                    .build();
//
//            log.info("{}", planning);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void readTeams(Workbook workbook,
                           ExcelParameters parameters,
                           Map<String, Team> teams) {
        log.info("Reading '{}'", parameters.getTeam().getSheetName());
        var datatypeSheet = workbook.getSheet(parameters.getTeam().getSheetName());

        var rowNum = parameters.getTeam().getFirstRow();
        while (true) {
            var row = datatypeSheet.getRow(rowNum++);

            if (row == null) {
                break;
            }

            var team = Team.builder()
                    .name(getCellStringValue(row, 1))
                    .abbreviation(getCellStringValue(row, 2))
//                    .referents()
                    .build();

            addTeam.call(team);
            teams.put(team.getAbbreviation(), team);
        }
    }

    private void readMembers(Workbook workbook,
                             ExcelParameters parameters,
                             Map<String, Team> teams) {
        log.info("Reading '{}'", parameters.getPlanning().getSheetName());
        var datatypeSheet = workbook.getSheet(parameters.getPlanning().getSheetName());

        var rowNum = parameters.getPlanning().getFirstRow();
        var rowHeader = datatypeSheet.getRow(rowNum++);
        var dateTimes = new HashMap<Integer, Pair<String, String>>();

        var friday = parameters.getPlanning().getFriday();
        dateTimes.putAll(toDates(rowHeader, friday.getDate(), friday.getStart(), friday.getEnd()));

        var saturday = parameters.getPlanning().getSaturday();
        dateTimes.putAll(toDates(rowHeader, saturday.getDate(), saturday.getStart(), saturday.getEnd()));

        var sunday = parameters.getPlanning().getSunday();
        dateTimes.putAll(toDates(rowHeader, sunday.getDate(), sunday.getStart(), sunday.getEnd()));

        while (true) {
            var row = datatypeSheet.getRow(rowNum++);

            if (row == null) {
                log.warn("Row null, breaking parser at row {}", rowNum);
                break;
            }

            var cellLastName = getCellStringValue(row, 0);
            var cellFirstName = capitalize(getCellStringValue(row, 1));
            var cellTeam = getCellStringValue(row, 2);

            if (isAllBlank(cellLastName, cellFirstName, cellTeam)) {
                log.warn("No data, breaking parser at row {}", rowNum);
                break;
            }

            if (isAnyBlank(cellLastName, cellFirstName, cellTeam)) {
                log.warn("Missing data, skipping row {}", rowNum);
                continue;
            }

            handleRow(teams, dateTimes, friday, sunday, row, cellLastName, cellFirstName, cellTeam);
        }
    }

    private void handleRow(Map<String, Team> teams, HashMap<Integer, Pair<String, String>> dateTimes, Day friday,
                           Day sunday, Row row, String cellLastName, String cellFirstName, String cellTeam) {
        var memberBuilder = Member.builder()
                .lastName(cellLastName)
                .firstName(cellFirstName)
                .phoneNumber(UUID.randomUUID().toString()) // fixme
                .email(UUID.randomUUID().toString()); // fixme

        if (teams.containsKey(cellTeam)) {
            var team = teams.get(cellTeam);
            var member = memberBuilder
                    .teamId(team.getId())
                    .build();
            addMember.call(member);
            team.getMembers().add(member);

            handleTeamSchedule(dateTimes, friday, sunday, row, team);
        } else {
            log.warn("Member {} - {} without team", cellLastName, cellFirstName);
            addMember.call(memberBuilder.build());
        }
    }

    private void handleTeamSchedule(HashMap<Integer, Pair<String, String>> dateTimes, Day friday, Day sunday, Row row,
                                    Team team) {
        if (team.getSchedules().isEmpty()) {
            for (var i = friday.getStart(); i <= sunday.getEnd(); i++) {
                var value = getCellStringValue(row, i);
                switch (value) {
                    case "F":
                    case "B":
                        Pair<String, String> schedules = dateTimes.get(i);
                        var schedule = Schedule.builder()
                                .from(LocalDateTime.parse(schedules.getLeft(), DATE_TIME_FORMATTER))
                                .to(LocalDateTime.parse(schedules.getRight(), DATE_TIME_FORMATTER))
                                .who(team.getId())
                                .where(getWhere(value))
                                .build();
                        addSchedule.call(schedule);
                        team.getSchedules().add(schedule);
                        break;
                    default:
                }
            }
        }
    }

    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"))
            .appendOptional(DateTimeFormatter.ofPattern(("MM/dd/yyyy H:mm")))
            .toFormatter();

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
        var cell = row.getCell(i);
        return cell != null ? cell.getStringCellValue() : "";
    }

}
