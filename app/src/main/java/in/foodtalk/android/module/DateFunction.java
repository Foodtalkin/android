package in.foodtalk.android.module;

import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by RetailAdmin on 27-12-2016.
 */

public class DateFunction {
    public static String convertFormat(String myDate, String currentFormat, String setFormat){
       // SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
       // SimpleDateFormat DesiredFormat = new SimpleDateFormat("MM/dd/yyyy HH:MM:SS a");
        // 'a' for AM/PM

         SimpleDateFormat sourceFormat = new SimpleDateFormat(currentFormat);
         SimpleDateFormat DesiredFormat = new SimpleDateFormat(setFormat);


        Date date = null;
        try {

            date = sourceFormat.parse(myDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedDate = DesiredFormat.format(date.getTime());
// Now formattedDate have current date/time
        //Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();
        return formattedDate;
    }

    public static int compareToCurrentDate(String dateFormat, String date1){
        String dtStart = "2017-01-04 13:33:59";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(date1);
            //Date date = format.parse(dtStart);
            Date todayDate = new Date();
            //Log.d("compair date", compareToDay(date, todayDate)+"");
            return compareToDay(date, todayDate);

            //System.out.println(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return -5;
        }
    }


    public static int compareToDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return 0;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return sdf.format(date1).compareTo(sdf.format(date2));
    }

    public static String timeDiffCurrent(Date startDate){
        Date currentDate = new Date();
        return timeDiff(startDate, currentDate);
    }

    public static String timeDiff(Date startDate, Date endDate){
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
