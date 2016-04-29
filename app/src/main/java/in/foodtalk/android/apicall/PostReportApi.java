package in.foodtalk.android.apicall;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

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
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.PostDeleteCallback;
import in.foodtalk.android.module.DatabaseHandler;

/**
 * Created by RetailAdmin on 29-04-2016.
 */
public class PostReportApi {

    DatabaseHandler db;
    Config config;
    String apiUrl;
    Context context;
    PostDeleteCallback deleteCallback;

   // String tag;
    public PostReportApi (Context context){
        config = new Config();
        db = new DatabaseHandler(context);
        this.context = context;
        deleteCallback = (PostDeleteCallback) context;
    }
    public void postReport(String sessionId, String postId, final String tag) throws JSONException {

       // this.tag = tag;
        if (tag.equals("delete")){
            apiUrl = config.URL_POST_DELETE;

        }else if(tag.equals("report")) {
            apiUrl = config.URL_POST_REPORT;
        }

        JSONObject obj = new JSONObject();
        //obj.put("sessionId", db.getUserDetails().get("sessionId"));
        obj.put("sessionId", sessionId);
        obj.put("postId", postId);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                apiUrl, obj,
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
                                    showToast(context.getString(R.string.postReportMsg));
                                }else if(tag.equals("delete")){
                                    deleteCallback.postDelete();
                                }
                            }else {
                                String errorCode = response.getString("errorCode");
                                if(errorCode.equals("6")){
                                    Log.d("Response error", "Session has expired");
                                    //logOut();
                                }else if(errorCode.equals("7")) {
                                    Log.e("Response error", "Already Report");
                                    showToast(context.getString(R.string.postReportMsg));
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
        AppController.getInstance().addToRequestQueue(jsonObjReq,"postbookmark");
    }

    public void showToast(String msg){
        Toast toast= Toast.makeText(context,
                msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 300);
        toast.show();
    }
}
