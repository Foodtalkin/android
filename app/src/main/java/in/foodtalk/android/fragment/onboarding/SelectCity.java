package in.foodtalk.android.fragment.onboarding;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.ApiCallback;
import in.foodtalk.android.communicator.OnBoardingCallback;
import in.foodtalk.android.communicator.SelectCityCallback;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.object.RestaurantListObj;
import in.foodtalk.android.object.SelectCityObj;

/**
 * Created by RetailAdmin on 02-09-2016.
 */
public class SelectCity extends Fragment implements ApiCallback, SelectCityCallback {
    OnBoardingCallback onBoardingCallback;
    LinearLayout btnSend;
    EditText inputCity;
    ApiCallback apiCallback;
    RecyclerView recyclerView;
    List<SelectCityObj> cityList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    SelectCityAdapter selectCityAdapter;

    Boolean btnSendIsEnabled = false;
    TextView txtError;

    ApiCall apiCall;
    //ApiCallback apiCallback;
    String googlePlaceId;

    LinearLayoutManager mLayoutManager;
    SelectCityCallback selectCityCallback;
    DatabaseHandler db;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.onboarding_city, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
       /*linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);*/
        apiCallback = this;
        apiCall = new ApiCall();
        db = new DatabaseHandler(getContext());
        btnSend = (LinearLayout) layout.findViewById(R.id.btn_send);
        inputCity = (EditText) layout.findViewById(R.id.input_city);
        txtError = (TextView) layout.findViewById(R.id.txt_error);
        onBoardingCallback = (OnBoardingCallback) getActivity();
        selectCityCallback = this;
        apiCallback = this;
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SelectEmail","button clicked send");
                if (btnSendIsEnabled){
                    //onBoardingCallback.onboardingBtnClicked("previos", null, null);
                    try {
                        createUserName(AppController.userName,AppController.fbEmailId, googlePlaceId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        inputListener();


        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        return layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(mLayoutManager);
        btnSendEnabled(false);

       // mAdapter = new ListingAdapter(mListing);
        //mRecyclerView.setAdapter(mAdapter);
    }

    private void inputListener(){

        inputCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0 ){
                    String sKey = s.toString();
                    sKey = sKey.replaceAll(" ", "%20");
                    Log.d("onTextChanged", sKey);
                    GetCitiesApi.getRequest(getActivity(),sKey,apiCallback);
                    errorMsg(true, "Searching...");
                }else {
                    recyclerView.setVisibility(View.GONE);
                    btnSendEnabled(false);
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
            errorMsg(false, "");
            try {
                JSONArray rListArray = response.getJSONArray("predictions");
                String status = response.getString("status");
                if (status.equals("OK")){
                    loadDataIntoView(response, tag);

                }else if (status.equals("ZERO_RESULTS")){
                    errorMsg(true,"Result not found.");
                    recyclerView.setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (tag.equals("emailSubmit")){
            Log.d("apiResponse", "email submited");
            errorMsg(false, "");
        }
    }
    private void btnSendEnabled(Boolean b){
        if (!b){
            btnSend.setAlpha(0.5f);
            btnSendIsEnabled = false;
        }else {
            btnSend.setAlpha(1);
            btnSendIsEnabled = true;
        }
    }
    Boolean loadFirstTime = true;

    private void loadDataIntoView(JSONObject response, String tag) throws JSONException {

        //progressBarCheckin.setVisibility(View.GONE);

        // progressBar.setVisibility(View.GONE);
       // progressHolder.setVisibility(View.GONE);
        //tapToRetry.setVisibility(View.GONE);
        if (!loadFirstTime){
         cityList.clear();
        }

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
        recyclerView.setVisibility(View.VISIBLE);
        //Log.d("send list", "total: "+restaurantList.size());
        if (getActivity() != null){
            if (loadFirstTime){
                selectCityAdapter = new SelectCityAdapter(getActivity(),cityList,selectCityCallback);
                //checkInAdapter = new CheckInAdapter(getActivity(),restaurantList);
                recyclerView.setAdapter(selectCityAdapter);
                loadFirstTime = false;
                //Animation myFadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
                //recyclerView.startAnimation(myFadeInAnimation);

            }else {
                selectCityAdapter.notifyDataSetChanged();
                errorMsg(false,"");
                Log.d("SelectCity","notifyDatasetChanged");
            }
        }
    }

    @Override
    public void getSelectedCity(String city, String placeId) {
        inputCity.setText(city);
        inputCity.setSelection(inputCity.getText().length());
        Log.d("SelectCity", "PlaceId: "+ placeId);

        errorMsg(false,"");

        googlePlaceId = placeId;
        btnSendEnabled(true);
        recyclerView.setVisibility(View.GONE);
    }

    private void createUserName(String userName ,String email, String googlePlaceId)throws JSONException{

        btnSendEnabled(false);
        //btnClickable = false;
        hideKeyboard();
        JSONObject obj = new JSONObject();
        obj.put("signInType", "F");
        obj.put("fullName", db.getUserDetails().get("fullName"));
        obj.put("userName",userName);
        obj.put("facebookId",db.getUserDetails().get("facebooId"));
        obj.put("email", email);
        obj.put("google_place_id", googlePlaceId);
        //obj.put("region", city);
        obj.put("deviceToken","12344566776");
        apiCall.apiRequestPost(getActivity(), obj, Config.URL_LOGIN, "emailSubmit", apiCallback);
        errorMsg(true, "Please wait...");
    }
    private void hideKeyboard(){
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void errorMsg(Boolean error, String msg){
        Log.d("errorMsg", "error show"+ error);
        if (error){
            txtError.setText(msg);
            //txtError.setAlpha(1);
            txtError.setVisibility(View.VISIBLE);
            Animation myFadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.blink_anim);
            txtError.startAnimation(myFadeInAnimation);
        }else {
            txtError.setVisibility(View.GONE);
        }
    }
}