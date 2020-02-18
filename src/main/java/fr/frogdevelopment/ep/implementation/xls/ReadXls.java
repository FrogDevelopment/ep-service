package fr.frogdevelopment.ep.implementation.xls;

import static java.util.regex.Pattern.compile;
import static org.apache.commons.lang3.StringUtils.isAllBlank;
import static org.apache.commons.lang3.StringUtils.isAnyBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import fr.frogdevelopment.ep.implementation.AddMember;
import fr.frogdevelopment.ep.implementation.AddSchedule;
import fr.frogdevelopment.ep.implementation.AddTeam;
import fr.frogdevelopment.ep.implementation.xls.ExcelParameters.Planning.Day;
import fr.frogdevelopment.ep.model.Member;
import fr.frogdevelopment.ep.model.Schedule;
import fr.frogdevelopment.ep.model.Team;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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
                    .code(getCellStringValue(row, 2))
//                    .referents() // fixme
                    .build();

            // fixme
            if ("Litiges".equals(team.getCode())) {
                team.setCode("LC");
            } else if ("Chefs".equals(team.getCode())) {
                team.setCode("Chef");
            }

            addTeam.call(team);
            teams.put(team.getCode(), team);
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
            var cellFirstName = getCellStringValue(row, 1);
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
        var member = Member.builder()
                .lastName(cellLastName)
                .firstName(cellFirstName)
                .phoneNumber(randomPhoneNumber()) // fixme
                .email(randomEmail(cellLastName, cellFirstName)) // fixme
                .teamCode(cellTeam)
                .build();

        if (teams.containsKey(cellTeam)) {
            addMember.call(member);
            Team team = teams.get(cellTeam);
            team.getMembers().add(member);
            handleTeamSchedule(dateTimes, friday, sunday, row, team);
        } else {
            log.warn("Member {} without team", member);
        }
    }

    private void handleTeamSchedule(HashMap<Integer, Pair<String, String>> dateTimes, Day friday, Day sunday, Row row,
                                    Team team) {
        if (team.getSchedules().isEmpty()) {
            for (var i = friday.getStart(); i <= sunday.getEnd(); i++) {
                var value = getCellStringValue(row, i);
                if (isNotBlank(value)) {
                    Pair<String, String> schedules = dateTimes.get(i);
                    var schedule = Schedule.builder()
                            .from(LocalDateTime.parse(schedules.getLeft(), DATE_TIME_FORMATTER))
                            .to(LocalDateTime.parse(schedules.getRight(), DATE_TIME_FORMATTER))
                            .teamCode(team.getCode())
                            .where(value)
                            .build();
                    addSchedule.call(schedule);
                    team.getSchedules().add(schedule);
                }
            }
        }
    }

    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"))
            .appendOptional(DateTimeFormatter.ofPattern(("MM/dd/yyyy H:mm")))
            .toFormatter();

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

    private static String format(String dayDate, String split) {
        return String.format("%s %s", dayDate, split.trim());
    }

    private static String getCellStringValue(Row row, int i) {
        var cell = row.getCell(i);
        return cell != null ? cell.getStringCellValue() : "";
    }

    private static String randomPhoneNumber() {
        Random generator = new Random();
        var sixOrSeven = generator.nextBoolean() ? "6" : "7";
        var num1 = generator.nextInt(99);
        var num2 = generator.nextInt(99);
        var num3 = generator.nextInt(99);
        var num4 = generator.nextInt(99);

        return String.format("0%s %02d %02d %02d %02d", sixOrSeven, num1, num2, num3, num4);
    }

    private static String randomEmail(String lastName, String firstName) {
        return String.format("%s.%s@test.com", lastName.replace(" ", "_"), firstName.replace(" ", "_")).toLowerCase();
    }
}
