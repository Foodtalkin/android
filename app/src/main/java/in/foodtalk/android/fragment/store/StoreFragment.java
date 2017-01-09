package in.foodtalk.android.fragment.store;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.foodtalk.android.R;
import in.foodtalk.android.adapter.StoreAdapter;
import in.foodtalk.android.apicall.ApiCall;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.ApiCallback;
import in.foodtalk.android.communicator.OpenFragmentCallback;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.StringCase;
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

    public TextView title;

    ApiCallback apiCallback;
    StoreAdapter storeAdapter;
    TextView placeholder;

    ImageView btnPurchases;

    //------user object-----
    ImageView imgUserThumb;
    TextView txtFullName, txtUsername, txtEventInfo, txtPoints;

    OpenFragmentCallback openFragmentCallback;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.store_fragment, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
        progressHolder = (LinearLayout) layout.findViewById(R.id.progress_h);
        tapToRetry = (LinearLayout) layout.findViewById(R.id.tap_to_retry);
        placeholder = (TextView) layout.findViewById(R.id.placeholder);

        imgUserThumb = (ImageView) layout.findViewById(R.id.img_user_thumb);
        txtFullName = (TextView) layout.findViewById(R.id.txt_fullname);
        txtUsername = (TextView) layout.findViewById(R.id.txt_username);
        txtEventInfo = (TextView) layout.findViewById(R.id.txt_event_info);
        txtPoints = (TextView) layout.findViewById(R.id.txt_pts);

        openFragmentCallback = (OpenFragmentCallback) getActivity();

        btnPurchases = (ImageView) layout.findViewById(R.id.btn_purchases);
        btnPurchases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("storeFragment", "btnPurchanse");
                
                openFragmentCallback.openFragment("storePurchases", "");
            }
        });


        apiCallback = this;
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
        progressHolder.setVisibility(View.VISIBLE);
        tapToRetry.setVisibility(View.GONE);
        placeholder.setVisibility(View.GONE);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sessionId", db.getUserDetails().get("sessionId"));
        apiCall.apiRequestPost(getActivity(),jsonObject, Config.URL_STORE_LIST, "storeList", apiCallback);
    }
    @Override
    public void apiResponse(JSONObject response, String tag) {
        Log.d("apiResponse", tag+" : " +response);
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
        if(listStore.size()>0){
            listStore.clear();
        }
        // progressBar.setVisibility(View.GONE);
        progressHolder.setVisibility(View.GONE);
        tapToRetry.setVisibility(View.GONE);

        setUserData(response.getJSONObject("profile"));


        // this.response = response;
        JSONArray listArray = response.getJSONArray("storeItems");

        if (listArray.length() == 0){
            placeholder.setVisibility(View.VISIBLE);
        }

        // Log.d("rListArray", "total: "+ rListArray.length());
        for (int i=0;i<listArray.length();i++){
            //Log.d("store frag", listArray.getJSONObject(i).getString("type")+"");
            StoreObj current = new StoreObj();
            current.storeItemId = listArray.getJSONObject(i).getString("storeItemId");
            current.title = listArray.getJSONObject(i).getString("title");
            current.coverImage = listArray.getJSONObject(i).getString("coverImage");
            current.cardImage = listArray.getJSONObject(i).getString("cardImage");
            current.actionButtonText = listArray.getJSONObject(i).getString("actionButtonText");
            current.description = listArray.getJSONObject(i).getString("description");
            current.cardActionButtonText = listArray.getJSONObject(i).getString("cardActionButtonText");
            current.shortDescription = listArray.getJSONObject(i).getString("shortDescription");
            current.costType = listArray.getJSONObject(i).getString("costType");
            current.costOnline = listArray.getJSONObject(i).getString("costOnline");
            current.costPoints = listArray.getJSONObject(i).getString("costPoints");
            current.termConditionsLink = listArray.getJSONObject(i).getString("termConditionsLink");
            current.type = listArray.getJSONObject(i).getString("type");
            current.thankYouText = listArray.getJSONObject(i).getString("thankYouText");

            current.postPurchaseInstructions = listArray.getJSONObject(i).getString("postPurchaseInstructions");
            current.startDate = listArray.getJSONObject(i).getString("startDate");
            current.endDate = listArray.getJSONObject(i).getString("endDate");
            current.cityText= listArray.getJSONObject(i).getString("cityText");



           // title.setText(String.valueOf((long)Double.parseDouble(current.avilablePoints))+" Points");
            listStore.add(current);
        }


        //Log.d("send list", "total: "+restaurantList.size());
        if (getActivity() != null){
            storeAdapter = new StoreAdapter(getActivity(),listStore);
            //checkInAdapter = new CheckInAdapter(getActivity(),restaurantList);
            recyclerView.setAdapter(storeAdapter);
        }
    }
    private void setUserData(JSONObject profile) throws JSONException {
        Picasso.with(getActivity())
                .load(profile.getString("image"))
                .fit()
                .placeholder(R.drawable.placeholder)
                .into(imgUserThumb);
        txtFullName.setText(StringCase.caseSensitive(profile.getString("fullName")));
        txtUsername.setText(profile.getString("userName"));
        double point = Double.parseDouble(profile.getString("avilablePoints"));
        if (point < 1 ){
            txtPoints.setText("0");
        }else {
            txtPoints.setText(String.valueOf((long) point));
        }
        //txtEventInfo.setText(profile.getString(""));
    }
}
