package in.foodtalk.android.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.foodtalk.android.R;
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.object.UserPostObj;

/**
 * Created by RetailAdmin on 06-05-2016.
 */
public class UserProfile extends Fragment {

    View layout;

    RecyclerView recyclerView;
    Config config;
    DatabaseHandler db;

    UserPostObj userPost;

    List<UserPostObj> postList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.user_profile_fragment, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.user_profile_recycler_view);
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        config = new Config();

        db = new DatabaseHandler(getActivity());

        try {
            getUserProfile("own");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void getUserProfile(final String tag) throws JSONException {

        Log.d("getUserProfile", "call");

        JSONObject obj = new JSONObject();
        obj.put("sessionId",db.getUserDetails().get("sessionId"));
        //obj.put("selectedUserId", db.getUserDetails().get("userId"));
        obj.put("selectedUserId", "2");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                config.URL_USER_PROFILE,
                obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("Responsne", response+"");
                        try {
                            loadDataIntoView(response, tag);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Response","Error: "+ error.getMessage());
            }
        }){

            //--Passing some request headers--
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        final int DEFAULT_TIMEOUT = 6000;
        // Adding request to request queue
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjectRequest,"getUserProfile");
    }

    private void loadDataIntoView(JSONObject response, String tag) throws JSONException {
        JSONArray postArray = response.getJSONArray("imagePosts");
        //Log.d("Image post", postArray.getJSONObject(0).getString("postImage")+"");
        for(int i=0; postArray.length() > i; i++){
            UserPostObj current = new UserPostObj();
            current.postImage = postArray.getJSONObject(i).getString("postImage");
            Log.d("postImage", current.postImage);
            postList.add(current);
        }
    }
}
