package in.foodtalk.android.receiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by RetailAdmin on 29-06-2016.
 */
public class CustomPushReceiver extends ParsePushBroadcastReceiver {

    private final String TAG = CustomPushReceiver.class.getSimpleName();
    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);
        if (intent == null)
            return;

        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

            Log.e(TAG, "Push received: " + json);

            //parseIntent = intent;

            // parsePushJson(context, json);

        } catch (JSONException e) {
            Log.e(TAG, "Push message json exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        super.onPushDismiss(context, intent);
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
    }
}
