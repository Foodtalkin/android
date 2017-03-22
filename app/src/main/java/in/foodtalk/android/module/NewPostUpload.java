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
import com.cloudinary.Api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import in.foodtalk.android.apicall.ApiCall;
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.ApiCallback;
import in.foodtalk.android.communicator.NewPostCallback;
import in.foodtalk.android.object.CreatePostObj;

/**
 * Created by RetailAdmin on 31-05-2016.
 */
public class NewPostUpload implements ApiCallback {

    Config config;
    CreatePostObj createPostObj;

    NewPostCallback newPostCallback;
    LinearLayout progressBar;
    Context context;

    ApiCall apiCall;

    public NewPostUpload (CreatePostObj createPostObj , NewPostCallback newPostCallback, LinearLayout progressBar, Context context){
        config = new Config();

        this.progressBar = progressBar;

        this.createPostObj = createPostObj;

        this.newPostCallback = newPostCallback;

        this.context = context;

        apiCall = new ApiCall();

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

        apiCall.apiRequestPost(context, obj, Config.URL_POST_CREATE, "uploadDish", this);
    }

    @Override
    public void apiResponse(JSONObject response, String tag) {
        if (tag.equals("uploadDish")){
            if (response != null){
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
            }
        }
    }
}
