package in.foodtalk.android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.foodtalk.android.app.AppController;

public class Test extends AppCompatActivity implements View.OnClickListener {

    private Button btnPostData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        btnPostData = (Button) findViewById(R.id.btn_test_post1);
        btnPostData.setOnClickListener(this);
    }

    private void makeJsonObjReq1() throws JSONException {
        //showProgressDialog();


        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("signInType", "F");
        postParam.put("fullName", "somepasswordhere");
        postParam.put("email","");
        postParam.put("facebookId","");
        postParam.put("latitude","");
        postParam.put("longitude","");
        postParam.put("deviceToken","");
        postParam.put("image","");
        postParam.put("twitterId","");
        postParam.put("googleId","");

        JSONObject obj = new JSONObject();
        obj.put("signInType", "F");
        obj.put("fullName", "Mandeep Singh");
        obj.put("email","kindheartmandeep11@yahoo.com");
        obj.put("facebookId","10209122833009000");
        obj.put("latitude","28.6753863");
        obj.put("longitude","77.180826");
        obj.put("deviceToken","12344566776");
        obj.put("image","https:\\/\\/graph.facebook.com\\/10209122833009022\\/picture?type=large");
        //obj.put("twitterId","");
        //obj.put("googleId","");



        //http://52.74.13.4/index.php/service/auth/signin
        //192.168.1.8

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                "http://52.74.13.4/index.php/service/auth/signin", obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Test", "After Sending JsongObj"+response.toString());
                       // msgResponse.setText(response.toString());
                        Log.d("response", response.toString());
                        //hideProgressDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Test", "Error: " + error.getMessage());
                //hideProgressDialog();
            }
        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        final int DEFAULT_TIMEOUT = 60000;

        // Adding request to request queue
        //jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
               // DEFAULT_TIMEOUT,
               // DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
               // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq,"post data");

        // Cancelling request
        // ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_test_post1:
                // OK button
                Log.d("test activity: ", "click on post data");
                try {
                    makeJsonObjReq1();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
