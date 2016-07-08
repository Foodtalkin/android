package in.foodtalk.android.module;

import java.util.Date;

/**
 * Created by RetailAdmin on 08-07-2016.
 */
public class DateTimeDifference {

    public String difference(Date startDate, Date endDate){
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long weeksInMilli = daysInMilli * 7;
        long monthsInMilli = daysInMilli * 30;
        long yearsInMilli = monthsInMilli * 12;


        long elapsedYear = different / yearsInMilli;
        different = different % yearsInMilli;

        long elapsedMonths = different / monthsInMilli;
        //different = different % monthsInMilli;

        long elapsedWeeks = different / weeksInMilli;
        different = different % weeksInMilli;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d years, %d months, %d weeks, %d days, %d hours, %d minutes, %d seconds%n",
                elapsedYear, elapsedMonths, elapsedWeeks, elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds);


        if (elapsedYear > 0){
            return Long.toString(elapsedYear)+"y";
        }else if (elapsedWeeks > 0){
            return Long.toString(elapsedWeeks)+"w";
        }else if (elapsedDays > 0 ){
            return Long.toString(elapsedDays)+"d";
        }else if (elapsedHours > 0){
            return Long.toString(elapsedHours)+"h";
        }else if (elapsedMinutes > 0){
            return Long.toString(elapsedMinutes)+"m";
        }else if (elapsedSeconds > 0){
            return Long.toString(elapsedSeconds)+"s";
        }
        return null;
    }
}
