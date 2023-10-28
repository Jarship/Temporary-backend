package com.temporary.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Day implements Comparable<Day> {
    /**
     * This is a class to represent a Day, without a timestamp, as unambiguously as possible. To do this, we store the
     * date's components, year, month, and day, as three integers, without converting them to or from a Date object.
     *
     * The recommended way to use this class is to construct the Day object using a string ("2020-11-14") and save it
     * using its toString() value, which returns a string in the same format. When used this way, the timezone is irrelevant.
     *
     * If you need to know the instant that the day starts and ends, the startDate and endDate properties are provided,
     * but these properties depend on setting the timezone correctly. The DEFAULT_TIMEZONE is used when a timezone isn't
     * otherwise specified. The Date properties are helpful when you want to know if a given Date falls on this Day in
     * a particular timezone, even though it may fall on a different day in another timezone.
     */

    private static final String DEFAULT_TIMEZONE = "America/Chicago";

    // The purpose of the TimeZone and Calendar is to convert the day into two timestamps indicating the
    // instants that the day starts and ends in the given timezone. If the startTime and endTime properties
    // are not used, then the TimeZone doesn't matter as it will not affect the year, month, and day values.
    @JsonIgnore
    private transient TimeZone timeZone;
    @JsonIgnore
    private transient Calendar c;

    // These values are timezone-independent as long as they are stored and retrieved without converting to
    // a Date object. The recommended approach is to store the toString() value and use the (String) constructor.
    private int month;
    private int day;
    private int year;

    // These properties convert the day to two instants of time, giving you the beginning and end of the day in
    // the timezone used to construct the object (or the default timezone). Be careful when using these values since
    // the month-day-year properties of these dates may differ. For example if the day is set to November 14 and the
    // timezone is America/Chicago, the startTime and endTime will have these values:
    // Sat Nov 14 06:00:00 UTC 2020 (1605333600000)
    // Sun Nov 15 05:59:59 UTC 2020 (1605419999999)
    // The typical purpose of accessing these properties is to determine if a given Date object falls within this day.
    // For example, the time Sun Nov 15 03:00:00 UTC 2020 falls on November 14th if the timezone is US Central time.
    @JsonIgnore
    private transient Date startTime;
    @JsonIgnore
    private transient Date endTime;

    public static TimeZone getDefaultTimeZone() {
        return TimeZone.getTimeZone(DEFAULT_TIMEZONE);
    }

    public static Calendar getDefaultCalendar() {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(Day.getDefaultTimeZone());
        return c;
    }

    // Constructs today, in the default timezone
    // Note that the default timezone is given by DEFAULT_TIMEZONE and may NOT be UTC
    public Day() {
        this(System.currentTimeMillis());
    }

    // Converts a given timestamp into a Day in the default timezone
    public Day(long timeMillis) {
        this(TimeZone.getTimeZone(DEFAULT_TIMEZONE), timeMillis);
    }

    // Converts a given timestamp into a Day in the default timezone
    public Day(Date date) {
        this(TimeZone.getTimeZone(DEFAULT_TIMEZONE), date.getTime());
    }

    // Copy constructor
    public Day(Day day) {
        this(day.getTimeZone(), day.getYear(), day.getMonth(), day.getDay());
    }

    // Construct a Day from a timestamp in the given timezone
    public Day(TimeZone tz, long timeMillis) {
        this.timeZone = tz;
        this.c = Calendar.getInstance();
        c.setTimeZone(tz);
        c.setTimeInMillis(timeMillis);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        this.month = c.get(Calendar.MONTH) + 1; // Add 1 because Calendar.MONTH is zero-based
        this.day = c.get(Calendar.DAY_OF_MONTH);
        this.year = c.get(Calendar.YEAR);

        this.startTime = c.getTime();
        c.add(Calendar.DAY_OF_YEAR, 1);
        c.add(Calendar.MILLISECOND, -1);
        this.endTime = c.getTime();
    }


    // Constructs a Day from year, month and day values. The default timezone is used to create the
    // startDate and endDate properties. However if those properties are not used, then the timezone doesn't matter.
    public Day(int year, int month, int day) {
        this(TimeZone.getTimeZone(DEFAULT_TIMEZONE), year, month, day);
    }

    // Constructs a Day from the given values using the given timezone. Note that the timezone does NOT affect the
    // values of month, day, or year, which are stored explicitly; it only affects the values of the startTime and
    // endTime properties. If those properties are not used, then it doesn't matter what TimeZone is passed.
    public Day(TimeZone tz, int year, int month, int day) {
        initWith(tz, year, month, day);
    }

    private void initWith(TimeZone tz, int year, int month, int day) {
        this.timeZone = tz;
        this.year = year;
        this.month = month;
        this.day = day;

        this.c = Calendar.getInstance();
        c.setTimeZone(tz);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);   // Subtract 1 because Calendar.MONTH is zero-based
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        this.startTime = c.getTime();
        c.add(Calendar.DAY_OF_YEAR, 1);
        c.add(Calendar.MILLISECOND, -1);
        this.endTime = c.getTime();
    }

    private void initIncomplete() {
        if (timeZone == null) timeZone = Day.getDefaultTimeZone();
        if (c == null) {
            c = Calendar.getInstance();
            c.setTimeZone(timeZone);
        }

        if (this.startTime == null) {
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month - 1);   // Subtract 1 because Calendar.MONTH is zero-based
            c.set(Calendar.DAY_OF_MONTH, day);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            this.startTime = c.getTime();
            c.add(Calendar.DAY_OF_YEAR, 1);
            c.add(Calendar.MILLISECOND, -1);
            this.endTime = c.getTime();
        }
    }


    // Constructs a Day from a string, in the following format: yyyy-mm-dd OR mm/dd/yyyy.
    // (This is the same as what is returned by the toString method).
    // The default timezone is used. The same comments apply here as for the constructor using ints.
    public Day(String ymd) {
        this(TimeZone.getTimeZone(DEFAULT_TIMEZONE), ymd);
    }

    // Constructs a Day from a string, in the following format: yyyy-mm-dd OR mm/dd/yyyy.
    // (This is the same as what is returned by the toString method).
    // The given timezone is used. The same comments apply here as for the constructor using ints.
    public Day(TimeZone tz, String ymd) {
        int i = 0;
        int j = 0;
        int[] values = new int[3];
        StringBuilder st = new StringBuilder();
        while (i < values.length && j < ymd.length()) {
            char c = ymd.charAt(j++);
            if (Character.isDigit(c)) {
                st.append(c);
            }
            if (!Character.isDigit(c) || j >= ymd.length()) {
                if (st.length() > 0) {
                    try {
                        int x = Integer.parseInt(st.toString());
                        values[i++] = x;
                    }
                    catch (Exception ignored) {}
                    finally {
                        st.setLength(0);
                    }
                }
            }
        }

        int year, month, day;
        if (values[0] > 100) {
            // yyyy-mm-dd
            year = values[0];
            month = values[1];
            day = values[2];
        }
        else {
            // mm/dd/yyyy or mm/dd/yy
            month = values[0];
            day = values[1];
            year = values[2];
            if (year < 100) year += 2000;
        }

        initWith(tz, year, month, day);
    }


    // Returns yyyy-mm-dd. This string can be used to reconstruct a Day using one of the above constructors.
    @Override
    public String toString() {
        return String.format("%4d-%02d-%02d", year, month, day);
    }

    public String getDate() {
        return this.toString();
    }

    public String getUSDate() {
        return String.format("%02d/%02d/%4d", month, day, year);
    }

    @JsonIgnore
    public Calendar getCalendar() {
        initIncomplete();
        c.setTime(startTime);
        return c;
    }

    @JsonIgnore
    public TimeZone getTimeZone() {
        initIncomplete();
        return timeZone;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getYear() {
        return year;
    }

    public void setTimeZone(TimeZone timeZone)
    {
        initWith(timeZone, year, month, day);
    }

    public void setMonth(int month)
    {
        initWith(timeZone, year, month, day);
    }

    public void setDay(int day)
    {
        initWith(timeZone, year, month, day);
    }

    public void setYear(int year)
    {
        initWith(timeZone, year, month, day);
    }

    // The instant representing the start of this day in the timezone that was used to construct it.
    public Date getStartTime() {
        initIncomplete();
        return startTime;
    }

    // The instant representing the end of this day in the timezone that was used to construct it.
    public Date getEndTime() {
        initIncomplete();
        return endTime;
    }

    @JsonIgnore
    public LocalDate getLocalDate() {
        return LocalDate.of(year, month, day);
    }

    @JsonIgnore
    public java.sql.Date getSqlDate() {
        return java.sql.Date.valueOf(getLocalDate());
    }

    public Day addDays(int days) {
        Calendar calendar = getCalendar();
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return new Day(timeZone, calendar.getTimeInMillis());
    }

    public Day addMonths(int months) {
        Calendar calendar = getCalendar();
        calendar.add(Calendar.MONTH, months);
        return new Day(timeZone, calendar.getTimeInMillis());
    }

    public Day addYears(int years) {
        Calendar calendar = getCalendar();
        calendar.add(Calendar.YEAR, years);
        return new Day(timeZone, calendar.getTimeInMillis());
    }

    // Returns a positive number if this Day is earlier than anotherDay
    // Returns a negative number if anotherDay is earlier than this Day
    public int getDifferenceInDays(Day anotherDay) {
        LocalDate first = getLocalDate();
        LocalDate last = anotherDay.getLocalDate();
        return (int) ChronoUnit.DAYS.between(first, last);
    }

    @Override
    public int hashCode() {
        return (year * 400) + (month * 40) + day;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Day) {
            Day other = (Day) obj;
            return year == other.year && month == other.month && day == other.day && timeZone.toZoneId().getId().equals(other.timeZone.toZoneId().getId());
        }
        else return false;
    }

    // Compares this object with the specified object for order. Returns a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
    @Override
    public int compareTo(Day o)
    {
        return Integer.compare(this.hashCode(), o.hashCode());
    }

}