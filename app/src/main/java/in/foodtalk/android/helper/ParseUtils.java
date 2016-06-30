package in.foodtalk.android.helper;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;

import in.foodtalk.android.Home;
import in.foodtalk.android.R;

/**
 * Created by RetailAdmin on 30-06-2016.
 */
public class ParseUtils {
    private static String TAG = ParseUtils.class.getSimpleName();
    public static void registerParse(Context context) {
        // initializing parse library
        Parse.initialize(context, context.getString(R.string.parseAppID), context.getString(R.string.parseClientID));
        ParseUser.enableAutomaticUser();

        //PushService.setDefaultPushCallback(context, Home.class);
        ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.e(TAG, "Successfully subscribed to Parse!");
            }
        });

        /*ParsePush.subscribeInBackground(AppConfig.PARSE_CHANNEL, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.e(TAG, "Successfully subscribed to Parse!");
            }
        });*/
    }
    public static void subscribeWithInfo(String userId,String locationIdentifire, String work) {

        /*if (ParseUser.getCurrentUser() == null) {
            ParseUser.enableAutomaticUser();
            Log.d("getCurrentUser","currentuser null");
        }
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();

        installation.put("userId", userId);
        installation.put("locationIdentifire", userId);
        installation.put("work",work);
        installation.put("channels",channels);
        installation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.e(TAG, "Successfully subscribed with userInfo to Parse!");
            }
        });*/

        if (ParseUser.getCurrentUser() == null) {
            ParseUser.enableAutomaticUser();
            Log.d("getCurrentUser","currentuser null");
        }
        ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                String deviceToken = (String) ParseInstallation.getCurrentInstallation().get("deviceToken");
                Log.d("deviceToken callback", deviceToken+"");
            }
        });

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("userId", userId);
        installation.put("work", work);
//        installation.put("localeIdentifier","en-IN");
       // installation.put("channels",channels);
        installation.saveInBackground();
        /*ParsePush.subscribeInBackground(channels, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.e(TAG, "Successfully subscribed to Parse!");
            }
        });*/
    }
    public void subscribeToChannels(String channel){
        ParsePush.subscribeInBackground(channel, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.e(TAG, "channel Successfully subscribed to Parse!");
            }
        });
    }
    public void unSubscribeToChannels(String channel){
        Log.d("channel","unsubscribe");
        ParsePush.unsubscribeInBackground(channel, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.e(TAG, "channel Successfully unSubscribed to Parse!");
            }
        });
    }
}
