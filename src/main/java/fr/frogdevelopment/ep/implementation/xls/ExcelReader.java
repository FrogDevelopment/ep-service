package fr.frogdevelopment.ep.implementation.xls;

import static java.util.regex.Pattern.compile;
import static org.apache.commons.lang3.StringUtils.isAnyBlank;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import fr.frogdevelopment.ep.implementation.xls.model.XlsPlanning;
import fr.frogdevelopment.ep.implementation.xls.model.XlsSchedule;
import fr.frogdevelopment.ep.implementation.xls.model.XlsTeam;
import fr.frogdevelopment.ep.implementation.xls.model.XlsVolunteer;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

@Slf4j
class ExcelReader {

    private static final Pattern SCHEDULE_PATTERN = compile("(?<from>\\d{1,2}:\\d{1,2}) . (?<to>\\d{1,2}:\\d{1,2})");

    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"))
            .appendOptional(DateTimeFormatter.ofPattern(("MM/dd/yyyy H:mm")))
            .toFormatter();

    private static final DateTimeFormatter TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("HH:mm"))
            .appendOptional(DateTimeFormatter.ofPattern(("H:mm")))
            .toFormatter();

    private static final int PLANNING_START_ROW = 4;
    private static final int PLANNING_FRIDAY_ROW = 11;
    private static final int PLANNING_SATURDAY_ROW = 17;
    private static final int PLANNING_TIMES_COLUMN = 1;
    private static final int PLANNING_DESCRIPTION_COLUMN = 8;

    private static final int TEAM_NAME_COLUMN = 1;
    private static final int TEAM_CODE_COLUMN = 2;

    private static final int VOLUNTEER_LAST_NAME_COLUMN = 0;
    private static final int VOLUNTEER_FIRST_NAME_COLUMN = 1;
    private static final int VOLUNTEER_TEAM_COLUMN = 2;
    private static final int VOLUNTEER_CIRCLE_COLUMN = 3;
    private static final int PLANNING_FOUILLES_COLUMN = 4;
    private static final int PLANNING_LITIGES_COLUMN = 5;
    private static final int PLANNING_BRACELET_COLUMN = 3;

    private static final int SCHEDULES_COLUMN_START = 9;
    private static final int SCHEDULES_COLUMN_END = 20;

    private final ExcelParameters parameters;
    private final Random phoneNumberGenerator = new Random();

    private final Map<String, XlsPlanning.XlsPlanningBuilder> plannings = new HashMap<>();
    private final Map<Integer, XlsPlanning.XlsPlanningBuilder> planningsByColumn = new HashMap<>();
    private final List<XlsTeam> teams = new ArrayList<>();
    private final List<XlsVolunteer> volunteers = new ArrayList<>();

    ExcelReader(ExcelParameters parameters) {
        this.parameters = parameters;
    }

    Result execute(InputStream inputStream) {
        try (Workbook workbook = new HSSFWorkbook(inputStream)) {
            readPlannings(workbook);
            readTeams(workbook);
            readVolunteers(workbook);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }

    private void readPlannings(Workbook workbook) {
        log.info("Reading 'horaires'");
        var datatypeSheet = workbook.getSheet("horaires");

        String date;
        LocalDate localDate;
        var dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        var rowNum = PLANNING_START_ROW;

        while (true) {
            var row = datatypeSheet.getRow(rowNum++);

            if (row == null) {
                break;
            }

            if (rowNum < PLANNING_FRIDAY_ROW) {
                date = parameters.getPlanning().getFriday().getDate();
            } else if (rowNum < PLANNING_SATURDAY_ROW) {
                date = parameters.getPlanning().getSaturday().getDate();
            } else {
                date = parameters.getPlanning().getSunday().getDate();
            }

            localDate = LocalDate.parse(date, dateTimeFormatter);

            var planning = getCellStringValue(row, PLANNING_TIMES_COLUMN);

            if (isBlank(planning)) {
                break;
            }

            var matcher = SCHEDULE_PATTERN.matcher(planning);
            if (matcher.find()) {
                addPlanning(localDate, row, matcher);
            } else {
                log.warn("incorrect row {}", rowNum);
            }
        }
    }

    private void addPlanning(LocalDate localDate, Row row, Matcher matcher) {
        var start = localDate.atTime(LocalTime.parse(matcher.group("from"), TIME_FORMATTER));
        var end = localDate.atTime(LocalTime.parse(matcher.group("to"), TIME_FORMATTER));

        var expectedBracelet = getNumericCellValue(row, PLANNING_BRACELET_COLUMN);
        var expectedFouille = getNumericCellValue(row, PLANNING_FOUILLES_COLUMN);
        var expectedLitiges = getNumericCellValue(row, PLANNING_LITIGES_COLUMN);
        var description = getCellStringValue(row, PLANNING_DESCRIPTION_COLUMN);

        var builder = XlsPlanning.builder()
                .start(start)
                .end(end)
                .expectedBracelet(expectedBracelet)
                .expectedFouille(expectedFouille)
                .expectedLitiges(expectedLitiges)
                .description(description);

        plannings.put(schedulesTitle(start, end), builder);
    }

    private void readTeams(Workbook workbook) {
        log.info("Reading '{}'", parameters.getTeam().getSheetName());
        var datatypeSheet = workbook.getSheet(parameters.getTeam().getSheetName());

        var rowNum = parameters.getTeam().getFirstRow();
        while (true) {
            var row = datatypeSheet.getRow(rowNum++);

            if (row == null) {
                break;
            }

            var team = XlsTeam.builder()
                    .name(getCellStringValue(row, TEAM_NAME_COLUMN))
                    .code(getCellStringValue(row, TEAM_CODE_COLUMN))
//                    .referents() // fixme
                    .build();

            // fixme
            if ("Litiges".equals(team.getCode())) {
                team.setCode("LC");
            } else if ("Chefs".equals(team.getCode())) {
                team.setCode("Chef");
            }

            teams.add(team);
        }
    }

    private void readVolunteers(Workbook workbook) {
        var planning = parameters.getPlanning();
        log.info("Reading '{}'", planning.getSheetName());
        var datatypeSheet = workbook.getSheet(planning.getSheetName());

        var rowNum = planning.getFirstRow();
        var rowHeader = datatypeSheet.getRow(rowNum++);

        for (var i = SCHEDULES_COLUMN_START; i <= SCHEDULES_COLUMN_END; i++) {
            var cellValue = rowHeader.getCell(i).getRichStringCellValue().toString();
            var matcher = SCHEDULE_PATTERN.matcher(cellValue);
            if (matcher.find()) {

                String dayDate;
                if (i <= planning.getFriday().getEnd()) {
                    dayDate = planning.getFriday().getDate();
                } else if (i <= planning.getSaturday().getEnd()) {
                    dayDate = planning.getSaturday().getDate();
                } else {
                    dayDate = planning.getSunday().getDate();
                }

                LocalDateTime start = LocalDateTime.parse(format(dayDate, matcher.group("from")), DATE_TIME_FORMATTER);
                LocalDateTime end = LocalDateTime.parse(format(dayDate, matcher.group("to")), DATE_TIME_FORMATTER);
                planningsByColumn.put(i, plannings.get(schedulesTitle(start, end)));
            } else {
                log.warn("No date matching with {}", cellValue);
            }
        }

        while (true) {
            var row = datatypeSheet.getRow(rowNum++);

            if (row == null) {
                log.warn("Row null, breaking parser at row {}", rowNum);
                break;
            }

            readVolunteer(row);
        }
    }

    private void readVolunteer(Row row) {
        var cellLastName = getCellStringValue(row, VOLUNTEER_LAST_NAME_COLUMN);

        if (isAnyBlank(cellLastName)) {
            log.warn("Missing data, skipping row {}", row.getRowNum());
            return;
        }

        var cellFirstName = getCellStringValue(row, VOLUNTEER_FIRST_NAME_COLUMN);
        var cellTeam = getCellStringValue(row, VOLUNTEER_TEAM_COLUMN);

        var volunteer = XlsVolunteer.builder()
                .ref(UUID.randomUUID().toString())
                .lastName(cellLastName)
                .firstName(cellFirstName)
                .phoneNumber(randomPhoneNumber()) // fixme
                .email(randomEmail(cellLastName, cellFirstName)) // fixme
                .teamCode(cellTeam)
                .friendsGroup(getCharForNumber(getNumericCellValue(row, VOLUNTEER_CIRCLE_COLUMN)))
                .build();

        volunteers.add(volunteer);

        for (var i = SCHEDULES_COLUMN_START; i <= SCHEDULES_COLUMN_END; i++) {
            var value = getCellStringValue(row, i);
            if (isNotBlank(value)) {
                var schedule = XlsSchedule.builder()
                        .location(value)
                        .volunteerRef(volunteer.getRef())
                        .build();
                planningsByColumn.get(i).schedule(schedule);
            }
        }
    }

    private static String schedulesTitle(LocalDateTime start, LocalDateTime end) {
        return String.format("%s - %s", start.toString(), end.toString());
    }

    private static String format(String dayDate, String split) {
        return String.format("%s %s", dayDate, split.trim());
    }

    private static String getCellStringValue(Row row, int i) {
        var cell = row.getCell(i);
        return cell != null ? cell.getStringCellValue() : "";
    }

    private static int getNumericCellValue(Row row, int i) {
        var cell = row.getCell(i);
        return cell != null ? Double.valueOf(cell.getNumericCellValue()).intValue() : -1;
    }

    private String randomPhoneNumber() {
        var sixOrSeven = phoneNumberGenerator.nextBoolean() ? "6" : "7";
        var num1 = phoneNumberGenerator.nextInt(99);
        var num2 = phoneNumberGenerator.nextInt(99);
        var num3 = phoneNumberGenerator.nextInt(99);
        var num4 = phoneNumberGenerator.nextInt(99);

        return String.format("0%s %02d %02d %02d %02d", sixOrSeven, num1, num2, num3, num4);
    }

    private static String randomEmail(String lastName, String firstName) {
        return String.format("%s.%s@test.com", lastName.replace(" ", "_"), firstName.replace(" ", "_"))
                .toLowerCase();
    }

    private static String getCharForNumber(int i) {
        if (i <= 0) {
            return null;
        }

        if (i > 27) {
            return (char) 65 + getCharForNumber(i - 26);
        }

        return String.valueOf((char) (i + 64));
    }
}
