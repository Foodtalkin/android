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
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.EndlessRecyclerOnScrollListener;
import in.foodtalk.android.object.UserPostObj;
import in.foodtalk.android.object.UserProfileObj;

/**
 * Created by RetailAdmin on 06-05-2016.
 */
public class UserProfile extends Fragment {

    View layout;

    RecyclerView recyclerView;
    Config config;
    DatabaseHandler db;
    UserProfileAdapter userProfileAdapter;

    UserPostObj userPost;
    UserProfileObj userProfile;

    Context context;

    List<UserPostObj> postList = new ArrayList<>();

    Boolean loading = false;
    Boolean loadMoreData = true;

    StaggeredGridLayoutManager staggeredGridLayoutManager;
    private int pageNo = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.user_profile_fragment, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.user_profile_recycler_view);
        if (postList.size() > 0){
            postList.clear();

            Log.d("loadData: ","clear post data");
        }
        pageNo = 1;
        loading = false;
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        config = new Config();
        userPost = new UserPostObj();
        userProfile = new UserProfileObj();

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        if (getActivity() != null){
            context = getActivity();
        }

        db = new DatabaseHandler(getActivity());

        try {
            getUserProfile("myProfile");
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

        String url = "";

        if (tag.equals("myProfile")){
            url = config.URL_USER_PROFILE;

        }else if (tag.equals("myProfilePost")){
            url = config.URL_USER_POST_IMAGE;
        }

        Log.d("getUserProfile", "call");

        JSONObject obj = new JSONObject();
        obj.put("sessionId",db.getUserDetails().get("sessionId"));
        //obj.put("selectedUserId", db.getUserDetails().get("userId"));
        obj.put("selectedUserId", "2");

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
            UserPostObj userPostObj = new UserPostObj();
            userPostObj.viewType = "profileInfo";
            postList.add(userPostObj);
        }
        JSONArray postArray = response.getJSONArray("imagePosts");
        //Log.d("Image post", postArray.getJSONObject(0).getString("postImage")+"");

        //----if array length 0 or less then 15 then ignore loadmore next time------------
        if (postArray.length() == 0){
            loadMoreData = false;
            Log.d("postArray length", "0");
        }else if (postArray.length() < 15){
            Log.d("postArray length", postArray.length()+"");
            loadMoreData = false;
        }
        //--------------------------------------------------------------------------------

        for(int i=0; postArray.length() > i; i++){
            UserPostObj current = new UserPostObj();
            current.viewType = "postImg";
            current.postImage = postArray.getJSONObject(i).getString("postImage");
            //Log.d("postImage", current.postImage);
            postList.add(current);
        }

        if (tag.equals("myProfilePost")){
            remove(null);
            loading = false;
            //userProfileAdapter.notifyDataSetChanged();
        }else {
            userProfileAdapter = new UserProfileAdapter(context, postList , userProfile);
            recyclerView.setAdapter(userProfileAdapter);
        }

        callScrollClass();

    }

    //-----remove function is used to remove progress bar using indexOf position of null objevt--------
    public void remove(ContactsContract.Contacts.Data data) {
        int position = postList.indexOf(data);
        Log.d("position for remove", position+"");
        postList.remove(position);
        userProfileAdapter.notifyItemRemoved(position);
    }
    //------------------------------------------------------------------------------------------------
    private void callScrollClass(){
        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(null, staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                Log.d("scroll listener", "current_page: "+ current_page);
                if(!loading && loadMoreData == true){
                    pageNo++;
                   // UserPostObj userPostObj = new UserPostObj();
                    //userPostObj.viewType = "progress";
                    postList.add(null);
                    //recyclerView.addD
                    userProfileAdapter.notifyItemInserted(postList.size()-1);
                    //--homeFeedAdapter.notifyItemInserted(postData.size()-1);
                    loading = true;
                    Log.d("loadMore", "call getPostFeed('loadMore')");
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
}
