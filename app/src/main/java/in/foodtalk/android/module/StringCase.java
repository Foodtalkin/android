package in.foodtalk.android.module;

/**
 * Created by RetailAdmin on 09-05-2016.
 */
public class StringCase {
    public static String caseSensitive (String source){

        StringBuffer res = new StringBuffer();

        String[] strArr = source.split(" ");
        for (String str : strArr) {
            char[] stringArray = str.trim().toCharArray();
            stringArray[0] = Character.toUpperCase(stringArray[0]);
            str = new String(stringArray);
            res.append(str).append(" ");
        }
        //System.out.print("Result: " + res.toString().trim());
        return res.toString().trim();
    }
}
