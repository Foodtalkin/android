package in.foodtalk.android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class ResultNotification extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_notification);

        //Get the bundle
        Bundle bundle = getIntent().getExtras();

        //Extract the data…
        String stuff = bundle.getString("");


        JSONObject json = null;
        try {
            json = new JSONObject(getIntent().getExtras().getString("com.parse.Data"));
            Log.e("getintent extra", "Push received: " + json);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
