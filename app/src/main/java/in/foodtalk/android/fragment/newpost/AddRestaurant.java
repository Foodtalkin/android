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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.util.Map;

import in.foodtalk.android.R;
import in.foodtalk.android.adapter.newpost.CheckInAdapter;
import in.foodtalk.android.adapter.newpost.CityListAdapter;
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.AddedRestaurantCallback;
import in.foodtalk.android.communicator.CityListCallback;
import in.foodtalk.android.communicator.LatLonCallback;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.GetLocation;
import in.foodtalk.android.module.StringCase;
import in.foodtalk.android.module.UserAgent;
import in.foodtalk.android.object.RestaurantListObj;

/**
 * Created by RetailAdmin on 03-06-2016.
 */
public class AddRestaurant extends Fragment implements LatLonCallback {

    View layout;

    EditText address;

    Switch switchIamHere;

    LinearLayout locationSection;
    TextView txtCity;
    Dialog dialog;

    DatabaseHandler db;
    Config config;

    LinearLayout btnCity;
    LatLonCallback latLonCallback;

    CityListAdapter cityListAdapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    StringCase stringCase;

    ArrayList<String> cityList;
   Boolean addressSection = true;
    Boolean cityListLoaded = false;

    GetLocation getLocation;

    String lat = "";
    String lon = "";

    EditText inputRName;
    EditText inputRAddress;
    TextView btnAddRestaurant;

    LinearLayout addRestaurantContainer;

    Boolean btnAddRestaurantEnable = false;

    AddedRestaurantCallback addedRestaurantCallback;

    //Boolean iAmHere = false;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.add_restaurant,container, false);
        address = (EditText) layout.findViewById(R.id.input_address_add_restaurant);

        locationSection = (LinearLayout) layout.findViewById(R.id.location_add_restaurant);
        txtCity = (TextView) layout.findViewById(R.id.txt_city_add_restaurant);
        btnCity = (LinearLayout) layout.findViewById(R.id.btn_city_add_restaurant);

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

        btnAddRestaurant = (TextView) layout.findViewById(R.id.btn_add_restaurant);
        btnAddRestaurant.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (v.getId()){
                    case R.id.btn_add_restaurant:
                        switch (event.getAction()){
                            case MotionEvent.ACTION_UP:
                                if (btnAddRestaurantEnable){
                                    Log.d("clicked","add restaurant");
                                    try {
                                        addRestaurant("addRestaurant");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;
                        }
                        break;
                }
                return true;
            }
        });
        stringCase = new StringCase();
        switchIamHere = (Switch) layout.findViewById(R.id.switch_add_restaurant);

        db = new DatabaseHandler(getActivity());
        config = new Config();

        try {
            getCityList("load");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnCity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("touch","call");
                switch (v.getId()){
                    case R.id.btn_city_add_restaurant:
                        switch (event.getAction()){
                            case MotionEvent.ACTION_UP:

                                if (cityListLoaded){
                                    cityShow();
                                }else {
                                    String errorMessage = "Please wait, Cities is loading";
                                    Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
                                    toast.show();

                                }
                                Log.d("onTouch","select city");

                                //Please wait for a Cites to load
                                break;
                        }
                        break;
                }
                return true;
            }
        });
        switchIamHere.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Log.d("switch is Checked", isChecked+"");
                    //address.setClickable(false);
                    address.setEnabled(false);
                    locationSection.setAlpha((float) 0.2);

                    getLocation = new GetLocation(getActivity(), latLonCallback);
                    getLocation.onStart();

                    addressSection = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                       // address.setBackground(getResources().getDrawable(R.drawable.round_bg_gray));
                    }else {
                       // address.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_bg_gray));
                    }

                        if (inputRName.length()>0){
                            btnAddRestaurant.setTextColor(getResources().getColor(R.color.btn_enable));
                            btnAddRestaurantEnable = true;
                        }





                }
                else {
                    if (inputRName.length()>0 && inputRAddress.length()>0){
                        btnAddRestaurant.setTextColor(getResources().getColor(R.color.btn_enable));
                        btnAddRestaurantEnable = true;
                    }else {
                        btnAddRestaurant.setTextColor(getResources().getColor(R.color.btn_disable));
                        btnAddRestaurantEnable = false;
                    }
                    addressSection = true;
                    locationSection.setAlpha((float) 1);
                    address.setEnabled(true);
                    Log.d("switch is Checked", isChecked+"");
                }
            }
        });

       /* address.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
                return false;
            }
        });*/
        return layout;
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

    public void getCityList(final String tag) throws JSONException {

        Log.d("getPostFeed", "post data");
        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
        //obj.put("latitude",lat);
        //obj.put("longitude",lon);
        //obj.put("includeCount", "1");
        //obj.put("includeFollowed","1");
        //obj.put("postUserId",db.getUserDetails().get("userId"));
        //Log.d("getPostFeed","pageNo: "+pageNo);
        //obj.put("page",Integer.toString(pageNo));
        // obj.put("recordCount","10");
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                config.URL_REGION_LIST, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d(TAG, "After Sending JsongObj"+response.toString());
                        //msgResponse.setText(response.toString());
                        Log.d("Login Respond", response.toString());
                        try {
                            String status = response.getString("status");
                            if (!status.equals("error")){
                                //-- getAndSave(response);

                                loadDataIntoView(response , tag);
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

    private void loadDataIntoView(JSONObject response, String tag) throws JSONException {

        cityListLoaded = true;

       // progressBarCheckin.setVisibility(View.GONE);

       // this.response = response;
        //String[] cityList = new String[];

        cityList = new ArrayList<String>();

        JSONArray rListArray = response.getJSONArray("regions");
        // Log.d("rListArray", "total: "+ rListArray.length());
        for (int i=0;i<rListArray.length();i++){
          //  RestaurantListObj current = new RestaurantListObj();
           // current.id = rListArray.getJSONObject(i).getString("id");
          //  current.area = rListArray.getJSONObject(i).getString("area");
          //  current.restaurantName = rListArray.getJSONObject(i).getString("restaurantName");
           // restaurantList.add(current);


            cityList.add(rListArray.getJSONObject(i).getString("name"));
        }
        //Log.d("send list", "total: "+restaurantList.size());

    }


    private void cityShow(){
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_city_list);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.verticalMargin = 100;
        lp.gravity = Gravity.CENTER;

       // dialog.getWindow().setAttributes(lp);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView = (RecyclerView) dialog.findViewById(R.id.recycler_view_city_list);

        recyclerView.setLayoutManager(layoutManager);

        cityListAdapter = new CityListAdapter(getActivity(),cityList, cityListCallback);
        recyclerView.setAdapter(cityListAdapter);
        dialog.show();
    }
    @Override
    public void location(String lat, String lon) {
        this.lat = lat;
        this.lon = lon;
    }


    public void addRestaurant(final String tag) throws JSONException {

        Log.d("getPostFeed", "post data");
        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
        obj.put("latitude",lat);
        obj.put("longitude",lon);
        obj.put("restaurantName",inputRName.getText().toString());
        obj.put("address",inputRAddress.getText().toString());
        obj.put("region",txtCity.getText().toString());
        //obj.put("includeCount", "1");
        //obj.put("includeFollowed","1");
        //obj.put("postUserId",db.getUserDetails().get("userId"));
        //Log.d("getPostFeed","pageNo: "+pageNo);
        //obj.put("page",Integer.toString(pageNo));
        // obj.put("recordCount","10");
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
                                addedRestaurantCallback.restaurantAdded(response.getString("restaurantId"));
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
                if (count == 0){
                    //recyclerView.setVisibility(View.GONE);
                    btnAddRestaurant.setTextColor(getResources().getColor(R.color.btn_disable));
                    btnAddRestaurantEnable = false;
                }else {
                   // if (dishNameLoaded){
                        //onTexChange(s.toString());
                   // }
                   // recyclerView.setVisibility(View.VISIBLE);
                    if (!addressSection || inputRAddress.length()>0){
                        btnAddRestaurant.setTextColor(getResources().getColor(R.color.btn_enable));
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
                    btnAddRestaurant.setTextColor(getResources().getColor(R.color.btn_disable));
                    btnAddRestaurantEnable = false;
                }else {
                    if (inputRName.length()>0){
                        btnAddRestaurant.setTextColor(getResources().getColor(R.color.btn_enable));
                        btnAddRestaurantEnable = true;
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
