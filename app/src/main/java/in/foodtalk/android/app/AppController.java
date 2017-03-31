package in.foodtalk.android.app;


import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.Parse;
import com.parse.ParseUser;
import com.parse.PushService;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import in.foodtalk.android.Home;
import in.foodtalk.android.R;
import in.foodtalk.android.communicator.OnBoardingCallback;
import in.foodtalk.android.helper.AnalyticsExceptionParser;
import in.foodtalk.android.helper.AnalyticsTrackers;
import in.foodtalk.android.helper.ParseUtils;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.LruBitmapCache;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AppController extends Application {

    public static final String TAG = AppController.class
            .getSimpleName();

    public static String fbEmailId;
    public static String userName;
    public static String fullName;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static AppController mInstance;

    public Boolean isHomeActivity = false;

    public String sessionId;

    public Context context;

    public RecyclerView recyclerView;

    private ParseUtils parseUtils;

    public String deviceToken;

    public String versionName;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    @Override
    public void onCreate() {
        super.onCreate();

        //removeDataForParse();
        checkfirstTime();

        //--configure analytics
        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
        //---



        // configure and init Flurry
        new FlurryAgent.Builder()
                .withLogEnabled(true)
                .withLogLevel(Log.INFO)
                .withContinueSessionMillis(5000L)
                .withCaptureUncaughtExceptions(true)
                .withPulseEnabled(true)
                .build(this, getString(R.string.flurryKey));

        /*Parse.initialize(this, getString(R.string.parseAppID), getString(R.string.parseClientID));
        ParseUser.enableAutomaticUser();
        PushService.setDefaultPushCallback(this, Home.class);*/

        try {
            PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = "Android-"+pinfo.versionName;
            Log.d("virson name", versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        parseUtils.registerParse(this);

        context = getApplicationContext();
        mInstance = this;
        FacebookSdk.sdkInitialize(getApplicationContext());
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "in.foodtalk.foodtalk",  // replace with your unique package name
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        ExceptionReporter reporter = new ExceptionReporter(
                AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP),
                Thread.getDefaultUncaughtExceptionHandler(), this);
        reporter.setExceptionParser(new AnalyticsExceptionParser(this, null));
        Thread.setDefaultUncaughtExceptionHandler(reporter);
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        Log.i("AppController","requestQueue: "+tag);
        sendEventByTag(tag);

        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    private void sendEventByTag(String tag){
        if (tag.equals("postlike")){
            trackEvent("Post", "like", "Like Post");
            Log.d("trackEvent","like");
        }else if (tag.equals("postbookmark")){
            trackEvent("Post", "bookmark", "bookmark Post");
            Log.d("trackEvent","bookmark");
        }
        else if (tag.equals("userUnfollow")){
            trackEvent("user", "unfollow", "User unfollow");
            Log.d("trackEvent","userUnfollow");
        }
        else if (tag.equals("userFollow")){
            trackEvent("user", "follow", "User follow");
            Log.d("trackEvent","userFollow");
        }else if (tag.equals("sendComment")){
            trackEvent("comment", "add", "Create Comment");
            Log.d("trackEvent","Comment Send");
        }else if (tag.equals("reportComment")){
            trackEvent("comment", "report", "Report Comment");
        }else if (tag.equals("deleteComment")){
            trackEvent("comment", "delete", "Delete Comment");
        }else if (tag.equals("uploadDish")){
            trackEvent("Post", "add", "Create Post");
        }else if (tag.equals("postQuestion")){
            trackEvent("Post", "add", "Ask a Question");
        }else if (tag.equals("postReport")){
            trackEvent("Post", "report", "report Post");
        }else if (tag.equals("storeItemBuy")){
            trackEvent("store", "purchase", "store Item Purchase");
        }
    }
    //----------------google analytics functions------------------
    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }
    /***
     * Tracking screen view
     *
     * @param screenName screen name to be displayed on GA dashboard
     */
    public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();

        Log.d("AppController","trackScreenView: "+ screenName);

        // Set screen name.
        t.setScreenName(screenName);
        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());
        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }

    /***
     * Tracking exception
     *
     * @param e exception to be tracked
     */
    public void trackException(Exception e) {
        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();

            t.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(
                            new StandardExceptionParser(this, null)
                                    .getDescription(Thread.currentThread().getName(), e))
                    .setFatal(false)
                    .build()
            );
        }
    }

    /***
     * Tracking event
     *
     * @param category event category
     * @param action   action of the event
     * @param label    label
     */
    public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }

    //-------

    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if(appDir.exists()){
            String[] children = appDir.list();
            for(String s : children){
                if(!s.equals("lib")){
                    deleteDir(new File(appDir, s));
                    Log.i("TAG", "File /data/data/APP_PACKAGE/" + s +" DELETED");
                }
            }
        }
    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
    private void checkfirstTime(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.getBoolean("first_time", false))
        {
            Log.d("AppController","start first time");
            clearApplicationData();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("first_time", true);
            editor.commit();
        }
        else
        {
            Log.d("AppController","not first time");
        }
    }
}
