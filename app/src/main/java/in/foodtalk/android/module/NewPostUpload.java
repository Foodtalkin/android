package in.foodtalk.android.module;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.NewPostCallback;
import in.foodtalk.android.object.CreatePostObj;

/**
 * Created by RetailAdmin on 31-05-2016.
 */
public class NewPostUpload {

    Config config;
    CreatePostObj createPostObj;

    NewPostCallback newPostCallback;
    LinearLayout progressBar;
    Context context;

    public NewPostUpload (CreatePostObj createPostObj , NewPostCallback newPostCallback, LinearLayout progressBar, Context context){
        config = new Config();

        this.progressBar = progressBar;

        this.createPostObj = createPostObj;

        this.newPostCallback = newPostCallback;

        this.context = context;

        //newPostCallback = ;
    }

    public void uploadNewPost() throws JSONException {

        byte[] data;
        byte[] dataTip;
        String base64DishName;
        String base64Tip;
        JSONObject obj = new JSONObject();
        Log.d("NewPostUpload", createPostObj+" : "+createPostObj.dishName);
        try {
            data = createPostObj.dishName.getBytes("UTF-8");
            dataTip = createPostObj.tip.getBytes("UTF-8");
            base64DishName = Base64.encodeToString(data, Base64.DEFAULT);
            base64Tip = Base64.encodeToString(dataTip,Base64.DEFAULT);
            obj.put("dishName", base64DishName);
            obj.put("tip",base64Tip);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //String base64 = Base64.encodeToString(data, Base64.DEFAULT);

        Log.d("uploadnewPost",createPostObj.dishName);

        obj.put("sessionId",createPostObj.sessionId);
        obj.put("checkedInRestaurantId",createPostObj.checkedInRestaurantId);
        obj.put("image",createPostObj.image);
        obj.put("rating",createPostObj.rating);
        obj.put("sendPushNotification",createPostObj.sendPushNotification);
        obj.put("shareOnFacebook",createPostObj.shareOnFacebook);
        obj.put("shareOnTwitter",createPostObj.shareOnTwitter);
        obj.put("shareOnInstagram",createPostObj.shareOnInstagram);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                config.URL_POST_CREATE, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d(TAG, "After Sending JsongObj"+response.toString());
                        //msgResponse.setText(response.toString());
                        Log.d("api Respond", response.toString());
                        try {
                            String status = response.getString("status");
                            if (!status.equals("error")){
                                //-- getAndSave(response);
                                progressBar.setVisibility(View.GONE);

                                newPostCallback.onPostCreated("sucsses");
                                Log.d("newPOstCallback","run");

                                //loadDataIntoView(response , tag);
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
                VolleyLog.d("Response", "Error: " + error.getMessage());

                Log.d("onErrorResponse","check oyur internet connection");
                //showToast("Please check your internet connection");
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
                UserAgent userAgent = new UserAgent();
                if (userAgent.getUserAgent(context) != null ){
                    headers.put("User-agent", userAgent.getUserAgent(context));
                }
                return headers;
            }
        };
        final int DEFAULT_TIMEOUT = 6000;
        // Adding request to request queue
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq,"uploadpost");
    }
}
