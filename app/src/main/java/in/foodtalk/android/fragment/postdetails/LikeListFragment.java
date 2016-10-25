package in.foodtalk.android.fragment.postdetails;

//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.foodtalk.android.R;
import in.foodtalk.android.adapter.postdetail.LikeListPostAdapter;
import in.foodtalk.android.apicall.ApiCall;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.ApiCallback;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.object.LikeListObj;

/**
 * Created by RetailAdmin on 19-10-2016.
 */

public class LikeListFragment extends Fragment implements ApiCallback {
    View layout;
    ApiCall apiCall;
    ApiCallback apiCallback;
    DatabaseHandler db;
    List<LikeListObj> likeList = new ArrayList<>();
    LikeListPostAdapter likeListPostAdapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    String sessionId;
    String postId;
    TextView txtPlaceholder;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.post_likes_fragment, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        apiCallback = this;
        apiCall = new ApiCall();
        db = new DatabaseHandler(getActivity());
        txtPlaceholder = (TextView) layout.findViewById(R.id.txt_placeholder);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Log.d("LikeListFragment","sIS "+ bundle.getString("postId")+" : "+bundle.getString("sessionId"));
            postId = bundle.getString("postId");
            sessionId = bundle.getString("sessionId");
        }

        try {
            getLikeList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return layout;
    }

    private void getLikeList() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("sessionId", sessionId);
        obj.put("postId", postId);
        //Log.d("LikeListFragment","obj: "+ obj);
        apiCall.apiRequestPost(getActivity(), obj, Config.URL_LIKE_LIST_POST,"likeListByPost", apiCallback);
    }

    @Override
    public void apiResponse(JSONObject response, String tag) {
        Log.d("LikeListFragment","tag: "+ tag+" respose: "+ response);
        if (tag.equals("likeListByPost") && response != null){
            try {
                loadDataIntoView(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void loadDataIntoView(JSONObject response) throws JSONException {
        JSONArray likeListArray = response.getJSONArray("likes");
        if (likeListArray.length() == 0){
            txtPlaceholder.setVisibility(View.VISIBLE);
            txtPlaceholder.setText("No Likes");
        }else {
            txtPlaceholder.setVisibility(View.GONE);
        }
        for (int i=0; i<likeListArray.length(); i++){
            LikeListObj current = new LikeListObj();
            current.id = likeListArray.getJSONObject(i).getString("id");
            current.userName = likeListArray.getJSONObject(i).getString("userName");
            current.fullName = likeListArray.getJSONObject(i).getString("fullName");
            if (!current.id.equals(db.getUserDetails().get("userId"))){
                current.iFollowIt = likeListArray.getJSONObject(i).getString("iFollowIt");
            }
            current.image = likeListArray.getJSONObject(i).getString("image");
            likeList.add(current);
        }
        likeListPostAdapter = new LikeListPostAdapter(getActivity(), likeList);
        recyclerView.setAdapter(likeListPostAdapter);
    }
}
