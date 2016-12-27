package in.foodtalk.android.module;

import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by RetailAdmin on 27-12-2016.
 */

public class SetDateFormat {
    public static String convertFormat(String myDate, String currentFormat, String setFormat){
       // SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
       // SimpleDateFormat DesiredFormat = new SimpleDateFormat("MM/dd/yyyy HH:MM:SS a");
        // 'a' for AM/PM

         SimpleDateFormat sourceFormat = new SimpleDateFormat(currentFormat);
         SimpleDateFormat DesiredFormat = new SimpleDateFormat(setFormat);


        Date date = null;
        try {

            date = sourceFormat.parse("2012/12/31 03:20:20");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedDate = DesiredFormat.format(date.getTime());
// Now formattedDate have current date/time
        //Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();
        return formattedDate;
    }
}
