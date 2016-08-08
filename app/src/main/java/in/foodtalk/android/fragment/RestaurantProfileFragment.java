package in.foodtalk.android.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
import in.foodtalk.android.adapter.RestaurantProfileAdapter;
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.EndlessRecyclerOnScrollListener;
import in.foodtalk.android.module.UserAgent;
import in.foodtalk.android.object.RestaurantPostObj;
import in.foodtalk.android.object.RestaurantProfileObj;

/**
 * Created by RetailAdmin on 17-05-2016.
 */
public class RestaurantProfileFragment extends Fragment {
    String restaurantId;
    View layout;
    RecyclerView recyclerView;
    RestaurantProfileAdapter restaurantProfileAdapter;


    Config config;
    DatabaseHandler db;

    Context context;

    RestaurantProfileObj rProfile;
    RestaurantPostObj rPostObj;

    List<RestaurantPostObj> rPostList = new ArrayList<>();

    Boolean loading = false;
    Boolean loadMoreData = true;

    LinearLayout progressBar;

    StaggeredGridLayoutManager staggeredGridLayoutManager;
    private int pageNo = 1;

    /*public RestaurantProfileFragment(String restaurantId){
        this.restaurantId = restaurantId;
    }*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.restaurant_profile_fragment, container, false);
        restaurantId =  getArguments().getString("restaurantId");
        recyclerView = (RecyclerView) layout.findViewById(R.id.restaurant_profile_recycler_view);
        progressBar = (LinearLayout) layout.findViewById(R.id.progress_bar);

        if (rPostList.size() > 0){
            rPostList.clear();
            loadMoreData = true;
            Log.d("loadData: ","clear post data");
        }
        pageNo = 1;
        loading = false;
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {


        context = getActivity();
        config = new Config();
        rProfile = new RestaurantProfileObj();
        db = new DatabaseHandler(context);

        try {
            getRestaurantProfile("restaurantProfile");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);



        super.onActivityCreated(savedInstanceState);
    }

    public void getRestaurantProfile(final String tag) throws JSONException {

        String url = "";

        if (tag.equals("restaurantProfile")){
            url = config.URL_RESTAURANT_PROFILE;

        }else if (tag.equals("myProfilePost")){
            //url = config.URL_USER_POST_IMAGE;
        }

        //Log.d("getUserProfile", "call");

        JSONObject obj = new JSONObject();
        obj.put("sessionId",db.getUserDetails().get("sessionId"));
        obj.put("restaurantId", restaurantId);
        obj.put("page",Integer.toString(pageNo));
        //obj.put("selectedUserId", userIdOther);
        //obj.put("latitude",lat);
        //obj.put("longitude",lon);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url,
                obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Responsne", response+"");
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
                UserAgent userAgent = new UserAgent();
                if (userAgent.getUserAgent(getActivity()) != null ){
                    headers.put("User-agent", userAgent.getUserAgent(getActivity()));
                }
                return headers;
            }
        };
        final int DEFAULT_TIMEOUT = 6000;
        //Adding request to request queue
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjectRequest,"getUserProfile");
    }
    private void loadDataIntoView(JSONObject response, String tag) throws JSONException {
        progressBar.setVisibility(View.GONE);
        if (tag.equals("restaurantProfile")){
            JSONObject profile = response.getJSONObject("restaurantProfile");
            rProfile.restaurantName = profile.getString("restaurantName");
            rProfile.address = profile.getString("address");
            rProfile.phone1 = profile.getString("phone1");
            rProfile.phone2 = profile.getString("phone2");
            rProfile.checkInCount = profile.getString("checkInCount");
            rProfile.distance = profile.getString("distance");

            RestaurantPostObj restaurantPostObj = new RestaurantPostObj();
            restaurantPostObj.viewType = "profileInfo";
            rPostList.add(restaurantPostObj);

        }

        //----if array length 0 or less then 15 then ignore loadmore next time------------
        JSONArray postArray = response.getJSONArray("images");

        if (postArray.length() == 0){
            loadMoreData = false;

            RestaurantPostObj rPostObj = new RestaurantPostObj();
            rPostObj.viewType = "errorCopy";
            rPostList.add(rPostObj);

            //Log.d("postArray length", "0");
        }else if (postArray.length() < 15){
            //Log.d("postArray length", postArray.length()+"");
            loadMoreData = false;
        }
        //--------------------



        for (int i=0;i<postArray.length();i++){
            RestaurantPostObj current = new RestaurantPostObj();
            current.viewType = "postImg";
            current.id = postArray.getJSONObject(i).getString("id");
            current.userId = postArray.getJSONObject(i).getString("userId");
            current.checkinId = postArray.getJSONObject(i).getString("checkinId");
            current.checkedInRestaurantId = postArray.getJSONObject(i).getString("checkedInRestaurantId");
            current.postImage = postArray.getJSONObject(i).getString("postImage");
            current.tip = postArray.getJSONObject(i).getString("tip");
            current.dishName = postArray.getJSONObject(i).getString("dishName");
            current.rating = postArray.getJSONObject(i).getString("rating");
            current.createDate = postArray.getJSONObject(i).getString("userId");
            current.currentDate = postArray.getJSONObject(i).getString("currentDate");
            current.userName = postArray.getJSONObject(i).getString("userName");
            current.email = postArray.getJSONObject(i).getString("email");
            current.country = postArray.getJSONObject(i).getString("country");
            current.state = postArray.getJSONObject(i).getString("state");
            current.userImage = postArray.getJSONObject(i).getString("userImage");
            current.restaurantName = postArray.getJSONObject(i).getString("restaurantName");
            current.followersCount = postArray.getJSONObject(i).getString("followersCount");
            current.likeCount = postArray.getJSONObject(i).getString("likeCount");
            current.commentCount = postArray.getJSONObject(i).getString("commentCount");
            current.flagCount = postArray.getJSONObject(i).getString("flagCount");
            current.bookmarkCount = postArray.getJSONObject(i).getString("bookmarkCount");
            current.iLikedIt = postArray.getJSONObject(i).getString("iLikedIt");
            current.iFlaggedIt = postArray.getJSONObject(i).getString("iFlaggedIt");
            current.iBookark = postArray.getJSONObject(i).getString("iBookark");
            current.timeElapsed = postArray.getJSONObject(i).getString("timeElapsed");
            current.restaurantIsActive = postArray.getJSONObject(i).getString("restaurantIsActive");

            rPostList.add(current);
        }

        if (tag.equals("myProfilePost")){
            remove(null);
            loading = false;
            //userProfileAdapter.notifyDataSetChanged();
        }else {
            restaurantProfileAdapter = new RestaurantProfileAdapter(context, rPostList , rProfile);
            recyclerView.setAdapter(restaurantProfileAdapter);
        }
        callScrollClass();
    }
    //-----remove function is used to remove progress bar using indexOf position of null objevt--------
    public void remove(ContactsContract.Contacts.Data data) {
        int position = rPostList.indexOf(data);
        //Log.d("position for remove", position+"");
        rPostList.remove(position);
        restaurantProfileAdapter.notifyItemRemoved(position);
    }
    //------

    //------------------------------------------------------------------------------------------------
    private void callScrollClass(){
        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(null, staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                //Log.d("scroll listener", "loading: "+ loading+" loadMoreData: "+loadMoreData);

                if(!loading && loadMoreData == true){
                    pageNo++;
                    // UserPostObj userPostObj = new UserPostObj();
                    //userPostObj.viewType = "progress";
                    rPostList.add(null);
                    //recyclerView.addD
                    restaurantProfileAdapter.notifyItemInserted(rPostList.size()-1);
                    //--homeFeedAdapter.notifyItemInserted(postData.size()-1);
                    loading = true;
                    //Log.d("loadMore", "call getPostFeed('loadMore')");
                    try {
                        getRestaurantProfile("myProfilePost");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onScrolled1(int dx, int dy, int firstVisibleItem, int lastVisibleItem) {

            }
        });
    }


}
