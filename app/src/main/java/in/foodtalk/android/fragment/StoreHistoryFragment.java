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

import org.json.JSONException;
import org.json.JSONObject;

import in.foodtalk.android.R;
import in.foodtalk.android.apicall.ApiCall;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.ApiCallback;
import in.foodtalk.android.module.DatabaseHandler;

/**
 * Created by RetailAdmin on 16-08-2016.
 */
public class StoreHistoryFragment extends Fragment implements ApiCallback {

    View layout;
    DatabaseHandler db;
    ApiCall apiCall;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    LinearLayout tapToRetry;
    LinearLayout progressHolder;

    ApiCallback apiCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.store_history_fragment, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);

        progressHolder = (LinearLayout) layout.findViewById(R.id.progress_h);
        tapToRetry = (LinearLayout) layout.findViewById(R.id.tap_to_retry);
        apiCallback = this;
        tapToRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        db = new DatabaseHandler(getActivity());
        apiCall = new ApiCall();
        try {
            getStoreList();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return layout;
    }
    private void getStoreList() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sessionId", db.getUserDetails().get("sessionId"));
        apiCall.apiRequestPost(getActivity(),jsonObject, Config.URL_ADWORD_REDEEMED, "storeHistory", apiCallback);
    }

    @Override
    public void apiResponse(JSONObject response, String tag) {
        Log.d("storeHistory", tag+" "+response+"");
    }
}
