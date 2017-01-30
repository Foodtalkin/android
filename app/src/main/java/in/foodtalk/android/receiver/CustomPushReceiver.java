package in.foodtalk.android.receiver;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.parse.ParseAnalytics;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.PushService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import in.foodtalk.android.Home;
import in.foodtalk.android.ResultNotification;
import in.foodtalk.android.app.AppController;
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
       // PushService.startServiceIfRequired(context);
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);
        String screenName = "_";
        if (intent == null)
            return;
        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            Log.e(TAG, "Push received: " + json);
            screenName = json.getString("class");

           // parseIntent = intent;
           // Intent resultIntent = new Intent(context, ResultNotification.class);
            //showNotificationMessage(context, json.getString("alert"), "", resultIntent);
            //showNotificationMessage(context, "Title set android", "Description", resultIntent);
            //parsePushJson(context, json);
        } catch (JSONException e) {
            Log.e(TAG, "Push message json exception: " + e.getMessage());
        }
        AppController.getInstance().trackEvent("Notification", "Receive", screenName);
    }

    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        super.onPushDismiss(context, intent);
        String jsonData = intent.getExtras().getString("com.parse.Data");
        JSONObject jsonObject = null;
        String screenName = "_";
        try {
            jsonObject = new JSONObject(jsonData);
            screenName = jsonObject.getString("class");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        AppController.getInstance().trackEvent("Notification","Dismiss", screenName);
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
        String screenName = "_";
        //isRunning(context);
        Log.d(TAG, "onPushOpen: isHomeActivity: "+ AppController.getInstance().isHomeActivity);
        if (AppController.getInstance().isHomeActivity){
            String jsonData = intent.getExtras().getString("com.parse.Data");
            try {
                JSONObject jsonObject = new JSONObject(jsonData);
                screenName = jsonObject.getString("class");
                final String elementId = jsonObject.getString("elementId");
                Log.d(TAG+" onPushOpen",screenName+" "+elementId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sendMessage(context, jsonData);
        }else {
            try {
                Log.d(TAG,"onPushOpen");
                //super.onPushOpen(context, intent);
                ParseAnalytics.trackAppOpenedInBackground(intent);
                //PushService.setDefaultPushCallback(context, Home.class);
                ParseAnalytics.trackAppOpenedInBackground(intent);
                Intent i = new Intent(context, Home.class);
                i.putExtras(intent.getExtras());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            } catch (Exception e) {
                Log.d("Tag parse", "onPushOpen Error : " + e);
            }
        }
        AppController.getInstance().trackEvent("Notification", "Open", screenName);
    }
    private void sendMessage(Context context, String jsonData) {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("custom-event-name");
        // You can also include some extra data.
        intent.putExtra("message", jsonData);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
    public boolean isRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                return true;
        }
        return false;
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
