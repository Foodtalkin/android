package in.foodtalk.android;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import in.foodtalk.android.module.DatabaseHandler;

public class SplashScreen extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityBackgroundColor(R.color.bg_color);
        setContentView(R.layout.splash);

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
}
