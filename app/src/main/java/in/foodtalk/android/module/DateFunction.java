package in.foodtalk.android.module;

import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
}
