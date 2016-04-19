package in.foodtalk.android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import in.foodtalk.android.module.DatabaseHandler;

public class Home extends AppCompatActivity implements View.OnClickListener {

    DatabaseHandler db;
    Button btnGetInfo, btnDelRow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHandler(getApplicationContext());
        setContentView(R.layout.activity_home);

        btnGetInfo = (Button) findViewById(R.id.btn_getInfo);
        btnDelRow = (Button) findViewById(R.id.btn_delRow);

        btnGetInfo.setOnClickListener(this);
        btnDelRow.setOnClickListener(this);


    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_getInfo:
                // OK button
               // Log.d("faceboo: ", "click getInfo button");
                Log.d("getInfo",db.getRowCount()+"");
                Log.d("get user info", db.getUserDetails().get("userName")+"");
                break;
            case R.id.btn_delRow:
                // OK button
                db.resetTables();
                Log.d("faceboo: ", "click delRow button");
                break;
        }
    }
}