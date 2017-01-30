package in.foodtalk.android.helper;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;
import com.parse.interceptors.ParseStethoInterceptor;

import in.foodtalk.android.Home;
import in.foodtalk.android.R;
import in.foodtalk.android.app.AppController;

/**
 * Created by RetailAdmin on 30-06-2016.
 */
public class ParseUtils {
    private static String TAG = ParseUtils.class.getSimpleName();


    public static void registerParse(Context context) {






        // initializing parse library
        //Parse.initialize(context, context.getString(R.string.parseAppID), context.getString(R.string.parseClientID));
        Parse.initialize(new Parse.Configuration.Builder(context)
                .applicationId(context.getString(R.string.parseAppID))
                //.clientKey(null)
                .server("http://52.74.136.146:1337/parse/")
                //.server("http://api.parse.com/parse/")
               // .addNetworkInterceptor(new ParseStethoInterceptor())
                //.server("http://192.168.1.5:1337/parse/")
                .build()

        );
        Log.d("ParseUtils","Parse initialize");

        ParseUser.enableAutomaticUser();

        Parse.setLogLevel(Parse.LOG_LEVEL_ERROR);

//        ParseInstallation.getCurrentInstallation().deleteInBackground(new DeleteCallback() {
//            @Override
//            public void done(ParseException e) {
//                Log.d("deleteInB","done");
//            }
//        });

        //clearParseInstallation();



        //PushService.setDefaultPushCallback(context, Home.class);
        ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.e(TAG, "Successfully subscribed to Parse!");
                String deviceToken = (String) ParseInstallation.getCurrentInstallation().get("deviceToken");
                Log.d("ParseUtils","deviceToken: "+deviceToken);
                if (e != null){
                    Log.e("ParseException", e.toString()+" done");
                }

            }
        });

        /*ParsePush.subscribeInBackground(AppConfig.PARSE_CHANNEL, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.e(TAG, "Successfully subscribed to Parse!");
            }
        });*/
    }
    public static void subscribeWithInfo(String userId,String locationIdentifire, String work,
                                         String cityId, String stateId, String countryId, String regionId) {

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
            AppController appController = new AppController();
            @Override
            public void done(ParseException e) {
                String deviceToken = (String) ParseInstallation.getCurrentInstallation().get("deviceToken");
                Log.e("deviceToken callback", deviceToken+" ");

                if (e!= null){
                    Log.e("ParseException", e.toString()+" done");
                }
                appController.deviceToken = deviceToken;
            }
        });

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("userId", userId);
        installation.put("work", work);
        //installation.put("region",region);



        installation.put("cityId", cityId);
        installation.put("stateId", stateId);
        installation.put("countryId", countryId);
        installation.put("regionId", regionId);

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

    public static void clearParseInstallation() {

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();

        try {
            if (installation != null){
                installation.delete();
            }else {
                Log.e("ParseUtils","installation is null");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}
