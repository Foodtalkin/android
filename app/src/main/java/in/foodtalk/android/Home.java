package in.foodtalk.android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import in.foodtalk.android.module.DatabaseHandler;

public class Home extends AppCompatActivity{

    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHandler(getApplicationContext());
        setContentView(R.layout.activity_home);



       // Log.d("getInfo",db.getRowCount()+"");
       // Log.d("get user info", db.getUserDetails().get("userName")+"");
    }

}