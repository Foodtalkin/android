package in.foodtalk.android.fragment.newpost;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.foodtalk.android.R;
import in.foodtalk.android.adapter.newpost.CheckInAdapter;
import in.foodtalk.android.adapter.newpost.DishTaggingAdapter;
import in.foodtalk.android.apicall.ApiCall;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.AddRestaurantCallback;
import in.foodtalk.android.communicator.ApiCallback;
import in.foodtalk.android.communicator.CheckInCallback;
import in.foodtalk.android.communicator.DishTaggingCallback;
import in.foodtalk.android.communicator.LatLonCallback;
import in.foodtalk.android.communicator.ShareNewPostCallback;
import in.foodtalk.android.constant.ConstantVar;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.GetLocation;
import in.foodtalk.android.module.StringCase;
import in.foodtalk.android.module.ToastShow;
import in.foodtalk.android.object.DishListObj;
import in.foodtalk.android.object.RestaurantListObj;

/**
 * Created by RetailAdmin on 22-09-2016.
 */
public class NewPostShare extends Fragment implements View.OnTouchListener, ApiCallback, DishTaggingCallback, LatLonCallback, CheckInCallback {

    View layout;
    public Bitmap photo;
    public String checkInRestaurantName;
    //public String checkInRestaurantId;

    ImageView imgHolder;
    ScrollView scrollView;
    EditText inputTip;
    public TextView rName;
    TextView txtAddDish;
    public RelativeLayout dishSearch;
    public RelativeLayout restaurantSearch;

    LinearLayout lableAddDish;
    LinearLayout lableCheckin;

    EditText inputDishSearch;
    EditText inputRestaurantSearch;


    public Boolean searchView = false;
    public Boolean restaurantSearchView = false;

    List<DishListObj> dishList = new ArrayList<>();
    List<RestaurantListObj> restaurantList = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView recyclerViewRestaurant;

    ApiCall apiCall;
    DatabaseHandler db;
    ApiCallback apiCallback;

    DishTaggingAdapter dishTaggingAdapter;
    CheckInAdapter checkInAdapter;
    DishTaggingCallback dishTaggingCallback;
    CheckInCallback checkInCallback;

    LinearLayoutManager linearLayoutManager;
    LinearLayoutManager linearLayoutManager1;

    Boolean dishNameLoaded = false;

    JSONObject response;
    JSONObject responseRList;

    String lat;
    String lon;
    GetLocation getLocation;
    LatLonCallback latLonCallback;

    String gpsLocationOn = "notSet";

    LinearLayout gpsAlertMsg;

    LinearLayout btnAddDishName;
    TextView txtNewDish;

    String newDishName;

    LinearLayout placeHolderNoRestaurant;
    TextView txtAddRestaurant;

    ShareNewPostCallback shareNewPostCallback;

    LinearLayout btnShare;

    public String restaurantId, dishName, tip;
    //public String restaurantName;
    String rating1;

    Boolean btnShareIsEnable = false;

    RatingBar ratingBar;
    AddRestaurantCallback addRestaurantCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.new_post_share,container,false);
        imgHolder = (ImageView) layout.findViewById(R.id.img_holder);
        inputTip = (EditText) layout.findViewById(R.id.input_tip);
        scrollView = (ScrollView) layout.findViewById(R.id.scroll_view);
        final View activityRootView = layout.findViewById(R.id.activityRoot);
        rName = (TextView) layout.findViewById(R.id.txt_rName);
        dishSearch = (RelativeLayout) layout.findViewById(R.id.dish_search);
        restaurantSearch = (RelativeLayout) layout.findViewById(R.id.restaurant_search);
        gpsAlertMsg = (LinearLayout) layout.findViewById(R.id.gps_alert_msg);
        btnShare = (LinearLayout) layout.findViewById(R.id.btn_post_share);

        ratingBar = (RatingBar) layout.findViewById(R.id.ratingBar1);

        placeHolderNoRestaurant = (LinearLayout) layout.findViewById(R.id.place_holder_no_restaurant);
        txtAddRestaurant = (TextView) layout.findViewById(R.id.txt_add_restaurant);

        lableAddDish = (LinearLayout) layout.findViewById(R.id.lable_add_dish);
        lableCheckin = (LinearLayout) layout.findViewById(R.id.lable_checkin);
        txtAddDish = (TextView) layout.findViewById(R.id.txt_add_dish);

        btnAddDishName = (LinearLayout) layout.findViewById(R.id.btn_add_dish_name);
        txtNewDish = (TextView) layout.findViewById(R.id.txt_new_dish);

        inputDishSearch = (EditText) layout.findViewById(R.id.input_dish_search);
        inputRestaurantSearch = (EditText) layout.findViewById(R.id.input_restaurant_search);

        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
        recyclerViewRestaurant = (RecyclerView) layout.findViewById(R.id.recycler_view_restaurant);

        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        linearLayoutManager1 = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewRestaurant.setLayoutManager(linearLayoutManager1);

        addRestaurantCallback = (AddRestaurantCallback) getActivity();

        apiCall = new ApiCall();
        db = new DatabaseHandler(getActivity());
        apiCallback = this;
        dishTaggingCallback = this;
        checkInCallback = this;

        shareNewPostCallback = (ShareNewPostCallback) getActivity();

        lableAddDish.setOnTouchListener(this);
        lableCheckin.setOnTouchListener(this);
        btnAddDishName.setOnTouchListener(this);
        gpsAlertMsg.setOnTouchListener(this);
        btnShare.setOnTouchListener(this);
        txtAddRestaurant.setOnTouchListener(this);

        Log.d("NewPOstShare","rName: "+checkInRestaurantName);
        if (!checkInRestaurantName.equals("")){
            rName.setText(StringCase.caseSensitive(checkInRestaurantName));
        }else {
            rName.setText("CheckIn");
        }
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (getActivity() != null){
                    if (heightDiff > dpToPx(getActivity(), 200)) { // if more than 200 dp, it's probably a keyboard...
                        // ... do something here
                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.scrollTo(0, 300);
                            }
                        });
                    }
                }
            }
        });
        if (photo != null){
            imgHolder.setImageBitmap(photo);
        }
        if (dishName != null){
            txtAddDish.setText(StringCase.caseSensitive(dishName));
        }
        if (rating1 != null){
            ratingBar.setRating(Float.parseFloat(rating1));
        }
        if (tip != null){
            inputTip.setText(tip);
        }
        if (rating1 == null || rating1.equals("0") || dishName == null){
            shareBtnEnable(false);
        }else {
            shareBtnEnable(true);
        }
        focusListener();
        textListener();

        latLonCallback = this;
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            Boolean a = false;
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                if (fromUser){
                    rating1 =  Integer.toString((int)rating);
                    if (dishName != null && rating1 != null && !rating1.equals("0")){
                        shareBtnEnable(true);
                    }else {
                        shareBtnEnable(false);
                    }
                    //ratingCallback.goforReview(Integer.toString((int)rating));
                }
            }
        });
        //fordishSearch();
        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getLocation.onStop();
    }
    @Override
    public void onResume() {
        super.onResume();
        Fragment currentFragment = this.getFragmentManager().findFragmentById(R.id.container1);
        if (currentFragment == this){
            Log.d("NewPostShare","getLocation");
            getLocation = new GetLocation(getActivity(), latLonCallback, "newPostShare");
            getLocation.onStart();
        }
        if (searchView){
            showKeyBoard();
        }
    }



    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    private void focusListener(){
        inputTip.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    Log.d("NewPostShare","onFocus");

                    //scrollView.scrollTo(0,800);
                }else {
                    Log.d("NewPostShare","offFocus");
                }
            }
        });
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()){
            case R.id.lable_add_dish:
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        dishSearch.setVisibility(View.VISIBLE);
                        inputDishSearch.requestFocus();
                        searchView = true;
                        showKeyBoard();
                        //fordishSearch();
                        break;
                }
                break;
            case R.id.lable_checkin:
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        restaurantSearch.setVisibility(View.VISIBLE);
                        if (inputTip.getText().length() > 0){
                            tip = inputTip.getText().toString();
                        }
                        //showKeyBoard();
                        restaurantSearchView = true;
                        if (gpsLocationOn.equals("on")){
                            gpsAlertMsg.setVisibility(View.GONE);
                        }else if (gpsLocationOn.equals("off")){
                            gpsAlertMsg.setVisibility(View.VISIBLE);
                        }
                        break;
                }
                break;
            case R.id.btn_add_dish_name:
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        hideSoftKeyboard();
                        txtAddDish.setText(StringCase.caseSensitive(newDishName));
                        Log.d("NewPostShare",newDishName);
                        this.dishName = newDishName;
                        dishSearch.setVisibility(View.GONE);
                        searchView = false;
                        break;
                }
                break;
            case R.id.gps_alert_msg:
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        Log.d("click","gps alert msg");
                        getLocation = new GetLocation(getActivity(), latLonCallback, "newPostShareGps");
                        getLocation.onStart();
                        break;
                }
                break;
            case R.id.btn_post_share:
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        Log.d("NewPostShare","dishName: "+ dishName+" rating1:"+ rating1+ "btnShareIsEnable: "+btnShareIsEnable);
                        if (rating1 != null || dishName != null){
                            if (dishName != null){
                                if (rating1 != null && !rating1.equals("0")){
                                    Log.d("NewPostShare","posing...");
                                    shareNewPostCallback.shareNewPost(restaurantId, dishName, rating1,inputTip.getText().toString());
                                }else {
                                    ToastShow.showToast(getActivity(), "Rating is required");
                                }
                            }else {
                                if (rating1 != null && !rating1.equals("0")){
                                    ToastShow.showToast(getActivity(), "Dish Name is required");
                                }else {
                                    ToastShow.showToast(getActivity(), "Dish Name and Rating are required");
                                }
                            }
                        }else {
                            ToastShow.showToast(getActivity(), "Dish Name and Rating required");
                        }
                        break;
                }
                break;
            case R.id.txt_add_restaurant:
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        Log.d("NewPostShare","addRestaurant");
                        addRestaurantCallback.addNewRestaurant(inputRestaurantSearch.getText().toString());
                        restaurantSearchView = false;
                        searchView = false;
                        break;
                }
                break;
        }
        return true;
    }

    private void shareBtnEnable(boolean btn){
        if (!btn){
            btnShare.setAlpha(.5f);
            btnShareIsEnable = false;
        }else {
            btnShare.setAlpha(1);
            btnShareIsEnable = true;
        }

    }

    private void textListener(){
        inputDishSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Log.d("beforeTextChange", s+"");
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("onTextChanged", count+"");
                if (count == 0){
                   // recyclerView.setVisibility(View.GONE);
                    //btnNext.setTextColor(getResources().getColor(R.color.btn_disable));
                   // btnNextEnable = false;
                }else {
                    if (s.toString().length()>1){
                        try {
                            getDishList(s.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (s.toString().length() > 2){
                        btnAddDishName.setVisibility(View.VISIBLE);
                        txtNewDish.setText("Add "+s.toString());
                        newDishName = s.toString();
                    }else {
                        btnAddDishName.setVisibility(View.GONE);
                    }

                    /*if (dishNameLoaded){
                        onTexChange(s.toString());
                    }*/
                    //recyclerView.setVisibility(View.VISIBLE);
                    Log.d("NewPostShare","onTextChange: "+s.toString());
                    //btnNext.setTextColor(getResources().getColor(R.color.btn_enable));
                    //btnNextEnable = true;
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                //Log.d("afterTextChange", s+"");
            }
        });
        inputRestaurantSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Log.d("beforeTextChange", s+"");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("onTextChanged", s.length()+"");
                if (s.length() == 0){
                    // recyclerView.setVisibility(View.GONE);
                    //btnNext.setTextColor(getResources().getColor(R.color.btn_disable));
                    // btnNextEnable = false;
                    try {
                        if (lat != null && !lat.equals("")){
                            getRestaurantList(lat, lon);
                            Log.d("onTextChanged", "call with lat lon");
                        }else {
                            Log.d("onTextChanged", "call with -- -- ");
                            getRestaurantList("", "");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {

                    //recyclerView.setVisibility(View.VISIBLE);
                    try {
                        getRestaurantListSearch(s.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("NewPostShare","onTextChange: "+s.toString());
                    //btnNext.setTextColor(getResources().getColor(R.color.btn_enable));
                    //btnNextEnable = true;
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                //Log.d("afterTextChange", s+"");
            }
        });

    }

    private void getDishList(String keyword) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
        obj.put("searchText", keyword);
        obj.put("type", "dish");
        //apiCall.apiRequestPost(getActivity(),obj, Config.URL_DISH_NAME, "loadDishNameList", apiCallback);
        apiCall.apiRequestPost(getActivity(),obj, Config.URL_SEARCH, "loadDishNameList", apiCallback);
    }
    private void getRestaurantList(String lat, String lon) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
        //obj.put("postUserId",db.getUserDetails().get("userId"));
        obj.put("type", "restaurant");
       // obj.put("searchText","fa");
        if (!lat.equals("")){
            obj.put("latitude",lat);
            obj.put("longitude",lon);
        }
        apiCall.apiRequestPost(getActivity(),obj, Config.URL_SEARCH, "loadRestaurantListCheckin", apiCallback);
    }
    private void getRestaurantListSearch(String key) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
        //obj.put("postUserId",db.getUserDetails().get("userId"));
        obj.put("type", "restaurant");
       // String sKey = key.toString();
        //sKey = sKey.replaceAll(" ", "%20");

        obj.put("searchText", key);

        apiCall.apiRequestPost(getActivity(),obj, Config.URL_SEARCH, "loadRestaurantListCheckin", apiCallback);
    }
    @Override
    public void apiResponse(JSONObject response, String tag) {
        Log.d("NewPostShare",tag+" : "+response);
        if (tag.equals("loadDishNameList")){
            if (response !=null){
                try {
                    loadDataIntoView(response, tag);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (tag.equals("loadRestaurantListCheckin")){
            if (response != null){
                try {
                    loadRestaurantIntoView(response, tag);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void loadDataIntoView(JSONObject response, String tag) throws JSONException {

        this.response = response;
        //JSONArray rListArray = response.getJSONArray("result");

        // Log.d("rListArray", "total: "+ rListArray.length());
       /* for (int i=0;i<rListArray.length();i++){
            DishListObj current = new DishListObj();
            current.id = rListArray.getJSONObject(i).getString("id");
            current.name = rListArray.getJSONObject(i).getString("name");
            current.postCount = rListArray.getJSONObject(i).getString("postCount");
            dishList.add(current);
        }*/

        //-------
        JSONArray rListArray = response.getJSONObject("result").getJSONObject("hits").getJSONArray("hits");
        //Log.d("SearchResult",response.getJSONObject("result").getJSONObject("hits").getJSONArray("hits").length()+"");
        dishList.clear();
        for (int i=0;i<rListArray.length();i++){
            DishListObj current = new DishListObj();
            current.id = rListArray.getJSONObject(i).getJSONObject("_source").getString("id");
            current.name = rListArray.getJSONObject(i).getJSONObject("_source").getString("dishname");
            if (rListArray.getJSONObject(i).getJSONObject("_source").has("postcount")){
                current.postCount = rListArray.getJSONObject(i).getJSONObject("_source").getString("postcount")+" Dishes";
                dishList.add(current);
            }

        }




        //Log.d("send list", "total: "+restaurantList.size());
        if (getActivity() != null && dishNameLoaded == false){
            dishTaggingAdapter = new DishTaggingAdapter(getActivity(),dishList , dishTaggingCallback);
            recyclerView.setAdapter(dishTaggingAdapter);
            dishNameLoaded = true;
        }else {
            dishTaggingAdapter.notifyDataSetChanged();
        }
    }

    private void loadRestaurantIntoView(JSONObject response, String tag) throws JSONException{
        responseRList = response;
//        JSONArray rListArray = response.getJSONArray("restaurants");

        JSONObject result= response.getJSONObject("result");
        JSONObject hitsObj = result.getJSONObject("hits");
        JSONArray hitsArray = hitsObj.getJSONArray("hits");

        //JSONObject sourceObj = hitsArray.getJSONObject(0).get;

       // Log.d("Restaurant list",);
        if (restaurantList.size() > 0){
            restaurantList.clear();
        }
        if (hitsArray.length() == 0){
            placeHolderNoRestaurant.setVisibility(View.VISIBLE);
            txtAddRestaurant.setText("You can add '" +inputRestaurantSearch.getText().toString()+ "' to food talk");
        }else {
            placeHolderNoRestaurant.setVisibility(View.GONE);
        }
//        Log.d("NewPostShare","total: "+hitsArray.length()+" source rname "+hitsArray.getJSONObject(0).getJSONObject("_source").getString("restaurantname"));
       for (int i=0;i<hitsArray.length();i++){
            RestaurantListObj current = new RestaurantListObj();
            current.id = hitsArray.getJSONObject(i).getJSONObject("_source").getString("id");
            current.area = hitsArray.getJSONObject(i).getJSONObject("_source").getString("area");
            current.cityName = hitsArray.getJSONObject(i).getJSONObject("_source").getString("cityname");
            current.restaurantName = hitsArray.getJSONObject(i).getJSONObject("_source").getString("restaurantname");
            current.restaurantIsActive = hitsArray.getJSONObject(i).getJSONObject("_source").getString("isactivated");
            restaurantList.add(current);
        }
        if (getActivity() != null){
            checkInAdapter = new CheckInAdapter(getActivity(),restaurantList, checkInCallback);
            recyclerViewRestaurant.setAdapter(checkInAdapter);
            checkInAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void dishNameSelected(String dishName) {
        Log.d("NewPostShare","dishNameSelected: "+dishName);
        this.dishName = dishName;
        Log.d("NewPostShare","dishNameS: "+dishName);
        hideSoftKeyboard();
        txtAddDish.setText(StringCase.caseSensitive(dishName));
        dishSearch.setVisibility(View.GONE);
        searchView = false;
        if (rating1 != null && !rating1.equals("0")){
            shareBtnEnable(true);
        }
    }
    @Override
    public void startRating(String dishName) {

    }
    private void showKeyBoard(){
        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
    public void hideSoftKeyboard() {
        if(getActivity().getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }
    //------
    private void onTexChange(String newText){
        try {
            JSONArray rListArray = response.getJSONArray("result");
            dishList.clear();
            for (int i=0;i<rListArray.length();i++){
                DishListObj current = new DishListObj();
                current.id = rListArray.getJSONObject(i).getString("id");
                current.name = rListArray.getJSONObject(i).getString("name");
                current.postCount = rListArray.getJSONObject(i).getString("postCount");
                dishList.add(current);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Log.d("rListArray", "total: "+ rListArray.length());
        // tempList = new ArrayList<RestaurantListObj>(restaurantList);

        final List<DishListObj> filteredModelList = filter(dishList, newText);
        Log.d("NewPostShare", "filterdModeList "+filteredModelList.size());
        dishTaggingAdapter.animateTo(filteredModelList);
        recyclerView.scrollToPosition(0);

        /*if (filteredModelList.size() == 0){
            btnAddDishName.setVisibility(View.VISIBLE);
            txtNewDish.setText("Add "+newText);
            newDishName = newText;
        }else {
            btnAddDishName.setVisibility(View.GONE);
        }*/
        if (newText.length() > 1){
            btnAddDishName.setVisibility(View.VISIBLE);
            txtNewDish.setText("Add "+newText);
            newDishName = newText;
        }else {
            btnAddDishName.setVisibility(View.GONE);
        }
    }
    private List<DishListObj> filter(List<DishListObj> models, String query) {
        query = query.toLowerCase();
        //this.postData =  new ArrayList<RestaurantPostObj>(postList);
        final List<DishListObj> filteredModelList = new ArrayList<>();
        for (DishListObj model : models) {
            final String text = model.name.toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
    @Override
    public void location(String gpsStatus, String lat, String lon) {
        if (gpsStatus.equals(ConstantVar.LOCATION_GOT)){
            gpsLocationOn = "on";
            this.lat = lat;
            this.lon = lon;
            try {
                getRestaurantList(lat, lon);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            gpsAlertMsg.setVisibility(View.GONE);
            Log.d("NewPostShare", "lat: "+lat+" lon: "+lon);
        }else if (gpsStatus.equals(ConstantVar.LOCATION_DISABLED)){
            gpsLocationOn = "off";
            try {
                getRestaurantList("", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("NewPostShare", "location disabled");
        }
    }
    @Override
    public void checkInRestaurant(String restaurantId, String restaurantName) {
        if (restaurantId == null){
            addRestaurantCallback.addNewRestaurant(inputRestaurantSearch.getText().toString());
            restaurantSearchView = false;
            searchView = false;
        }else {
            this.restaurantId = restaurantId;
            rName.setText(StringCase.caseSensitive(restaurantName));
        }
        restaurantSearch.setVisibility(View.GONE);
        restaurantSearchView = false;
        hideSoftKeyboard();
    }
}
