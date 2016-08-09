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
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.flurry.android.FlurryAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.foodtalk.android.R;
import in.foodtalk.android.adapter.CuratedAdapter;
import in.foodtalk.android.adapter.newpost.CheckInAdapter;
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.LatLonCallback;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.GetLocation;
import in.foodtalk.android.module.UserAgent;
import in.foodtalk.android.object.RestaurantListObj;

/**
 * Created by RetailAdmin on 09-08-2016.
 */
public class CuratedFragment extends Fragment implements LatLonCallback {

    View layout;
    DatabaseHandler db;
    Config config;

    String lat;
    String lon;

    ProgressBar progressBar;

    LatLonCallback latLonCallback;

    GetLocation getLocation;
    List<RestaurantListObj> restaurantList = new ArrayList<>();

    RecyclerView recyclerView;

    CuratedAdapter curatedAdapter;
    LinearLayoutManager linearLayoutManager;

    /*
   selectedUserId, latitude, longitude, sessionId, foodtalksuggested
   */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.curated_fragment, container, false);
        db = new DatabaseHandler(getActivity().getApplicationContext());
        config = new Config();

        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);

        progressBar = (ProgressBar) layout.findViewById(R.id.progress_bar);


        latLonCallback = this;

        getLocation = new GetLocation(getActivity(), latLonCallback);
        getLocation.onStart();

        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        return layout;
    }
    @Override
    public void onDestroyView() {
        Log.d("Fragment","onDestryView");
        //getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        getLocation.onStop();
        super.onDestroyView();
    }
    public void getRestaurantList(final String tag) throws JSONException {

        Log.d("getPostFeed", "post data");
        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
        obj.put("latitude",lat);
        obj.put("longitude",lon);
        obj.put("foodtalksuggested", "1");
        //obj.put("includeCount", "1");
        //obj.put("includeFollowed","1");
        obj.put("postUserId",db.getUserDetails().get("userId"));
        //Log.d("getPostFeed","pageNo: "+pageNo);
        //obj.put("page",Integer.toString(pageNo));
        // obj.put("recordCount","10");
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                config.URL_NEAR_BY_RESTAURANT, obj,
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

               // progressBarCheckin.setVisibility(View.GONE);
               // tapToRetry.setVisibility(View.VISIBLE);

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
                UserAgent userAgent = new UserAgent();
                if (userAgent.getUserAgent(getActivity()) != null ){
                    headers.put("User-agent", userAgent.getUserAgent(getActivity()));
                }
                return headers;
            }
        };
        final int DEFAULT_TIMEOUT = 6000;
        // Adding request to request queue
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq,"gethomefeed");
    }

    private void loadDataIntoView(JSONObject response, String tag) throws JSONException {

        //progressBarCheckin.setVisibility(View.GONE);

        progressBar.setVisibility(View.GONE);

       // this.response = response;
        JSONArray rListArray = response.getJSONArray("restaurants");
        // Log.d("rListArray", "total: "+ rListArray.length());
        for (int i=0;i<rListArray.length();i++){
            RestaurantListObj current = new RestaurantListObj();
            current.id = rListArray.getJSONObject(i).getString("id");
            current.area = rListArray.getJSONObject(i).getString("area");
            current.restaurantName = rListArray.getJSONObject(i).getString("restaurantName");
            current.restaurantIsActive = rListArray.getJSONObject(i).getString("restaurantIsActive");
            current.priceRange = rListArray.getJSONObject(i).getString("priceRange");
            current.distance = rListArray.getJSONObject(i).getString("distance");
            restaurantList.add(current);
        }
        //Log.d("send list", "total: "+restaurantList.size());
        if (getActivity() != null){
            curatedAdapter = new CuratedAdapter(getActivity(),restaurantList);
            //checkInAdapter = new CheckInAdapter(getActivity(),restaurantList);
            recyclerView.setAdapter(curatedAdapter);
        }
    }

    @Override
    public void location(String lat, String lon) {
        FlurryAgent.setLocation((float)Double.parseDouble(lat), (float)Double.parseDouble(lon));
        this.lat = lat;
        this.lon = lon;
        try {
            getRestaurantList("load");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
