package in.foodtalk.android;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.FacebookSdk;
import com.facebook.applinks.AppLinkData;

import java.util.Arrays;
import java.util.List;

import in.foodtalk.android.module.DatabaseHandler;

public class SplashScreen extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1500;

    private DatabaseHandler db;

    private String fragmentName = null;
    private String elementId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityBackgroundColor(R.color.bg_color);
        setContentView(R.layout.splash);

        deepLinkfb();



        db = new DatabaseHandler(getApplicationContext());

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                int count  = db.getRowCount();
                 //int count  = 0;
                Log.d("from splash check login",count+"");
                Intent i;

                if(count > 0){
                    if(db.getUserDetails().get("userName").equals("N/A")){
                        i = new Intent(SplashScreen.this, WelcomeUsername.class);
                    }
                    else {

                        i = new Intent(SplashScreen.this, Home.class);
                        if (fragmentName != null){
                            i.putExtra("FRAGMENT_NAME",fragmentName);
                        }
                        if (elementId != null){
                            i.putExtra("ELEMENT_ID",elementId);
                        }

                    }
                    Log.d("user info from Splash", db.getUserDetails().get("userName")+"");
                }else {
                    i = new Intent(SplashScreen.this, FbLogin.class);
                }
                startActivity(i);
                // close this activity
                //Intent intent = new Intent(SplashScreen.this,Test.class);
                //startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
    public void setActivityBackgroundColor(int color) {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(color);
    }
    private void deepLinkfb(){
        Log.d("SplashScreen", "deepLinkfb");
        FacebookSdk.sdkInitialize(this);
        AppLinkData.fetchDeferredAppLinkData(this,
                new AppLinkData.CompletionHandler() {
                    @Override
                    public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                        if (appLinkData != null) {
                            Bundle bundle = appLinkData.getArgumentBundle();
                            Log.i("DEBUG_FACEBOOK_SDK- S", bundle.getString("target_url"));
                            String url = bundle.getString("target_url");
                            List<String> items = Arrays.asList(url.split("\\s*/\\s*"));

                            if (items.size() > 3){
                                Log.e("DebugFb urlvalue- S", items.get(2));
                                Log.e("DebugFb urlvalue- S", items.get(3));
                                fragmentName = items.get(2);
                                elementId = items.get(3);
                                //openNotificationFragment(items.get(2), items.get(3));
                            }else if (items.size() > 2){
                                fragmentName = items.get(2);
                                //openNotificationFragment(items.get(2), "");
                                Log.e("DebugFb urlvalue", items.get(2));
                            }
                        } else {
                            Log.i("DEBUG_FACEBOOK_SDK", "AppLinkData is Null");
                        }
                    }
                });
    }
}
