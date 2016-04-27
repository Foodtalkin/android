package in.foodtalk.android.apicall;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.module.DatabaseHandler;

/**
 * Created by RetailAdmin on 27-04-2016.
 */
public class PostBookmarkApi {
    DatabaseHandler db;
    Config config;
    String apiUrl;
    Context context;
    public PostBookmarkApi (Context context){
        config = new Config();
        db = new DatabaseHandler(context);
        this.context = context;
    }
    public void postLike(String postId, boolean bookmarkPost) throws JSONException {

        if (bookmarkPost){
            apiUrl = config.URL_POST_BOOKMARK;

        }else {
            apiUrl = config.URL_POST_REMOVE_BOOKMARK;
        }


        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
        obj.put("postId",postId);
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
                            }else {
                                String errorCode = response.getString("errorCode");
                                if(errorCode.equals("6")){
                                    Log.d("Response error", "Session has expired");
                                    //logOut();
                                }else {
                                    Log.e("Response status", "some error");
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
                // VolleyLog.d(TAG, "Error: " + error.getMessage());
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
}
