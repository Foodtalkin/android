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
import in.foodtalk.android.communicator.ApiCallback;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.UserAgent;

/**
 * Created by RetailAdmin on 16-05-2016.
 */
public class UserFollow implements ApiCallback {
    DatabaseHandler db;
    Config config;
    String apiUrl;
    Context context;
    String requestTag;

    ApiCall apiCall;
    public UserFollow(Context context){
        config = new Config();
        db = new DatabaseHandler(context);
        this.context = context;

    }
    public void follow(boolean userFollow, String followedUserId) throws JSONException {

        if (userFollow){
            apiUrl = config.URL_FOLLOW;
            requestTag = "userFollow";
        }else {
            apiUrl = config.URL_UNFOLLOW;
            requestTag = "userUnfollow";
        }
        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
        //obj.put("postId",postId);
        obj.put("followedUserId", followedUserId);

        apiCall = new ApiCall();

        apiCall.apiRequestPost(context, obj, apiUrl, requestTag, this);
    }

    @Override
    public void apiResponse(JSONObject response, String tag) {

    }
}
