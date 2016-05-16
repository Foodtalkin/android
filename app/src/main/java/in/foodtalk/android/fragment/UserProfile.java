package in.foodtalk.android.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import in.foodtalk.android.adapter.UserProfileAdapter;
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.LatLonCallback;
import in.foodtalk.android.communicator.UserProfileCallback;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.EndlessRecyclerOnScrollListener;
import in.foodtalk.android.module.GetLocation;
import in.foodtalk.android.object.UserPostObj;
import in.foodtalk.android.object.UserProfileObj;

/**
 * Created by RetailAdmin on 06-05-2016.
 */
public class UserProfile extends Fragment implements LatLonCallback {

    View layout;

    RecyclerView recyclerView;
    Config config;
    DatabaseHandler db;
    UserProfileAdapter userProfileAdapter;

    UserPostObj userPost;
    UserProfileObj userProfile;



    List<UserPostObj> postList = new ArrayList<>();

    Boolean loading = false;
    Boolean loadMoreData = true;

    Boolean followBtnVisible;

    StaggeredGridLayoutManager staggeredGridLayoutManager;
    private int pageNo = 1;

    UserProfileCallback userProfileCallback;

    GetLocation getLocation;

    LatLonCallback latLonCallback;

    String lat, lon;

    Context context;
    String userIdOther;
    String userId;
    Button btnFollow;



    public UserProfile (String userId){
           this.userIdOther = userId;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.user_profile_fragment, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.user_profile_recycler_view);
        if (postList.size() > 0){
            postList.clear();
            loadMoreData = true;
            Log.d("loadData: ","clear post data");
        }
        pageNo = 1;
        loading = false;





        //btnFollow = (Button) layout.findViewById(R.id.btn_follow_profile);


        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        config = new Config();
        userPost = new UserPostObj();
        userProfile = new UserProfileObj();


        //------for gps location----------------
        latLonCallback = this;
        //getLocation = new GetLocation(getActivity(), latLonCallback);

       // getLocation.onStart();



        staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        if (getActivity() != null){
            context = getActivity();
        }



        userProfileCallback = (UserProfileCallback) context;

        db = new DatabaseHandler(context);

        userId = db.getUserDetails().get("userId");

        Log.d("check user ids","userId: "+userId+ " userIdOther: "+userIdOther);
        if (userIdOther.equals(userId)){
            Log.d("userIds","Match");
            followBtnVisible = false;
           // btnFollow.setVisibility(View.GONE);
        }else {
           // btnFollow.setVisibility(View.VISIBLE);
            followBtnVisible = true;
            Log.d("userIds", "not match");
        }



        try {
            getUserProfile("myProfile");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        //------for gps location----------------
        // getLocation.onStop();
        super.onDestroy();
    }
    public void getUserProfile(final String tag) throws JSONException {

        String url = "";

        if (tag.equals("myProfile")){
            url = config.URL_USER_PROFILE;

        }else if (tag.equals("myProfilePost")){
            url = config.URL_USER_POST_IMAGE;
        }

        //Log.d("getUserProfile", "call");

        JSONObject obj = new JSONObject();
        obj.put("sessionId",db.getUserDetails().get("sessionId"));
        //obj.put("selectedUserId", db.getUserDetails().get("userId"));
        obj.put("page",Integer.toString(pageNo));
        obj.put("selectedUserId", userIdOther);
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
                return headers;
            }
        };
        final int DEFAULT_TIMEOUT = 6000;
        //Adding request to request queue
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjectRequest,"getUserProfile");
    }

    private void loadDataIntoView(JSONObject response, String tag) throws JSONException {
        if (tag.equals("myProfile")){
            JSONObject profile = response.getJSONObject("profile");
            userProfile.userName = profile.getString("userName");
            userProfile.fullName = profile.getString("fullName");
            userProfile.checkInCount = profile.getString("checkInCount");
            userProfile.followersCount = profile.getString("followersCount");
            userProfile.followingCount = profile.getString("followingCount");
            userProfile.iFollowedIt = profile.getString("iFollowedIt");
            userProfile.image = profile.getString("image");
            userProfile.totalPoints = profile.getString("totalPoints");
            userProfile.score = profile.getString("score");
            userProfile.userId = profile.getString("id");

            UserPostObj userPostObj = new UserPostObj();
            userPostObj.viewType = "profileInfo";
            postList.add(userPostObj);
            userProfileCallback.getUserInfo(userProfile.score, userProfile.userName);
        }
        JSONArray postArray = response.getJSONArray("imagePosts");
        //Log.d("Image post", postArray.getJSONObject(0).getString("postImage")+"");

        //----if array length 0 or less then 15 then ignore loadmore next time------------
        if (postArray.length() == 0){
            loadMoreData = false;

            UserPostObj userPostObj = new UserPostObj();
            userPostObj.viewType = "errorCopy";
            postList.add(userPostObj);

            //Log.d("postArray length", "0");
        }else if (postArray.length() < 15){
            //Log.d("postArray length", postArray.length()+"");
            loadMoreData = false;
        }
        //--------------------------------------------------------------------------------

        for(int i=0; postArray.length() > i; i++){
            UserPostObj current = new UserPostObj();
            current.viewType = "postImg";
            current.postImage = postArray.getJSONObject(i).getString("postImage");
            current.userId = postArray.getJSONObject(i).getString("userId");
            current.id = postArray.getJSONObject(i).getString("id");
            current.checkinId = postArray.getJSONObject(i).getString("checkinId");
            current.checkedInRestaurantId = postArray.getJSONObject(i).getString("checkedInRestaurantId");
            current.tip = postArray.getJSONObject(i).getString("tip");
            current.dishName = postArray.getJSONObject(i).getString("dishName");
            current.rating = postArray.getJSONObject(i).getString("rating");
            current.createDate = postArray.getJSONObject(i).getString("createDate");
            current.currentDate = postArray.getJSONObject(i).getString("currentDate");
            current.userName = postArray.getJSONObject(i).getString("userName");
            current.email = postArray.getJSONObject(i).getString("email");
            current.country = postArray.getJSONObject(i).getString("country");
            current.state = postArray.getJSONObject(i).getString("state");
            current.city = postArray.getJSONObject(i).getString("city");
            current.address = postArray.getJSONObject(i).getString("address");
            current.postcode = postArray.getJSONObject(i).getString("postcode");
            current.userImage = postArray.getJSONObject(i).getString("userImage");
            current.facebookId = postArray.getJSONObject(i).getString("facebookId");
            current.userImage = postArray.getJSONObject(i).getString("userImage");
            current.restaurantName = postArray.getJSONObject(i).getString("restaurantName");
            current.restaurantIsActive = postArray.getJSONObject(i).getString("restaurantIsActive");
            current.followersCount = postArray.getJSONObject(i).getString("followersCount");
            current.likeCount = postArray.getJSONObject(i).getString("likeCount");
            current.commentCount = postArray.getJSONObject(i).getString("commentCount");
            current.flagCount = postArray.getJSONObject(i).getString("flagCount");
            current.bookmarkCount = postArray.getJSONObject(i).getString("bookmarkCount");
            current.iLikedIt = postArray.getJSONObject(i).getString("iLikedIt");
            current.iFlaggedIt = postArray.getJSONObject(i).getString("iFlaggedIt");
            current.iBookark = postArray.getJSONObject(i).getString("iBookark");
            current.timeElapsed = postArray.getJSONObject(i).getString("timeElapsed");
            //current.restaurantDistance = postArray.getJSONObject(i).getString("restaurantDistance");
            //Log.d("postImage", current.postImage);
            postList.add(current);
        }

        if (tag.equals("myProfilePost")){
            remove(null);
            loading = false;
            //userProfileAdapter.notifyDataSetChanged();
        }else {
            userProfileAdapter = new UserProfileAdapter(context, postList , userProfile ,followBtnVisible);
            recyclerView.setAdapter(userProfileAdapter);
        }
        callScrollClass();
    }

    //-----remove function is used to remove progress bar using indexOf position of null objevt--------
    public void remove(ContactsContract.Contacts.Data data) {
        int position = postList.indexOf(data);
        //Log.d("position for remove", position+"");
        postList.remove(position);
        userProfileAdapter.notifyItemRemoved(position);
    }
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
                    postList.add(null);
                    //recyclerView.addD
                    userProfileAdapter.notifyItemInserted(postList.size()-1);
                    //--homeFeedAdapter.notifyItemInserted(postData.size()-1);
                    loading = true;
                    //Log.d("loadMore", "call getPostFeed('loadMore')");
                    try {
                        getUserProfile("myProfilePost");
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

    @Override
    public void location(String lat, String lon) {
        //Log.d("location", "lat: "+ lat+" lon: "+lon);
        Log.d("GPS location","Latitude "+lat);
        Log.d("GPS location","Longitude "+lon);
        this.lat = lat;
        this.lon = lon;
        try {
            pageNo = 1;
            getUserProfile("myProfile");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
