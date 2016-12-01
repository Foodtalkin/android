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
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.cloudinary.Api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.foodtalk.android.R;
import in.foodtalk.android.adapter.FollowListAdapter;
import in.foodtalk.android.adapter.FollowedListAdapter;
import in.foodtalk.android.adapter.postdetail.LikeListPostAdapter;
import in.foodtalk.android.apicall.ApiCall;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.ApiCallback;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.object.FollowListObj;
import in.foodtalk.android.object.LikeListObj;

/**
 * Created by RetailAdmin on 28-11-2016.
 */

public class FollowListFragment extends Fragment implements ApiCallback {

    View layout;

    ProgressBar progressBar;
    DatabaseHandler db;
    String userId;
    ApiCall apiCall;
    ApiCallback apiCallback;
    String listType;
    LinearLayout tapToRetry;

    List<FollowListObj> followList = new ArrayList<>();
    LikeListPostAdapter likeListPostAdapter;
    FollowListAdapter followListAdapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    String loginUserId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.follow_list_fragment, container, false);
        progressBar = (ProgressBar) layout.findViewById(R.id.progress_bar);
        tapToRetry = (LinearLayout) layout.findViewById(R.id.tap_to_retry);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        apiCall = new ApiCall();
        apiCallback = this;

        db = new DatabaseHandler(getActivity());
        loginUserId = db.getUserDetails().get("userId");
        if (getArguments() != null){
            listType = getArguments().getString("listType");
            userId = getArguments().getString("userId");
            //Log.d("FollowListFragment", getArguments().getString("listType")+" Id"+getArguments().getString("userId"));
            try {
                getFollowList(getArguments().getString("userId"),getArguments().getString("listType"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        tapToRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getFollowList(userId, listType);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return layout;
    }

    private void getFollowList(String userId, String listType) throws JSONException {
        tapToRetry.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
        obj.put("selectedUserId", userId);
        if (listType.equals("followers")){
            apiCall.apiRequestPost(getActivity(),obj, Config.URL_LIST_FOLLOWER,listType,apiCallback);
        }else if (listType.equals("following")){
            apiCall.apiRequestPost(getActivity(),obj, Config.URL_LIST_FOLLOWED,listType,apiCallback);
        }
    }
    private void loadDataIntoView(JSONObject response, String tag) throws JSONException {

        JSONArray likeListArray = (tag.endsWith("followers"))? response.getJSONArray("followers") : response.getJSONArray("followedUsers") ;
        if (likeListArray.length() == 0){
           // txtPlaceholder.setVisibility(View.VISIBLE);
           // txtPlaceholder.setText("No bookmarks");
        }else {
           // txtPlaceholder.setVisibility(View.GONE);
        }
        for (int i=0; i<likeListArray.length(); i++){
            FollowListObj current = new FollowListObj();
            current.id = likeListArray.getJSONObject(i).getString("id");
            current.userName = likeListArray.getJSONObject(i).getString("userName");
            current.fullName = likeListArray.getJSONObject(i).getString("fullName");
            if (!current.id.equals(loginUserId) && tag.endsWith("followers")){
                current.iFollowIt = likeListArray.getJSONObject(i).getString("iFollowIt");
            }
            current.image = likeListArray.getJSONObject(i).getString("image");
            followList.add(current);
        }
        if (getActivity()!= null){
            followListAdapter = new FollowListAdapter(getActivity(), followList, tag);
            recyclerView.setAdapter(followListAdapter);
        }
    }
    @Override
    public void apiResponse(JSONObject response, String tag) {
        progressBar.setVisibility(View.GONE);
        if (tag.equals(listType) && response != null){
            try {
                loadDataIntoView(response, tag);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (response == null){
            tapToRetry.setVisibility(View.VISIBLE);
        }
        Log.d("apiRespose","tag "+tag+" response: "+response);
    }
}
