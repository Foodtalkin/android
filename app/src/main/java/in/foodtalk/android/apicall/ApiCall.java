package in.foodtalk.android.apicall;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

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

import in.foodtalk.android.Home;
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.ApiCallback;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.UserAgent;

/**
 * Created by RetailAdmin on 11-08-2016.
 */
public class ApiCall {
    ApiCallback apiCallback1;
    DatabaseHandler db;
    public void apiRequestPost(final Context context, final JSONObject obj, final String url, final String tag, final ApiCallback apiCallback){

        db = new DatabaseHandler(context);
        apiCallback1 = apiCallback;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d(TAG, "After Sending JsongObj"+response.toString());
                        //msgResponse.setText(response.toString());
                        Log.d("like api Respond", response.toString());
                        if (apiCallback1 != null){
                            apiCallback1.apiResponse(response, tag);
                        }
                        try {
                            String status = response.getString("status");
                            if (!status.equals("error")){
                                //-- getAndSave(response);
                                //loadDataIntoView(response);
                                if(tag.equals("userReport") || tag.equals("restaurantReport")){
                                    showToast(context, "Your report send successfully.");
                                    //Toast.makeText(context, "Your report send successfully.",
                                           // Toast.LENGTH_SHORT).show();
                                }else if(tag.equals("delete")){
                                    //deleteCallback.postDelete();
                                }
                            }else {
                                String errorCode = response.getString("errorCode");
                                if(errorCode.equals("6")){
                                    Log.d("Response error", "Session has expired");
                                    //logOut();
                                    getSessionToken(context, obj, url, tag, apiCallback);
                                }else if(errorCode.equals("7")) {
                                    if (tag.equals("userReport") || tag.equals("restaurantReport")){
                                        showToast(context, "Your report send successfully.");
                                    }
                                   //showToast(context.getString(R.string.postReportMsg));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("Json Error", e+"");
                        }
                        //----------------------
                        //hideProgressDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("error response", "Error: " + error.getMessage());
                apiCallback1.apiResponse(null, tag);
                //hideProgressDialog();
            }
        }){
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                UserAgent userAgent = new UserAgent();
                if (userAgent.getUserAgent(context) != null ){
                    headers.put("User-agent", userAgent.getUserAgent(context));
                }
                return headers;
            }
        };
        final int DEFAULT_TIMEOUT = 6000;
        // Adding request to request queue
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().cancelPendingRequests(tag);
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag);
    }
    public void showToast(Context context, String msg){
        Toast toast= Toast.makeText(context,
                msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 300);
        toast.show();
    }


    public void getSessionToken(final Context context, final JSONObject obj, final String url, final String tag, final ApiCallback apiCallback){


        String refreshToken = db.getUserDetails().get("refreshtoken");
        String sessionId = db.getUserDetails().get("sessionId");
        JSONObject obj1 = new JSONObject();
        try {
            obj1.put("refreshToken", refreshToken);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_AUTH_REFRESH, obj1,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        Log.d("ApiCall","getSessionToken: "+ response);
                        try {
                            String status = response.getString("status");
                            if (!status.equals("error")){
                                //-- getAndSave(response);
                                //loadDataIntoView(response);
                                db.updateTokens(db.getUserDetails().get("userId"), response.getString("sessionId"), response.getString("refreshToken"));
                                obj.put("sessionId", db.getUserDetails().get("sessionId"));
                                Log.d("ApiCall","new sessionId: "+ db.getUserDetails().get("sessionId"));
                                apiRequestPost(context, obj, url, tag, apiCallback);
                            }else {
                                String errorCode = response.getString("errorCode");
                                if(errorCode.equals("106")){
                                    Home home = (Home) context;
                                    home.logOut();
                                    Log.d("Response error", "Session has expired");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("Json Error", e+"");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("error response", "Error: " + error.getMessage());
                apiCallback1.apiResponse(null, tag);
            }
        }){
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                UserAgent userAgent = new UserAgent();
                if (userAgent.getUserAgent(context) != null ){
                    headers.put("User-agent", userAgent.getUserAgent(context));
                }
                return headers;
            }
        };
        final int DEFAULT_TIMEOUT = 6000;
        // Adding request to request queue
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag);
    }
}
