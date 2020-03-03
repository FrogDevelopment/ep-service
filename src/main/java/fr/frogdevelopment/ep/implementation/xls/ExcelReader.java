package fr.frogdevelopment.ep.implementation.xls;

import static fr.frogdevelopment.ep.implementation.xls.ExcelReaderUtils.getCellStringValue;
import static fr.frogdevelopment.ep.implementation.xls.ExcelReaderUtils.getCharForNumber;
import static fr.frogdevelopment.ep.implementation.xls.ExcelReaderUtils.getNumericCellValue;
import static fr.frogdevelopment.ep.implementation.xls.ExcelReaderUtils.randomEmail;
import static fr.frogdevelopment.ep.implementation.xls.ExcelReaderUtils.randomPhoneNumber;
import static fr.frogdevelopment.ep.implementation.xls.ExcelReaderUtils.schedulesTitle;
import static fr.frogdevelopment.ep.implementation.xls.ParseTeams.readTeams;
import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.LocalTime.parse;
import static java.util.regex.Pattern.compile;
import static org.apache.commons.lang3.StringUtils.isAnyBlank;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import fr.frogdevelopment.ep.implementation.xls.model.XlsSchedule;
import fr.frogdevelopment.ep.implementation.xls.model.XlsSchedule.XlsScheduleBuilder;
import fr.frogdevelopment.ep.implementation.xls.model.XlsTimetable;
import fr.frogdevelopment.ep.implementation.xls.model.XlsVolunteer;
import java.io.IOException;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

@Slf4j
public class ExcelReader {

    private static final Pattern TIME_PATTERN = compile("(?<start>\\d{1,2}:\\d{1,2}) . (?<end>\\d{1,2}:\\d{1,2})");
    private static final DateTimeFormatter TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("HH:mm"))
            .appendOptional(DateTimeFormatter.ofPattern(("H:mm")))
            .toFormatter();

    private static final int SCHEDULES_START_ROW = 4;
    private static final int SCHEDULES_FRIDAY_ROW = 11;
    private static final int SCHEDULES_SATURDAY_ROW = 17;
    private static final int SCHEDULES_TIMES_COLUMN = 1;
    private static final int SCHEDULES_BRACELET_COLUMN = 3;
    private static final int SCHEDULES_FOUILLES_COLUMN = 4;
    private static final int SCHEDULES_LITIGES_COLUMN = 5;
    private static final int SCHEDULES_DESCRIPTION_COLUMN = 8;

    private static final int TEAM_START_ROW = 3;
    private static final int TEAM_NAME_COLUMN = 1;
    private static final int TEAM_CODE_COLUMN = 2;

    private static final int VOLUNTEER_START_ROW = 10;
    private static final int VOLUNTEER_LAST_NAME_COLUMN = 0;
    private static final int VOLUNTEER_FIRST_NAME_COLUMN = 1;
    private static final int VOLUNTEER_TEAM_COLUMN = 2;
    private static final int VOLUNTEER_CIRCLE_COLUMN = 3;

    private static final int TIMETABLE_FRIDAY_COLUMN_START = 9;
    private static final int TIMETABLE_FRIDAY_COLUMN_END = 12;
    private static final int TIMETABLE_SATURDAY_COLUMN_END = 17;
    private static final int TIMETABLE_SUNDAY_COLUMN_END = 20;

    private final Map<String, XlsSchedule.XlsScheduleBuilder> plannings = new HashMap<>();
    private final Map<Integer, XlsSchedule.XlsScheduleBuilder> planningsByColumn = new HashMap<>();
    private final List<XlsVolunteer> volunteers = new ArrayList<>();

    public static Result read(InputStream inputStream) {
        return new ExcelReader().execute(inputStream);
    }

    private Result execute(InputStream inputStream) {
        try (Workbook workbook = new HSSFWorkbook(inputStream)) {
            var teams = readTeams(workbook);
            readSchedules(workbook);
            readVolunteers(workbook);

            var resultBuilder = Result.builder();

            plannings.values()
                    .stream()
                    .map(XlsScheduleBuilder::build)
                    .forEach(resultBuilder::schedule);

            return resultBuilder
                    .teams(teams)
                    .volunteers(volunteers)
                    .build();

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private void readSchedules(Workbook workbook) {
        log.info("Parsing schedules");
        var datatypeSheet = workbook.getSheet("horaires");

        var rowNum = SCHEDULES_START_ROW;
        while (true) {
            var row = datatypeSheet.getRow(rowNum++);

            if (row == null) {
                break;
            }

            DayOfWeek dayOfWeek;
            if (rowNum < SCHEDULES_FRIDAY_ROW) {
                dayOfWeek = FRIDAY;
            } else if (rowNum < SCHEDULES_SATURDAY_ROW) {
                dayOfWeek = SATURDAY;
            } else {
                dayOfWeek = SUNDAY;
            }

            var scheduleTimes = getCellStringValue(row, SCHEDULES_TIMES_COLUMN);

            if (isBlank(scheduleTimes)) {
                break;
            }

            var matcher = TIME_PATTERN.matcher(scheduleTimes);
            if (matcher.find()) {
                addSchedule(dayOfWeek, row, matcher);
            } else {
                log.warn("Incorrect row {}", rowNum);
            }
        }
    }

    private void addSchedule(DayOfWeek dayOfWeek, Row row, Matcher matcher) {
        var start = parse(matcher.group("start"), TIME_FORMATTER);
        var end = parse(matcher.group("end"), TIME_FORMATTER);
        var expectedBracelet = getNumericCellValue(row, SCHEDULES_BRACELET_COLUMN);
        var expectedFouille = getNumericCellValue(row, SCHEDULES_FOUILLES_COLUMN);
        var expectedLitiges = getNumericCellValue(row, SCHEDULES_LITIGES_COLUMN);
        var description = getCellStringValue(row, SCHEDULES_DESCRIPTION_COLUMN);

        var builder = XlsSchedule.builder()
                .ref(UUID.randomUUID().toString())
                .dayOfWeek(dayOfWeek)
                .start(start)
                .end(end)
                .expectedBracelet(expectedBracelet)
                .expectedFouille(expectedFouille)
                .expectedLitiges(expectedLitiges)
                .description(description);

        plannings.put(schedulesTitle(dayOfWeek, start, end), builder);
    }

    private void readVolunteers(Workbook workbook) {
        log.info("Parsing Timetables");
        var datatypeSheet = workbook.getSheet("EP_planning_général");

        var rowNum = VOLUNTEER_START_ROW;
        var rowHeader = datatypeSheet.getRow(rowNum++);

        for (var colNum = TIMETABLE_FRIDAY_COLUMN_START; colNum <= TIMETABLE_SUNDAY_COLUMN_END; colNum++) {
            var cellValue = rowHeader.getCell(colNum).getRichStringCellValue().toString();
            var matcher = TIME_PATTERN.matcher(cellValue);
            if (matcher.find()) {

                DayOfWeek dayOfWeek;
                if (colNum <= TIMETABLE_FRIDAY_COLUMN_END) {
                    dayOfWeek = FRIDAY;
                } else if (colNum <= TIMETABLE_SATURDAY_COLUMN_END) {
                    dayOfWeek = SATURDAY;
                } else {
                    dayOfWeek = SUNDAY;
                }

                var start = parse(matcher.group("start"), TIME_FORMATTER);
                var end = parse(matcher.group("end"), TIME_FORMATTER);
                planningsByColumn.put(colNum, plannings.get(schedulesTitle(dayOfWeek, start, end)));
            } else {
                log.warn("No date matching with {}", cellValue);
            }
        }

        var read = true;
        while (read) {
            var row = datatypeSheet.getRow(rowNum++);

            if (row == null) {
                log.warn("Row null, breaking parser at row {}", rowNum);
                break;
            }

            read = readVolunteer(row);
        }
    }

    private boolean readVolunteer(Row row) {
        var cellLastName = getCellStringValue(row, VOLUNTEER_LAST_NAME_COLUMN);

        if (isAnyBlank(cellLastName)) {
            log.warn("Missing data, stopping reading at row {}", row.getRowNum());
            return false;
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

        for (var i = TIMETABLE_FRIDAY_COLUMN_START; i <= TIMETABLE_SUNDAY_COLUMN_END; i++) {
            var value = getCellStringValue(row, i);
            if (isNotBlank(value)) {
                var timetable = XlsTimetable.builder()
                        .location(value)
                        .volunteerRef(volunteer.getRef())
                        .build();
                planningsByColumn.get(i).timetable(timetable);
            }
        }

        return true;
    }
}
