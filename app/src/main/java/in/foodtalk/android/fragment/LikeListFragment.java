package in.foodtalk.android.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.foodtalk.android.R;
import in.foodtalk.android.apicall.ApiCall;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.ApiCallback;
import in.foodtalk.android.module.DatabaseHandler;

/**
 * Created by RetailAdmin on 19-10-2016.
 */

public class LikeListFragment extends Fragment implements ApiCallback {
    View layout;
    ApiCall apiCall;
    ApiCallback apiCallback;
    DatabaseHandler db;
    public String postId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.like_list_fragment, container, false);
        apiCallback = this;
        apiCall = new ApiCall();
        db = new DatabaseHandler(getActivity());
        try {
            getLikeList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return layout;
    }

    private void getLikeList() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
        obj.put("postId", postId);
        //Log.d("LikeListFragment","obj: "+ obj);
        apiCall.apiRequestPost(getActivity(), obj, Config.URL_LIKE_LIST_POST,"likeListByPost", apiCallback);
    }

    @Override
    public void apiResponse(JSONObject response, String tag) {
        Log.d("LikeListFragment","tag: "+ tag+" respose: "+ response);
        if (tag.equals("likeListByPost")){
            try {
                loadDataIntoView(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void loadDataIntoView(JSONObject response) throws JSONException {
        JSONArray likeListArray = response.getJSONArray("likes");
    }
}
