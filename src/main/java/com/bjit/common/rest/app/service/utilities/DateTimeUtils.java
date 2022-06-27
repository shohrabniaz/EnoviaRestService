package com.bjit.common.rest.app.service.utilities;

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class DateTimeUtils {

    public synchronized static Date getTime(Date time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
        String timeInSimpleDateFormat = simpleDateFormat.format(time);
        try {
            return simpleDateFormat.parse(timeInSimpleDateFormat);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("Time couldn't be parsed into simple date format");
    }

    public synchronized static String elapsedTime(Date executionStartTime, Date executionEndTime, String writeElapsedTimeToAFile, String message) {
        try {
            String elapsedTime = printDifference(getTime(executionStartTime), getTime(executionEndTime), writeElapsedTimeToAFile, message);
            return elapsedTime;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    //1 minute = 60 seconds
    //1 hour = 60 x 60 = 3600
    //1 day = 3600 x 24 = 86400
    public synchronized static String printDifference(Date startDate, Date endDate, String writeElapsedTimeToAFile, String message) {

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

//        System.out.println("startDate : " + startDate);
//        System.out.println("endDate : " + endDate);
//        System.out.println("different : " + different);
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        //String time = "%d days, %d hours, %d minutes, %d seconds%n", elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds;
        System.out.printf("%d days, %d hours, %d minutes, %d seconds%n", elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
        //writeToAFile(writeElapsedTimeToAFile, elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds, message);
        String elapsedTime = "Days : " + elapsedDays + " Hours : " + elapsedHours + " Minutes : " + elapsedMinutes + " Seconds : " + elapsedSeconds;
        return elapsedTime;
    }

    private synchronized static void writeToAFile(String writeElapsedTimeToAFile, long elapsedDays, long elapsedHours, long elapsedMinutes, long elapsedSeconds, String message) {
        try (FileWriter fileWriter = new FileWriter(writeElapsedTimeToAFile, true)) {

            fileWriter.write("\n\n\n");
            fileWriter.write("----------------------- ||| Time Taken To Complete The Cycle ||| -----------------------\n");
            fileWriter.write("----------------------- ||| --- " + message + " --- ||| -----------------------\n");
            fileWriter.write("Days             : " + elapsedDays + "\n");
            fileWriter.write("Hours            : " + elapsedHours + "\n");
            fileWriter.write("Minutes          : " + elapsedMinutes + "\n");
            fileWriter.write("Seconds          : " + elapsedSeconds + "\n");
            fileWriter.write("----------------------- ||| Time Taken To Complete The Cycle ||| -----------------------\n\n");
        } catch (IOException exp) {
            System.err.println("IOException: " + exp.getMessage());
        }
    }

    public synchronized static long getDuration(Instant start, Instant end) {
//        Instant start = Instant.now();
//        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        return timeElapsed.toMillis();
    }
}
