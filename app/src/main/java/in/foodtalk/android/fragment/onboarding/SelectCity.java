package in.foodtalk.android.fragment.onboarding;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.foodtalk.android.R;
import in.foodtalk.android.adapter.CuratedAdapter;
import in.foodtalk.android.adapter.onboarding.SelectCityAdapter;
import in.foodtalk.android.apicall.ApiCall;
import in.foodtalk.android.apicall.GetCitiesApi;
import in.foodtalk.android.communicator.ApiCallback;
import in.foodtalk.android.communicator.OnBoardingCallback;
import in.foodtalk.android.object.RestaurantListObj;
import in.foodtalk.android.object.SelectCityObj;

/**
 * Created by RetailAdmin on 02-09-2016.
 */
public class SelectCity extends Fragment implements ApiCallback {
    OnBoardingCallback onBoardingCallback;
    LinearLayout btnSend;
    EditText inputCity;
    ApiCallback apiCallback;
    RecyclerView recyclerView;
    List<SelectCityObj> cityList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    SelectCityAdapter selectCityAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.onboarding_city, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        btnSend = (LinearLayout) layout.findViewById(R.id.btn_send);
        inputCity = (EditText) layout.findViewById(R.id.input_city);
        onBoardingCallback = (OnBoardingCallback) getActivity();
        apiCallback = this;
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SelectEmail","button clicked send");
                onBoardingCallback.onboardingBtnClicked("previos", null, null);
            }
        });
        inputListener();
        return layout;
    }
    private void inputListener(){
        inputCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0 ){
                    Log.d("onTextChanged", s+"");
                    GetCitiesApi.getRequest(getActivity(),s.toString(),apiCallback);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    @Override
    public void apiResponse(JSONObject response, String tag) {
        if (tag.equals("googleApiCities")){
            Log.d("apiResponse - "+tag,response+"");
            try {
                loadDataIntoView(response, tag);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadDataIntoView(JSONObject response, String tag) throws JSONException {

        //progressBarCheckin.setVisibility(View.GONE);

        // progressBar.setVisibility(View.GONE);
       // progressHolder.setVisibility(View.GONE);
        //tapToRetry.setVisibility(View.GONE);


        // this.response = response;
        JSONArray rListArray = response.getJSONArray("predictions");
        // Log.d("rListArray", "total: "+ rListArray.length());
        for (int i=0;i<rListArray.length();i++){
            SelectCityObj current = new SelectCityObj();


            current.id = rListArray.getJSONObject(i).getString("id");
            current.description = rListArray.getJSONObject(i).getString("description");
            current.place_id = rListArray.getJSONObject(i).getString("place_id");
            cityList.add(current);
        }
        //Log.d("send list", "total: "+restaurantList.size());
        if (getActivity() != null){
            selectCityAdapter = new SelectCityAdapter(getActivity(),cityList);
            //checkInAdapter = new CheckInAdapter(getActivity(),restaurantList);
            recyclerView.setAdapter(selectCityAdapter);
        }
    }
}
