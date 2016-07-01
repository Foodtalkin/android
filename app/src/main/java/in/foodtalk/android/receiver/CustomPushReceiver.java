package in.foodtalk.android.receiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParseAnalytics;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.PushService;

import org.json.JSONException;
import org.json.JSONObject;

import in.foodtalk.android.Home;
import in.foodtalk.android.ResultNotification;
import in.foodtalk.android.helper.NotificationUtils;

/**
 * Created by RetailAdmin on 29-06-2016.
 */
public class CustomPushReceiver extends ParsePushBroadcastReceiver {

    private final String TAG = CustomPushReceiver.class.getSimpleName();
    private NotificationUtils notificationUtils;

    private Intent parseIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        PushService.startServiceIfRequired(context);
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);


        if (intent == null)
            return;

        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

            Log.e(TAG, "Push received: " + json);

           // parseIntent = intent;
           // Intent resultIntent = new Intent(context, ResultNotification.class);
            //showNotificationMessage(context, json.getString("alert"), "", resultIntent);
            //showNotificationMessage(context, "Title set android", "Description", resultIntent);

            //parsePushJson(context, json);

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
        try {
            //super.onPushOpen(context, intent);
            ParseAnalytics.trackAppOpenedInBackground(intent);
            PushService.setDefaultPushCallback(context, ResultNotification.class);
            ParseAnalytics.trackAppOpenedInBackground(intent);
            Intent i = new Intent(context, ResultNotification.class);
            i.putExtras(intent.getExtras());
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } catch (Exception e) {
            Log.d("Tag parse", "onPushOpen Error : " + e);
        }
    }

    /**
     * Parses the push notification json
     *
     * @param context
     * @param json
     */
    private void parsePushJson(Context context, JSONObject json) {
        try {
            boolean isBackground = json.getBoolean("is_background");
            JSONObject data = json.getJSONObject("data");
            String title = data.getString("title");
            String message = data.getString("message");

            if (!isBackground) {
                Intent resultIntent = new Intent(context, ResultNotification.class);
                showNotificationMessage(context, title, message, resultIntent);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Push message json exception: " + e.getMessage());
        }
    }

    /**
     * Shows the notification message in the notification bar
     * If the app is in background, launches the app
     *
     * @param context
     * @param title
     * @param message
     * @param intent
     */
    private void showNotificationMessage(Context context, String title, String message, Intent intent) {

        notificationUtils = new NotificationUtils(context);

       // intent.putExtras(parseIntent.getExtras());

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        notificationUtils.showNotificationMessage(title, message, intent);
    }
}
