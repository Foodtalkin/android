package in.foodtalk.android.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

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

import in.foodtalk.android.FbLogin;
import in.foodtalk.android.R;
import in.foodtalk.android.adapter.DiscoverAdapter;
import in.foodtalk.android.adapter.HomeFeedAdapter;
import in.foodtalk.android.adapter.OpenPostAdapter;
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.LatLonCallback;
import in.foodtalk.android.communicator.PostLikeCallback;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.EndlessRecyclerOnScrollListener;
import in.foodtalk.android.module.GetLocation;
import in.foodtalk.android.object.PostObj;
import in.foodtalk.android.object.UserPostObj;

/**
 * Created by RetailAdmin on 21-04-2016.
 */
public class OpenPostFragment extends Fragment implements View.OnTouchListener {


    View layout;
    DatabaseHandler db;
    Config config;
    PostObj postObj;
    HomeFeedAdapter homeFeedAdapter;

    DiscoverAdapter discoverAdapter;
    OpenPostAdapter openPostAdapter;
    List<UserPostObj> postData;

    private RecyclerView.LayoutManager mLayoutManager;

    PostLikeCallback likeCallback;

    public RecyclerView recyclerView;

    PostLikeCallback postLikeCallback;

    SwipeRefreshLayout swipeRefreshHome;

    LinearLayoutManager linearLayoutManager;

    private int pageNo = 1;

    String selectedUserId;
    String selectedPostId;

    LinearLayout progressBar;

    int dx1;
    int firstVItem;
    int lastVItem;
    Context context;

    //List<UserPostObj> postList;
    public void openPostFragment1(List<UserPostObj> postList, String postId, String userId){
        //postList.remove(0);
        //this.postData = postList;
        this.postData =  new ArrayList<UserPostObj>(postList);
        this.selectedPostId = postId;
        selectedUserId = userId;
        this.postData.remove(0);
        Log.d("postList data length", postList.size()+"");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.open_post_fragment, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view_open_post);
        progressBar = (LinearLayout) layout.findViewById(R.id.progress_bar);


        recyclerView.setOnTouchListener(this);

        //--swipeRefreshHome = (SwipeRefreshLayout) layout.findViewById(R.id.swipeRefreshHome);
        // use a linear layout manager
       // linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL);



        if(postData != null){
            Log.d("postData","size: "+ postData);
        }else {
            Log.d("postData","null");
        }


        /*--swipeRefreshHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("swip to refresh home", "Refreshing");
                try {
                    pageNo = 1;
                    getPostFeed("refresh");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });*/
        return layout;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        linearLayoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        callScrollClass();






        config = new Config();
        db = new DatabaseHandler(getActivity().getApplicationContext());
        postObj = new PostObj();
        Log.d("get user info F", db.getUserDetails().get("sessionId"));
        Log.d("get user info F", db.getUserDetails().get("userId"));

//        Log.d("get user info lat", db.getUserDetails().get("lat"));
        //Log.d("get user info lon", db.getUserDetails().get("lon"));
        //postData.remove(0);

        openPostAdapter = new OpenPostAdapter(getActivity(), postData, postLikeCallback);
        recyclerView.setAdapter(openPostAdapter);

        //recyclerView.smoothScrollToPosition(2*280);
        recyclerView.scrollToPosition(Integer.parseInt(selectedPostId)-1);

        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public void onDestroyView() {
        Log.d("Fragment","onDestryView");
        //getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d("Fragment","onDestroy");
        super.onDestroy();
    }
    @Override
    public void onDetach() {
        Log.d("Fragment","onDetach");
        super.onDetach();
    }
    Parcelable mListState;
    public void getPostFeed(final String tag) throws JSONException {
        Log.d("getPostFeed", "post data");
        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
        obj.put("includeCount", "1");
        obj.put("includeFollowed","1");
        obj.put("postUserId",db.getUserDetails().get("userId"));
        //Log.d("getPostFeed","pageNo: "+pageNo);
        obj.put("page",Integer.toString(pageNo));
        obj.put("recordCount","15");
        obj.put("selectedUserId",selectedUserId);


        Log.d("post page number param", pageNo+"");
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                config.URL_USER_POST_IMAGE, obj,
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
                VolleyLog.d("Response", "Error: " + error.getMessage());
                showToast("Please check your internet connection");

                if(tag.equals("refresh")){
                   //-- swipeRefreshHome.setRefreshing(false);
                }
                if(tag.equals("loadMore")){
                    remove(null);
                    //callScrollClass();
                    pageNo--;
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
    private void loadDataIntoView(JSONObject response , String tag) throws JSONException {

       // progressBar.setVisibility(View.GONE);

        //--swipeRefreshHome.setRefreshing(false);

        JSONArray postArray = response.getJSONArray("imagePosts");
       // JSONObject postObject = postArray.getJSONObject(0);
        //String userName = postObject.getString("userName");
        //Log.d("user name from post", userName);
        Log.d("check list array", postData.size()+"");
        if (postData.size() > 0 && !tag.equals("loadMore")){
            postData.clear();
            Log.d("loadData: ","clear post data");
        }
        for (int i=0; postArray.length()>i;i++){
            //String userName = postArray.getJSONObject(i).getString("userName");
            //PostObj current = new PostObj();
            UserPostObj current = new UserPostObj();
            current.id = postArray.getJSONObject(i).getString("id");
            current.userName = postArray.getJSONObject(i).getString("userName");
            current.userId = postArray.getJSONObject(i).getString("userId");
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
            current.rating = postArray.getJSONObject(i).getString("rating");
            //current.restaurantDistance = postArray.getJSONObject(i).getString("restaurantDistance");
            // postData.clear();
            postData.add(current);
           //Log.d("dish name", postData.get(i).userId);
        }
        //postData = (List<PostObj>) postObj;
        if (tag.equals("load")){
            if(getActivity() != null){
                openPostAdapter = new OpenPostAdapter(getActivity(), postData, postLikeCallback);
                recyclerView.setAdapter(discoverAdapter);
            }else {
                Log.d("DiscoverAdapter call", "getActivity null");
            }
            // recyclerView.invalidate();
            callScrollClass();
            Log.d("Response LoadData", "Load");
        }else if (tag.equals("refresh")){
            // discoverAdapter.clear();
            //Log.d("on update","postData size: " +postData.size());
            //discoverAdapter.addAll(postData);
            callScrollClass();
            discoverAdapter.notifyDataSetChanged();
            Log.d("Response LoadData", "Refresh - size: "+ postData.size());
        } else if(tag.equals("loadMore")){
            //postData.add(postData);
            //recyclerView.addD
            //discoverAdapter.addAll(postData);
            //postData.remove(postData.size() - 1);
            // discoverAdapter.notifyItemRemoved(postData.size()-1);
            remove(null);
            loading = false;
            Log.d("Response LoadData", "LoadMore: postSize"+postData.size()+" dishname: "+postData.get(0).dishName);
            // discoverAdapter.notifyDataSetChanged();
            //discoverAdapter.notifyItemInserted(postData.size());
        }
        //discoverAdapter.notifyDataSetChanged();
    }
    public void remove(ContactsContract.Contacts.Data data) {
        int position = postData.indexOf(data);
        postData.remove(position);
        openPostAdapter.notifyItemRemoved(position);
    }
    Boolean loading = false;
    private void callScrollClass(){
        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager, null) {
            @Override
            public void onLoadMore(int current_page) {
                Log.d("scroll listener", "current_page: "+ current_page);
                if(!loading){
                    pageNo++;
                    postData.add(null);
                    //recyclerView.addD
                    openPostAdapter.notifyItemInserted(postData.size()-1);
                    loading = true;
                    Log.d("loadMore", "call getPostFeed('loadMore')");
                    try {
                        getPostFeed("loadMore");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onScrolled1(int dx, int dy, int firstVisibleItem, int lastVisibleItem) {
                dx1 = dx;
                firstVItem = firstVisibleItem;
                lastVItem = lastVisibleItem;
               // Log.d("onScrolled called",dx+"");
                if (dx > 0){
                   // recyclerView.smoothScrollToPosition(lastVisibleItem);
                }else if(dx < 0){
                   // recyclerView.smoothScrollToPosition(firstVisibleItem);
                }
            }
        });
    }
    private void logOut(){
        db.resetTables();
        Intent i = new Intent(getActivity().getApplicationContext(), FbLogin.class);
        startActivity(i);
        getActivity().finish();
    }
    public void showToast(String msg){
        Toast toast= Toast.makeText(getActivity(),
                msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 300);
        toast.show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d("onTouch","call");
        switch (v.getId()){
            case R.id.recycler_view_open_post:
                Log.d("clicked","recycler view");
                switch (event.getAction()){

                    case MotionEvent.ACTION_DOWN:
                        Log.d("recycler view","action down");
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d("recycler view", "action up");
                        if (dx1 > 0){
                             recyclerView.smoothScrollToPosition(lastVItem);
                            int vl = lastVItem*280;
                            Log.d("value scrol", vl+"");
                           //recyclerView.scrollTo(vl, 0);
                        }else if(dx1 < 0){
                            int vl = firstVItem*280;
                          // recyclerView.scrollTo(vl, 0);
                             recyclerView.smoothScrollToPosition(firstVItem);
                        }else if (dx1 == 0){
                            int vl = lastVItem*280;
                            //recyclerView.scrollTo(vl, 0);
                            recyclerView.smoothScrollToPosition(lastVItem);
                        }
                        break;
                }
                break;
        }
        return false;
    }
}