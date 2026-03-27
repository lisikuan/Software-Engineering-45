package edu.bupt.tarecruitment.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime now() { return LocalDateTime.now(); }

    public static String toIsoString(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(ISO_FORMATTER);
    }

    public static LocalDateTime fromIsoString(String isoString) {
        if (isoString == null || isoString.isEmpty()) return null;
        return LocalDateTime.parse(isoString, ISO_FORMATTER);
    }

    public static String toDisplayString(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DISPLAY_FORMATTER);
    }

    public static boolean isSameDay(LocalDateTime date1, LocalDateTime date2) {
        if (date1 == null || date2 == null) return false;
        return date1.toLocalDate().equals(date2.toLocalDate());
    }

    public static boolean isAfter(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        if (dateTime1 == null || dateTime2 == null) return false;
        return dateTime1.isAfter(dateTime2);
    }

    public static boolean isBefore(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        if (dateTime1 == null || dateTime2 == null) return false;
        return dateTime1.isBefore(dateTime2);
    }
}