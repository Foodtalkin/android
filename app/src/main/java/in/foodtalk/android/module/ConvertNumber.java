package in.foodtalk.android.module;

/**
 * Created by RetailAdmin on 01-02-2017.
 */

public class ConvertNumber {
    public static String withSuffix(long count) {
        if (count < 10000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));


//        return String.format("%.1f %c",
//                count / Math.pow(1000, exp),
//                "kMGTPE".charAt(exp-1));

        return String.format("%.0f%c",
                Math.floor(count / Math.pow(1000, exp)),
                "kMGTPE".charAt(exp-1));
    }
}
