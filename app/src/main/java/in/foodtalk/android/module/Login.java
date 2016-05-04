package in.foodtalk.android.module;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
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
import in.foodtalk.android.app.Config;
import in.foodtalk.android.object.LoginInfo;

/**
 * Created by RetailAdmin on 18-04-2016.
 */
public class Login {

    private Context context;

    private ProgressDialog pDialog;
    private String TAG = Login.class.getSimpleName();


    public Config config;
    // These tags will be used to cancel the requests
    private String tag_json_obj;

    public Login (Context context){
        this.context = context;

        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        config = new Config();


    }



    public void postLoginInfo(LoginInfo loginInfo, String tag) throws JSONException {
        //showProgressDialog();


        JSONObject obj = new JSONObject();
        obj.put("signInType", loginInfo.signInType);
        obj.put("fullName", loginInfo.fullName);
        obj.put("email",loginInfo.email);
        obj.put("facebookId",loginInfo.facebookId);
        obj.put("latitude",loginInfo.latitude);
        obj.put("longitude",loginInfo.longitude);
        obj.put("deviceToken","12344566776");
        obj.put("image",loginInfo.image);
        //obj.put("twitterId","");
        //obj.put("googleId","");

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                config.URL_LOGIN, obj,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "After Sending JsongObj"+response.toString());
                        //msgResponse.setText(response.toString());
                        Log.d("Login Respond", response.toString());

                        hideProgressDialog();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
               // hideProgressDialog();
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
        // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq,tag);

        // Cancelling request
        // ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);

    }
    private void showProgressDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog.isShowing())
            pDialog.hide();
    }
}
