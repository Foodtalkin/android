package in.foodtalk.android.module;


import android.content.Context;

import in.foodtalk.android.R;

/**
 * Created by RetailAdmin on 27-02-2017.
 */

public class GetRange {
    public static String getRangePrice(Context context, String price){

        int price1 = Integer.valueOf(price);

        String rs = context.getResources().getString(R.string.rs);

        if (price1 < 500){

            return rs+" Budget";
        }else if (price1 >= 500 && price1 < 1000){

            return rs+" Mid Range";
        } else {
            return rs+" Splurge";
        }
    }
}
