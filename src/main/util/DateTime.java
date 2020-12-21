package main.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Date;
import java.util.TimeZone;

/**
 * <h1>DateTime</h1>
 * DateTime class is used to handle any operations with datetime formatting and swapping between timezones.
 *
 * @author Lee Rhodes
 */
public class DateTime {
    private static final ResourceBundle rb = ResourceBundle.getBundle(Constants.PROPERTIES_PATH_BASE + "scheduler_" + Locale.getDefault().getLanguage());
    private static final String localZoneId = ZoneId.systemDefault().getId();
    private static final String dtPattern = rb.getString("datePattern") + ' ' + Constants.TIME_PATTERN;

    /**
     * getUTCTimestampNow() is used to get the current time as a timestamp in UTC. This format is compatible with
     * entry in the mySQL database.
     *
     * @return the timestamp of the current time in UTC.
     */
    public static Timestamp getUTCTimestampNow() {
        return localToUTCTimestamp(ZonedDateTime.now());
    }

    /**
     * getStartOfWeek() determines what day would be the start of the week for the passed in date.
     * The start of the week is SUNDAY for this method.
     *
     * @param date the date to base the start of the week off of.
     * @return the timestamp of the start of the week for the passed in date.
     */
    public static Timestamp getStartOfWeek(LocalDate date) {
        LocalDate previousSunday = date.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
        return Timestamp.valueOf(previousSunday.atTime(0,0,0,0));
    }

    /**
     * getEndOfWeek() determines what day would be the end of the week for the passed in date.
     * The start of the week is SATURDAY for this method.
     *
     * @param date the date to base the end of the week off of.
     * @return the timestamp of the end of the week for the passed in date.
     */
    public static Timestamp getEndOfWeek(LocalDate date) {
        LocalDate nextMonday = date.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        return Timestamp.valueOf(nextMonday.atTime(23,59,59,	99999999));
    }

    /**
     * getStartOfMonth() determines what day would be the start of the month for the passed in date.
     *
     * @param date the date to base the start of the month off of.
     * @return the timestamp of the start of the month for the passed in date.
     */
    public static Timestamp getStartOfMonth(LocalDate date) {
        LocalDate startOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        return Timestamp.valueOf(startOfMonth.atTime(0,0,0,0));
    }

    /**
     * getEndOfMonth() determines what day would be the end of the month for the passed in date.
     *
     * @param date the date to base the end of the month off of.
     * @return the timestamp of the end of the month for the passed in date.
     */
    public static Timestamp getEndOfMonth(LocalDate date) {
        LocalDate startOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());
        return Timestamp.valueOf(startOfMonth.atTime(23,59,59,99999999));
    }

    /**
     * localToWeekDisplay() formats a given date into a display for the range of the week.
     * [SUNDAY - SATURDAY]
     *
     * @param date the date to base the display off of.
     * @return the string of the week display in [Start of week - End of week] in local date format.
     */
    public static String localToWeekDisplay(LocalDate date) {
        LocalDate previousSunday = date.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
        LocalDate nextMonday = date.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(rb.getString("datePattern"));
        return dtf.format(previousSunday) + " - " + dtf.format(nextMonday);
    }

    /**
     * localToMonthDisplay() formats a display the month of the given date.
     *
     * @param date the date to base the display off of.
     * @return the string of the month to display.
     */
    public static String localToMonthDisplay(LocalDate date) {
        String currMonth = date.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
        return currMonth.substring(0,1).toUpperCase() + currMonth.substring(1).toLowerCase() + " " + date.getYear();
    }

    /**
     * UTCTimestampToDateTimeDisplay() formats UTC timestamp to a display of the date and time together.
     *
     * @param timestamp the timestamp in UTC to format for the display.
     * @return the formatted date as a string.
     */
    public static String UTCTimestampToDateTimeDisplay(Timestamp timestamp) {
        Date fromUTC = new Date(timestamp.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat(dtPattern);
        sdf.setTimeZone(TimeZone.getTimeZone(localZoneId));
        return sdf.format(fromUTC);
    }

    /**
     * UTCTimestampToDateDisplay() formats UTC timestamp to a display of the date only.
     *
     * @param timestamp the timestamp in UTC to format for the display.
     * @return the formatted date as a string.
     */
    public static String UTCTimestampToDateDisplay(Timestamp timestamp) {
        Date fromUTC = new Date(timestamp.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat(rb.getString("datePattern"));
        sdf.setTimeZone(TimeZone.getTimeZone(localZoneId));
        return sdf.format(fromUTC);
    }

    /**
     * UTCTimestampToTimeDisplay() formats UTC timestamp to a display of the time only.
     *
     * @param timestamp the timestamp in UTC to format for the display.
     * @return the formatted date as a string.
     */
    public static String UTCTimestampToTimeDisplay(Timestamp timestamp) {
        Date fromUTC = new Date(timestamp.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.TIME_PATTERN);
        sdf.setTimeZone(TimeZone.getTimeZone(localZoneId));
        return sdf.format(fromUTC);
    }

    /**
     * localToUTCTimestamp() converts a given time from the local system's timezone from a ZonedDateTime
     * to a timestamp in UTC.
     *
     * @param local the ZonedDateTime in the local system's timezone.
     * @return the UTC timestamp of the given time.
     */
    public static Timestamp localToUTCTimestamp(ZonedDateTime local) {
        try {
            ZonedDateTime utcTime = local.withZoneSameInstant(Constants.UNIVERSAL_ZONE_ID);
            return Timestamp.valueOf(utcTime.toLocalDateTime());
        } catch (Exception e) {
            Common.handleException(e);
            return null;
        }
    }

    /**
     * localToBusinessTimeZone() converts a given time from the local system's timezone from a ZonedDateTime
     * to the specified business timezone in Constants.
     *
     * @param local the ZonedDateTime in the local system's timezone.
     * @return the ZonedDateTime in the specified business timezone of the given time.
     */
    public static ZonedDateTime localToBusinessTimeZone(ZonedDateTime local) {
        return local.withZoneSameInstant(Constants.BUSINESS_ZONE_ID);
    }

    /**
     * displayToZoned() converts the display strings for a date and time into a ZonedDateTime with the local
     * system's timezone.
     *
     * @param displayDate the string for a given date.
     * @param displayTime the string for a given time.
     * @return the ZonedDateTime in the local system's timezone parsed from the given concatenated strings.
     */
    public static ZonedDateTime displayToZoned(String displayDate, String displayTime) {
        DateTimeFormatter dtf = new DateTimeFormatterBuilder()
                .appendPattern(dtPattern)
                .appendLiteral(' ')
                .appendPattern(Constants.ZONE_PATTERN)
                .toFormatter();
        String dateTimeStr = displayDate + " " + displayTime + " " + localZoneId;
        try {
            return ZonedDateTime.parse(dateTimeStr, dtf);
        } catch (Exception e) {
            Common.handleException(e);
            return null;
        }
    }
}
