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
 * Created by RetailAdmin on 27-04-2016.
 */
public class PostBookmarkApi implements ApiCallback {
    DatabaseHandler db;
    Config config;
    String apiUrl;
    Context context;
    ApiCall apiCall;
    public PostBookmarkApi (Context context){
        config = new Config();
        db = new DatabaseHandler(context);
        this.context = context;
        apiCall = new ApiCall();
    }
    public void postBookmark(String postId, boolean bookmarkPost) throws JSONException {

        if (bookmarkPost){
            apiUrl = config.URL_POST_BOOKMARK;
        }else {
            apiUrl = config.URL_POST_REMOVE_BOOKMARK;
        }


        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
        obj.put("postId",postId);

        apiCall.apiRequestPost(context, obj, apiUrl, "postbookmark", this);
    }

    @Override
    public void apiResponse(JSONObject response, String tag) {

    }
}
