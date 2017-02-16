package in.foodtalk.android.module;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by RetailAdmin on 16-02-2017.
 */

public class MyEditText extends EditText {
    public MyEditText(Context context) {
        super(context);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_ENTER)
        {
            // Just ignore the [Enter] key
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
