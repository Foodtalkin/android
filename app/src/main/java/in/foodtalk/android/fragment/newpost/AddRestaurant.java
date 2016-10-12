package in.foodtalk.android.fragment.newpost;

import android.app.Dialog;
import android.app.Fragment;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
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

import in.foodtalk.android.R;
import in.foodtalk.android.adapter.newpost.CheckInAdapter;
import in.foodtalk.android.adapter.newpost.CityListAdapter;
import in.foodtalk.android.adapter.onboarding.SelectCityAdapter;
import in.foodtalk.android.apicall.GetCitiesApi;
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.AddedRestaurantCallback;
import in.foodtalk.android.communicator.ApiCallback;
import in.foodtalk.android.communicator.CityListCallback;
import in.foodtalk.android.communicator.LatLonCallback;
import in.foodtalk.android.communicator.SelectCityCallback;
import in.foodtalk.android.constant.ConstantVar;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.GetLocation;
import in.foodtalk.android.module.StringCase;
import in.foodtalk.android.module.ToastShow;
import in.foodtalk.android.module.UserAgent;
import in.foodtalk.android.object.RestaurantListObj;
import in.foodtalk.android.object.SelectCityObj;

/**
 * Created by RetailAdmin on 03-06-2016.
 */
public class AddRestaurant extends Fragment implements LatLonCallback, ApiCallback, SelectCityCallback {

    View layout;

    EditText address;

    Switch switchIamHere;

    ApiCallback apiCallback;

    LinearLayout locationSection;
    TextView txtCity;
    Dialog dialog;

    DatabaseHandler db;
    Config config;

    LinearLayout btnCity;
    LatLonCallback latLonCallback;

    CityListAdapter cityListAdapter;

    LinearLayoutManager linearLayoutManager;

    StringCase stringCase;

    List<SelectCityObj> cityList = new ArrayList<>();
   Boolean addressSection = true;
    Boolean cityListLoaded = false;

    GetLocation getLocation;

    String lat = "";
    String lon = "";

    EditText inputRName;
    EditText inputRAddress;
    LinearLayout btnAddRestaurant;

    LinearLayout addRestaurantContainer;

    Boolean btnAddRestaurantEnable = false;

    AddedRestaurantCallback addedRestaurantCallback;

    EditText inputCity;

    LinearLayout selectCityHolder;

    String searchKey = "";

    ScrollView scrollView;

    //Boolean iAmHere = false;
    TextView txtError;
    RecyclerView recyclerView;
    Boolean btnSendIsEnabled = false;

    Boolean loadFirstTime = true;
    SelectCityAdapter selectCityAdapter;
    SelectCityCallback selectCityCallback;
    Boolean searchOnChangeText = true;
    String googlePlaceId;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.add_restaurant,container, false);
        address = (EditText) layout.findViewById(R.id.input_address_add_restaurant);

        apiCallback = this;
        selectCityCallback = this;

        locationSection = (LinearLayout) layout.findViewById(R.id.location_add_restaurant);
        //--txtCity = (TextView) layout.findViewById(R.id.txt_city_add_restaurant);
        //--btnCity = (LinearLayout) layout.findViewById(R.id.btn_city_add_restaurant);
        inputCity = (EditText) layout.findViewById(R.id.input_city);
        selectCityHolder = (LinearLayout) layout.findViewById(R.id.select_city);
        scrollView = (ScrollView) layout.findViewById(R.id.scroll_view_add_restaurant);
        txtError = (TextView) layout.findViewById(R.id.txt_error);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);


        inputCity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    Log.d("AddRestaurant", "hasFocus "+hasFocus+ " : "+selectCityHolder.getY());
                    //scrollView.scrollTo(0,(int)selectCityHolder.getY());
                }else {
                    Log.d("AddRestaurant", "hasFocus "+hasFocus);
                }
            }
        });

        addRestaurantContainer = (LinearLayout) layout.findViewById(R.id.add_restaurant_container);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        //addRestaurantContainer.setMinimumHeight(height);

        inputRName = (EditText) layout.findViewById(R.id.input_name_add_restaurant);
        inputRAddress = (EditText) layout.findViewById(R.id.input_address_add_restaurant);

        latLonCallback = this;

        addedRestaurantCallback = (AddedRestaurantCallback) getActivity();
        textListener();

        btnAddRestaurant = (LinearLayout) layout.findViewById(R.id.btn_add_restaurant);
        btnAddRestaurant.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (v.getId()){
                    case R.id.btn_add_restaurant:
                        switch (event.getAction()){
                            case MotionEvent.ACTION_UP:
                                if (btnSendIsEnabled){
                                    Log.d("clicked","add restaurant");
                                    try {
                                        addRestaurant("addRestaurant");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }else {
                                    ToastShow.showToast(getActivity(), "Restaurant and City are required");
                                }
                                break;
                        }
                        break;
                }
                return true;
            }
        });
        stringCase = new StringCase();
        //--switchIamHere = (Switch) layout.findViewById(R.id.switch_add_restaurant);

        db = new DatabaseHandler(getActivity());
        config = new Config();


        btnSendEnabled(false);
        return layout;
    }

    private void errorMsg(Boolean error, String msg){
        Log.d("errorMsg", "error show"+ error);
        if (error){
            txtError.setText(msg);
            txtError.setAlpha(1);
            //txtError.setVisibility(View.VISIBLE);
            Animation myFadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.blink_anim);
           txtError.startAnimation(myFadeInAnimation);
        }else {
           //-- txtError.setAlpha(0);
            txtError.setVisibility(View.GONE);
        }
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("AddRestaurant","onDestroyView");
        if (getLocation != null){
            getLocation.onStop();
        }

    }

    CityListCallback cityListCallback = new CityListCallback() {
        @Override
        public void selectCity(String cityName) {
            Log.d("city", cityName);
            txtCity.setText(stringCase.caseSensitive(cityName));
            dialog.dismiss();
        }
    };

    @Override
    public void location(String gpsStatus, String lat, String lon) {
        if (gpsStatus.equals(ConstantVar.LOCATION_GOT)){
            this.lat = lat;
            this.lon = lon;
        }
    }


    public void addRestaurant(final String tag) throws JSONException {

        errorMsg(true, "Please wait...");

        Log.d("getPostFeed", "post data");
        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
       // obj.put("latitude",lat);
       // obj.put("longitude",lon);
        obj.put("restaurantName",inputRName.getText().toString());
        if (!inputRAddress.getText().toString().equals("")){
            obj.put("area",inputRAddress.getText().toString());
        }
        obj.put("google_place_id", googlePlaceId);
//        obj.put("region",txtCity.getText().toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                config.URL_ADD_RESTAURANT, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d(TAG, "After Sending JsongObj"+response.toString());
                        //msgResponse.setText(response.toString());
                        Log.d("Login Respond", response.toString());
                        try {
                            String status = response.getString("status");
                            if (!status.equals("error")){
                                //Log.d("api call","restaurant added"+ response.getString("restaurantId"));
                                addedRestaurantCallback.restaurantAdded(response.getString("restaurantId"), inputRName.getText().toString());
                                errorMsg(false, "");
                                //-- getAndSave(response);
                                //loadDataIntoView(response , tag);
                            }else {
                                String errorCode = response.getString("errorCode");
                                if(errorCode.equals("6")){
                                    Log.d("Response error", "Session has expired");
                                    //logOut();
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
                //showToast("Please check your internet connection");

                if(tag.equals("refresh")){
                    //swipeRefreshHome.setRefreshing(false);
                }
                if(tag.equals("loadMore")){
                    //remove(null);
                    //callScrollClass();
                    //pageNo--;
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
                UserAgent userAgent = new UserAgent();
                if (userAgent.getUserAgent(getActivity()) != null ){
                    headers.put("User-agent", userAgent.getUserAgent(getActivity()));
                }
                return headers;
            }
        };
        final int DEFAULT_TIMEOUT = 6000;
        // Adding request to request queue
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq,"gethomefeed");
    }

    private void textListener(){
        inputRName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Log.d("beforeTextChange", s+"");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.d("onTextChanged", s+"");
                if (s.toString().length() > 2){
                    if (googlePlaceId != null){
                        btnSendEnabled(true);
                    }
                }else {
                    btnSendEnabled(false);
                }
                if (count == 0){
                    //recyclerView.setVisibility(View.GONE);
                    //btnAddRestaurant.setTextColor(getResources().getColor(R.color.btn_disable));
                    btnAddRestaurantEnable = false;
                }else {
                   // if (dishNameLoaded){
                        //onTexChange(s.toString());
                   // }
                   // recyclerView.setVisibility(View.VISIBLE);
                    if (!addressSection || inputRAddress.length()>0){
                       // btnAddRestaurant.setTextColor(getResources().getColor(R.color.btn_enable));
                        btnAddRestaurantEnable = true;
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Log.d("afterTextChange", s+"");
            }
        });

        inputRAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (count == 0){
                   // btnAddRestaurant.setTextColor(getResources().getColor(R.color.btn_disable));
                    btnAddRestaurantEnable = false;
                }else {
                    if (inputRName.length()>0){
                       // btnAddRestaurant.setTextColor(getResources().getColor(R.color.btn_enable));
                        btnAddRestaurantEnable = true;
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCity.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                scrollView.scrollTo(0,(int)selectCityHolder.getY());

                Log.d("onTextChange", s.toString()+" : count "+count);
                if (s.toString().length() > 0 ){
                    String sKey = s.toString();
                    sKey = sKey.replaceAll(" ", "%20");
                    Log.d("onTextChanged", sKey);
                    //Log.d("onTextChanged", "searchOnChangeText : "+searchOnChangeText);
                    Log.d("onTextChanged","searchKey "+searchKey+" : "+"s "+ s.toString());
                    Log.d("onTextChange", String.valueOf("boolean"+ searchKey == s.toString()));
                    if (searchKey != null){
                        if (!searchKey.equals(s.toString())){
                            GetCitiesApi.getRequest(getActivity(),sKey,apiCallback);
                            //errorMsg(true, "Searching...");
                        }
                    }
                }else {
                   //-- recyclerView.setVisibility(View.GONE);
                    //--btnSendEnabled(false);
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
            errorMsg(false, "");
            //Log.d("apiResponse - "+tag,response+"");
            errorMsg(false, "");
            if (response != null){
                try {
                    JSONArray rListArray = response.getJSONArray("predictions");
                    String status = response.getString("status");
                    if (status.equals("OK")){
                        if (inputCity.getText().length() > 1){
                            loadDataIntoView(response, tag);
                        }
                    }else if (status.equals("ZERO_RESULTS")){
                        errorMsg(true,"Result not found.");
                        recyclerView.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                ToastShow.showToast(getActivity(), "Please check your internet connection");
                btnSendEnabled(true);
            }

        }
    }
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
    private void btnSendEnabled(Boolean b){
        if (!b){
            btnAddRestaurant.setAlpha(0.5f);
            btnSendIsEnabled = false;
        }else {
            btnAddRestaurant.setAlpha(1);
            btnSendIsEnabled = true;
        }
    }

    @Override
    public void getSelectedCity(String city, String placeId) {
        Log.d("SelectCity", "PlaceId: "+ placeId);
        searchKey = city;
        searchOnChangeText = false;
        Log.d("searchOnChangeText", searchOnChangeText+"");

        errorMsg(false,"");

        inputCity.setText(city);

        googlePlaceId = placeId;
        if (inputRName.getText().length() > 2){
            btnSendEnabled(true);
        } else {
            btnSendEnabled(false);
        }

        recyclerView.setVisibility(View.GONE);
        inputCity.setSelection(inputCity.getText().length());
    }
}
