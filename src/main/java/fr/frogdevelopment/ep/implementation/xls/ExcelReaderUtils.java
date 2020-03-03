package fr.frogdevelopment.ep.implementation.xls;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Random;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Row;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ExcelReaderUtils {

    private static final Random PHONE_NUMBER_GENERATOR = new Random();

    static String schedulesTitle(DayOfWeek dayOfWeek, LocalTime start, LocalTime end) {
        return String.format("%s: %s - %s", dayOfWeek, start.toString(), end.toString());
    }

    static String getCellStringValue(Row row, int i) {
        var cell = row.getCell(i);
        return cell != null ? cell.getStringCellValue() : "";
    }

    static int getNumericCellValue(Row row, int i) {
        var cell = row.getCell(i);
        return cell != null ? Double.valueOf(cell.getNumericCellValue()).intValue() : -1;
    }

    static String randomPhoneNumber() {
        var sixOrSeven = PHONE_NUMBER_GENERATOR.nextBoolean() ? "6" : "7";
        var num1 = PHONE_NUMBER_GENERATOR.nextInt(99);
        var num2 = PHONE_NUMBER_GENERATOR.nextInt(99);
        var num3 = PHONE_NUMBER_GENERATOR.nextInt(99);
        var num4 = PHONE_NUMBER_GENERATOR.nextInt(99);

        return String.format("0%s %02d %02d %02d %02d", sixOrSeven, num1, num2, num3, num4);
    }

    static String randomEmail(String lastName, String firstName) {
        return String.format("%s.%s@test.com", lastName.replace(" ", "_"), firstName.replace(" ", "_"))
                .toLowerCase();
    }

    static String getCharForNumber(int i) {
        if (i <= 0) {
            return null;
        }

        if (i > 27) {
            return (char) 65 + getCharForNumber(i - 26);
        }

        return String.valueOf((char) (i + 64));
    }
}
