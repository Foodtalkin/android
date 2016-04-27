package in.foodtalk.android.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.foodtalk.android.FbLogin;
import in.foodtalk.android.R;
import in.foodtalk.android.adapter.HomeFeedAdapter;
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.PostLikeCallback;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.object.PostObj;

/**
 * Created by RetailAdmin on 21-04-2016.
 */
public class HomeFragment extends Fragment{
    View layout;
    DatabaseHandler db;
    Config config;
    PostObj postObj;
    HomeFeedAdapter homeFeedAdapter;
    List<PostObj> postData = new ArrayList<>();

    private RecyclerView.LayoutManager mLayoutManager;

    PostLikeCallback likeCallback;

    RecyclerView recyclerView;

    PostLikeCallback postLikeCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.home_fragment, container, false);



        //postLikeCallback = this;
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view_home);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        return layout;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        config = new Config();
        db = new DatabaseHandler(getActivity().getApplicationContext());
        postObj = new PostObj();
        Log.d("get user info F", db.getUserDetails().get("sessionId"));
        Log.d("get user info F", db.getUserDetails().get("userId"));
        try {
            getPostFeed();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onActivityCreated(savedInstanceState);
    }
    private void getPostFeed() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
        obj.put("includeCount", "1");
        obj.put("includeFollowed","1");
        obj.put("postUserId",db.getUserDetails().get("userId"));
        obj.put("page","0");
        obj.put("recordCount","10");
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                config.URL_POST_LIST, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d(TAG, "After Sending JsongObj"+response.toString());
                        //msgResponse.setText(response.toString());
                        Log.d("Login Respond", response.toString());
                        try {
                            String status = response.getString("status");
                            if (!status.equals("error")){
                               //-- getAndSave(response);
                                loadDataIntoView(response);
                            }else {
                                String errorCode = response.getString("errorCode");
                                if(errorCode.equals("6")){
                                    Log.d("Response error", "Session has expired");
                                    logOut();
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
        AppController.getInstance().addToRequestQueue(jsonObjReq,"gethomefeed");
    }
    private void loadDataIntoView(JSONObject response) throws JSONException {

        JSONArray postArray = response.getJSONArray("posts");
        JSONObject postObject = postArray.getJSONObject(0);
        //String userName = postObject.getString("userName");
        //Log.d("user name from post", userName);

        for (int i=0; postArray.length()>i;i++){
            //String userName = postArray.getJSONObject(i).getString("userName");
            //PostObj current = new PostObj();
            PostObj current = new PostObj();
            current.id = postArray.getJSONObject(i).getString("id");
            current.userName = postArray.getJSONObject(i).getString("userName");
            current.dishName = postArray.getJSONObject(i).getString("dishName");
            current.restaurantName = postArray.getJSONObject(i).getString("restaurantName");
            current.createDate = postArray.getJSONObject(i).getString("createDate");
            current.currentDate = postArray.getJSONObject(i).getString("currentDate");
            current.likeCount = postArray.getJSONObject(i).getString("likeCount");
            current.bookmarkCount = postArray.getJSONObject(i).getString("bookmarkCount");
            current.commentCount = postArray.getJSONObject(i).getString("commentCount");
            current.userThumb = postArray.getJSONObject(i).getString("userThumb");
            current.userImage = postArray.getJSONObject(i).getString("userImage");
            current.postImage = postArray.getJSONObject(i).getString("postImage");
            current.postThumb = postArray.getJSONObject(i).getString("postThumb");
            current.iLikedIt = postArray.getJSONObject(i).getString("iLikedIt");
            current.iBookark = postArray.getJSONObject(i).getString("iBookark");

            postData.add(current);
           // Log.d("user name", userName);
        }
        //postData = (List<PostObj>) postObj;
        homeFeedAdapter = new HomeFeedAdapter(getActivity(), postData, postLikeCallback);
        recyclerView.setAdapter(homeFeedAdapter);
    }
    private void logOut(){
        db.resetTables();
        Intent i = new Intent(getActivity().getApplicationContext(), FbLogin.class);
        startActivity(i);
        getActivity().finish();
    }


}