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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import in.foodtalk.android.R;
import in.foodtalk.android.adapter.newpost.StoreAdapter;
import in.foodtalk.android.apicall.ApiCall;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.ApiCallback;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.object.StoreObj;

/**
 * Created by RetailAdmin on 12-08-2016.
 */
public class StoreFragment extends Fragment implements ApiCallback {
    View layout;
    DatabaseHandler db;
    ApiCall apiCall = new ApiCall();
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    LinearLayout tapToRetry;
    LinearLayout progressHolder;

    List<StoreObj> listStore = new ArrayList<>();

    ApiCallback apiCallback;
    StoreAdapter storeAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.store_fragment, container, false);
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
        try {
            getStoreList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        return layout;

    }

    private void getStoreList() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sessionId", db.getUserDetails().get("sessionId"));
        apiCall.apiRequestPost(getActivity(),jsonObject, Config.URL_ADWORD_LIST, "storeList", apiCallback);
    }

    @Override
    public void apiResponse(JSONObject response, String tag) {
        Log.d("apiResponse", tag+" : " +response);
        try {
            loadDataIntoView(response, tag);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void loadDataIntoView(JSONObject response, String tag) throws JSONException {

        //progressBarCheckin.setVisibility(View.GONE);
        if(listStore.size()>0){
            listStore.clear();
        }
        // progressBar.setVisibility(View.GONE);
        progressHolder.setVisibility(View.GONE);
        tapToRetry.setVisibility(View.GONE);


        // this.response = response;
        JSONArray listArray = response.getJSONArray("result");
        // Log.d("rListArray", "total: "+ rListArray.length());
        for (int i=0;i<listArray.length();i++){
            StoreObj current = new StoreObj();
            current.id = listArray.getJSONObject(i).getString("id");
            current.entityId = listArray.getJSONObject(i).getString("entityId");
            current.title = listArray.getJSONObject(i).getString("title");
            current.adImage = listArray.getJSONObject(i).getString("adImage");
            current.adThumb = listArray.getJSONObject(i).getString("adThumb");
            current.points = listArray.getJSONObject(i).getString("points");
            current.couponCode = listArray.getJSONObject(i).getString("couponCode");
            current.paymentUrl = listArray.getJSONObject(i).getString("paymentUrl");
            current.description = listArray.getJSONObject(i).getString("description");
            current.bookedSlots = listArray.getJSONObject(i).getString("bookedSlots");
            current.description2 = listArray.getJSONObject(i).getString("description2");
            current.expiry = listArray.getJSONObject(i).getString("expiry");
            current.iRedeemed = listArray.getJSONObject(i).getString("iRedeemed");
            current.type = listArray.getJSONObject(i).getString("type");
            listStore.add(current);
        }
        //Log.d("send list", "total: "+restaurantList.size());
        if (getActivity() != null){
            storeAdapter = new StoreAdapter(getActivity(),listStore);
            //checkInAdapter = new CheckInAdapter(getActivity(),restaurantList);
            recyclerView.setAdapter(storeAdapter);
        }
    }
}
