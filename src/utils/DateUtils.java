package utils;


import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public final class DateUtils {
    private DateUtils() {}

    public static int yearsBetween(LocalDate from, LocalDate to) {
        return Period.between(from, to).getYears();
    }

    public static String format(LocalDate date) {
        return date == null ? "" : date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static LocalDate parse(String iso) {
        return LocalDate.parse(iso, DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
