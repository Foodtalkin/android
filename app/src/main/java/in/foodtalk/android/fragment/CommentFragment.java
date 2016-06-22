package in.foodtalk.android.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.foodtalk.android.R;
import in.foodtalk.android.adapter.CommentAdapter;
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.object.CommentObj;
import in.foodtalk.android.object.PostObj;

/**
 * Created by RetailAdmin on 20-06-2016.
 */
public class CommentFragment extends Fragment {
    View layout;
    Config config;
    DatabaseHandler db;

    String postId;

    PostObj postObj;
    List<CommentObj> postDataList  = new ArrayList<>();

    CommentAdapter commentAdapter;

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    TextView txtUserName;

    public CommentFragment (String postId){
        this.postId = postId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.comment_fragment, container, false);

        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view_comment);

        txtUserName = (TextView) layout.findViewById(R.id.txt_name_comment);

        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        postObj = new PostObj();
        config = new Config();
        db = new DatabaseHandler(getActivity());

        try {
            getPostFeed("load");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return layout;
    }
    public void getPostFeed(final String tag) throws JSONException {
        Log.d("getPostFeed", "post data");
        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
        obj.put("postId",postId);
        //Log.d("getPostFeed","pageNo: "+pageNo);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                config.URL_GET_POST, obj,
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
                                loadDataIntoView(response , tag);
                            }else {
                                String errorCode = response.getString("errorCode");
                                if(errorCode.equals("6")){
                                    Log.d("Response error", "Session has expired");
                                   // logOut();
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
               // showToast("Please check your internet connection");

                if(tag.equals("refresh")){
                    //-- swipeRefreshHome.setRefreshing(false);
                }
                if(tag.equals("loadMore")){
                    //remove(null);
                    //callScrollClass();
                   // pageNo--;
                }
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
        final int DEFAULT_TIMEOUT = 6000;
        // Adding request to request queue
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq,"gethomefeed");
    }

    private void loadDataIntoView(JSONObject response, String tag) throws JSONException {
        JSONObject post = response.getJSONObject("post");
        postObj.id = post.getString("id");
        postObj.userId = post.getString("userId");
        postObj.checkedInRestaurantId = post.getString("checkedInRestaurantId");
        postObj.postImage = post.getString("postImage");
        postObj.dishName = post.getString("dishName");
        postObj.rating = post.getString("rating");
        postObj.tip = post.getString("tip");
        postObj.userName = post.getString("userName");
        postObj.next = post.getString("next");
        postObj.previous = post.getString("previous");
        postObj.iBookark = post.getString("iBookark");
        postObj.bookmarkCount = post.getString("bookmarkCount");
        postObj.iLikedIt = post.getString("iLikedIt");
        postObj.createDate = post.getString("createDate");
        postObj.currentDate = post.getString("currentDate");
        postObj.userImage = post.getString("userImage");
        postObj.userThumb = post.getString("userThumb");
        postObj.restaurantName = post.getString("restaurantName");
        postObj.restaurantIsActive = post.getString("restaurantIsActive");
        postObj.comment_count = post.getString("comment_count");
        postObj.like_count = post.getString("like_count");
        postObj.timeElapsed = post.getString("timeElapsed");

        txtUserName.setText(post.getString("userName"));

        CommentObj commentObj = new CommentObj();
        commentObj.viewType = "post";

        postDataList.add(commentObj);
        commentAdapter = new CommentAdapter(getActivity(), postDataList, postObj);
        recyclerView.setAdapter(commentAdapter);
        Log.d("post", post+"");
    }
}