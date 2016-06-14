package in.foodtalk.android.search;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.foodtalk.android.R;
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.SearchCallback;
import in.foodtalk.android.module.DatabaseHandler;

/**
 * Created by Belal on 2/3/2016.
 */

//Our class extending fragment
public class Tab1 extends Fragment implements SearchCallback {

    View layout;

    RecyclerView recyclerView;
    DatabaseHandler db;
    Config config;
    String keyword;

    static final String TAG = "searchDish";

    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.search_tab1, container, false);

        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_search_dish);



        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes
        return layout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        db = new DatabaseHandler(context);
        config = new Config();
    }

    @Override
    public void searchKey(String keyword, String searchType) {
        this.keyword = keyword;

        AppController.getInstance().cancelPendingRequests(TAG);

        try {
            getDishList(TAG);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("search key", "keyword: "+ keyword+" searchType "+searchType);
    }

    public void getDishList(final String tag) throws JSONException {

        //Log.d("getPostFeed", "post data");
        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
        obj.put("search", keyword);
        obj.put("region", "delhi");
        //obj.put("latitude","28.4820495");
        // obj.put("longitude","77.0832561");
        //obj.put("includeCount", "1");
        //obj.put("includeFollowed","1");
        // obj.put("postUserId",db.getUserDetails().get("userId"));
        //Log.d("getPostFeed","pageNo: "+pageNo);
        //obj.put("page",Integer.toString(pageNo));
        // obj.put("recordCount","10");

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                config.URL_DISH_LIST, obj,
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
                //showToast("Please check your internet connection");

                if(tag.equals("refresh")){
                    //swipeRefreshHome.setRefreshing(false);
                }
                if(tag.equals("loadMore")){
                    //remove(null);
                    //callScrollClass();
                    //pageNo--;
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
}
