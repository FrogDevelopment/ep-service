package fr.frogdevelopment.ep.implementation.xls;

import static fr.frogdevelopment.ep.model.Schedule.Location.AUTRES;
import static fr.frogdevelopment.ep.model.Schedule.Location.BRACELET;
import static fr.frogdevelopment.ep.model.Schedule.Location.FOUILLES;
import static fr.frogdevelopment.ep.model.Schedule.Location.LITIGES;
import static java.util.regex.Pattern.compile;
import static org.apache.commons.lang3.StringUtils.isAllBlank;
import static org.apache.commons.lang3.StringUtils.isAnyBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import fr.frogdevelopment.ep.implementation.schedules.AddSchedule;
import fr.frogdevelopment.ep.implementation.teams.AddTeam;
import fr.frogdevelopment.ep.implementation.volunteers.AddVolunteer;
import fr.frogdevelopment.ep.implementation.xls.ExcelParameters.Planning.Day;
import fr.frogdevelopment.ep.model.Schedule;
import fr.frogdevelopment.ep.model.Schedule.Location;
import fr.frogdevelopment.ep.model.Team;
import fr.frogdevelopment.ep.model.Volunteer;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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
    private final AddVolunteer addVolunteer;
    private final AddSchedule addSchedule;

    public ReadXls(ExcelParameters parameters,
                   AddTeam addTeam,
                   AddVolunteer addVolunteer,
                   AddSchedule addSchedule) {
        this.parameters = parameters;
        this.addTeam = addTeam;
        this.addVolunteer = addVolunteer;
        this.addSchedule = addSchedule;
    }

    public void call(InputStream inputStream) {
        var teams = new HashMap<String, Team>();

        try (Workbook workbook = new HSSFWorkbook(inputStream)) {
            readTeams(workbook, parameters, teams);
            readVolunteers(workbook, parameters, teams);

            teams.values().forEach(team -> {
                addTeam.call(team);
//                var mapSchedules = new HashMap<Schedule, Integer>();
                team.getVolunteers().forEach(volunteer -> {
                    addVolunteer.call(volunteer);
                    volunteer.getSchedules().forEach(addSchedule::call);
//                    volunteer.getSchedules().forEach(schedule -> mapSchedules.merge(schedule, 1, Integer::sum));
                });

//                if (!mapSchedules.isEmpty()) { // Boss don't have schedules
//                    Integer max = Collections.max(mapSchedules.values());
//                    mapSchedules.forEach((schedule, total) -> {
//                        if (total.equals(max)) { // is a team schedule
//                            schedule.setVolunteerRef(null);
//                        }
//                        addSchedule.call(schedule);
//                    });
//                }
            });
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

            teams.put(team.getCode(), team);
        }
    }

    private void readVolunteers(Workbook workbook,
                                ExcelParameters parameters,
                                Map<String, Team> teams) {
        log.info("Reading '{}'", parameters.getPlanning().getSheetName());
        var datatypeSheet = workbook.getSheet(parameters.getPlanning().getSheetName());

        var rowNum = parameters.getPlanning().getFirstRow();
        var rowHeader = datatypeSheet.getRow(rowNum++);
        var dateTimes = new HashMap<Integer, Pair<LocalDateTime, LocalDateTime>>();

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

    private void handleRow(Map<String, Team> teams, HashMap<Integer, Pair<LocalDateTime, LocalDateTime>> dateTimes,
                           Day friday,
                           Day sunday, Row row, String cellLastName, String cellFirstName, String cellTeam) {
        var volunteer = Volunteer.builder()
                .ref(UUID.randomUUID().toString())
                .lastName(cellLastName)
                .firstName(cellFirstName)
                .phoneNumber(randomPhoneNumber()) // fixme
                .email(randomEmail(cellLastName, cellFirstName)) // fixme
                .teamCode(cellTeam)
                .build();

        if (teams.containsKey(cellTeam)) {
            Team team = teams.get(cellTeam);
            team.getVolunteers().add(volunteer);
            addSchedules(dateTimes, friday, sunday, row, volunteer);
        } else {
            log.warn("Volunteer {} without team", volunteer);
        }
    }

    private void addSchedules(HashMap<Integer, Pair<LocalDateTime, LocalDateTime>> dateTimes, Day friday,
                              Day sunday, Row row, Volunteer volunteer) {
        for (var i = friday.getStart(); i <= sunday.getEnd(); i++) {
            var value = getCellStringValue(row, i);
            if (isNotBlank(value)) {
                Pair<LocalDateTime, LocalDateTime> schedules = dateTimes.get(i);

                var schedule = Schedule.builder()
                        .start(schedules.getLeft())
                        .end(schedules.getRight())
                        .location(getLocation(value))
                        .teamCode(volunteer.getTeamCode())
                        .volunteerRef(volunteer.getRef())
                        .build();
                volunteer.getSchedules().add(schedule);
            }
        }
    }

    private static Location getLocation(String location) {
        switch (location) {
            case "F":
                return FOUILLES;
            case "B":
                return BRACELET;
            case "L":
                return LITIGES;
            default:
                return AUTRES;
        }
    }

    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"))
            .appendOptional(DateTimeFormatter.ofPattern(("MM/dd/yyyy H:mm")))
            .toFormatter();

    private Map<Integer, Pair<LocalDateTime, LocalDateTime>> toDates(Row rowHeader, String dayDate, int start,
                                                                     int end) {
        var dates = new HashMap<Integer, Pair<LocalDateTime, LocalDateTime>>();
        for (var i = start; i <= end; i++) {
            var cellValue = rowHeader.getCell(i).getRichStringCellValue().toString();
            var matcher = DATE_PATTERN.matcher(cellValue);
            if (matcher.find()) {
                LocalDateTime from = LocalDateTime.parse(format(dayDate, matcher.group("from")), DATE_TIME_FORMATTER);
                LocalDateTime to = LocalDateTime.parse(format(dayDate, matcher.group("to")), DATE_TIME_FORMATTER);
                if (from.getHour()
                        < 12) { // Friday case when working from 00:15 to 03:00 -> in fact it is Saturday morning
                    from = from.plusDays(1);
                    to = to.plusDays(1);
                }

                if (to.isBefore(from)) {
                    to = to.plusDays(1);
                }
                dates.put(i, Pair.of(from, to)
                );
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
