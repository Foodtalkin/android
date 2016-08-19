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
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.foodtalk.android.R;
import in.foodtalk.android.adapter.StoreHistoryAdapter;
import in.foodtalk.android.apicall.ApiCall;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.ApiCallback;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.object.StoreHistoryObj;

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

    StoreHistoryAdapter storeHistoryAdapter;

    List<StoreHistoryObj> storeHistoryList = new ArrayList<>();

    TextView placeholder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.store_history_fragment, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);

        progressHolder = (LinearLayout) layout.findViewById(R.id.progress_h);
        tapToRetry = (LinearLayout) layout.findViewById(R.id.tap_to_retry);
        apiCallback = this;

        placeholder = (TextView) layout.findViewById(R.id.placeholder);

        tapToRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getStoreList();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
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
        progressHolder.setVisibility(View.VISIBLE);
        tapToRetry.setVisibility(View.GONE);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sessionId", db.getUserDetails().get("sessionId"));
        apiCall.apiRequestPost(getActivity(),jsonObject, Config.URL_ADWORD_REDEEMED, "storeHistory", apiCallback);
    }

    @Override
    public void apiResponse(JSONObject response, String tag) {
        Log.d("storeHistory", tag+" "+response+"");
        if (response != null){
            try {
                loadDataIntoView(response, tag);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            tapToRetry.setVisibility(View.VISIBLE);
            progressHolder.setVisibility(View.GONE);
        }
    }
    private void loadDataIntoView(JSONObject response, String tag) throws JSONException {

        //progressBarCheckin.setVisibility(View.GONE);
        if(storeHistoryList.size()>0){
            storeHistoryList.clear();
        }
        // progressBar.setVisibility(View.GONE);
        progressHolder.setVisibility(View.GONE);
        tapToRetry.setVisibility(View.GONE);




        // this.response = response;
        JSONArray listArray = response.getJSONArray("result");
        Log.d("store history fragment", "length: "+ listArray.length());
        if (listArray.length() > 0){
            placeholder.setVisibility(View.GONE);
        }else {
            placeholder.setVisibility(View.VISIBLE);
        }
        // Log.d("rListArray", "total: "+ rListArray.length());
        for (int i=0;i<listArray.length();i++){
            StoreHistoryObj current = new StoreHistoryObj();
            current.id = listArray.getJSONObject(i).getString("id");
            current.entityId = listArray.getJSONObject(i).getString("entityId");
            current.title = listArray.getJSONObject(i).getString("title");
            current.adImage = listArray.getJSONObject(i).getString("adImage");
            current.adThumb = listArray.getJSONObject(i).getString("adThumb");
            current.points = listArray.getJSONObject(i).getString("points");
            current.couponCode = listArray.getJSONObject(i).getString("couponCode");
            current.paymentUrl = listArray.getJSONObject(i).getString("paymentUrl");
            current.description = listArray.getJSONObject(i).getString("description");
            current.totalSlots = listArray.getJSONObject(i).getString("totalSlots");
            current.bookedSlots = listArray.getJSONObject(i).getString("bookedSlots");
            current.description2 = listArray.getJSONObject(i).getString("description2");
            current.expiry = listArray.getJSONObject(i).getString("expiry");
            current.rid = listArray.getJSONObject(i).getString("rid");
            current.pointsRedeemed = listArray.getJSONObject(i).getString("pointsRedeemed");
            current.iRedeemed = listArray.getJSONObject(i).getString("iRedeemed");
            current.bookedOn = listArray.getJSONObject(i).getString("bookedOn");
            current.type = listArray.getJSONObject(i).getString("type");
            storeHistoryList.add(current);
        }
        //Log.d("send list", "total: "+restaurantList.size());
        if (getActivity() != null){
            storeHistoryAdapter = new StoreHistoryAdapter(getActivity(),storeHistoryList);
            //checkInAdapter = new CheckInAdapter(getActivity(),restaurantList);
            recyclerView.setAdapter(storeHistoryAdapter);
        }
    }
}
