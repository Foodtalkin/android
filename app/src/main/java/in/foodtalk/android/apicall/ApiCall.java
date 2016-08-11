package in.foodtalk.android.apicall;

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

import in.foodtalk.android.R;
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.module.UserAgent;

/**
 * Created by RetailAdmin on 11-08-2016.
 */
public class ApiCall {
    public void apiRequestPost(final Context context, JSONObject obj, String url, final String tag){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d(TAG, "After Sending JsongObj"+response.toString());
                        //msgResponse.setText(response.toString());
                        Log.d("like api Respond", response.toString());
                        try {
                            String status = response.getString("status");
                            if (!status.equals("error")){
                                //-- getAndSave(response);
                                //loadDataIntoView(response);
                                if(tag.equals("report")){
                                    //showToast(context.getString(R.string.postReportMsg));
                                }else if(tag.equals("delete")){
                                    //deleteCallback.postDelete();
                                }
                            }else {
                                String errorCode = response.getString("errorCode");
                                if(errorCode.equals("6")){
                                    Log.d("Response error", "Session has expired");
                                    //logOut();
                                }else if(errorCode.equals("7")) {
                                    Log.e("Response error", "Already Report");
                                   // showToast(context.getString(R.string.postReportMsg));
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
                // hideProgressDialog();
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
        AppController.getInstance().addToRequestQueue(jsonObjectRequest,"postbookmark");
    }
}
