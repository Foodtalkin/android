package in.foodtalk.android.module;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by RetailAdmin on 19-04-2016.
 */
public class TextValidation {

    public Boolean userNameValidation(String value){
        Pattern ps = Pattern.compile("^[a-zA-Z ]+$");
        Matcher ms = ps.matcher(value);
        boolean bs = ms.matches();
        if (bs == false) {
           // if (ErrorMessage.contains("invalid"))
               // ErrorMessage = ErrorMessage + "state,";
           // else
               // ErrorMessage = ErrorMessage + "invalid state,";

        }
        return false;
    }
}
