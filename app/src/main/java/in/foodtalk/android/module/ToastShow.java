package in.foodtalk.android.module;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by RetailAdmin on 17-08-2016.
 */
public class ToastShow {
    public void showToast(Context context, String msg){
        Toast toast= Toast.makeText(context,
                msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 300);
        toast.show();
    }
}
