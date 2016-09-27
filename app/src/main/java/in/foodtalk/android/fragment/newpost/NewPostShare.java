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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.foodtalk.android.R;
import in.foodtalk.android.adapter.newpost.DishTaggingAdapter;
import in.foodtalk.android.apicall.ApiCall;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.ApiCallback;
import in.foodtalk.android.communicator.DishTaggingCallback;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.StringCase;
import in.foodtalk.android.object.DishListObj;

/**
 * Created by RetailAdmin on 22-09-2016.
 */
public class NewPostShare extends Fragment implements View.OnTouchListener, ApiCallback, DishTaggingCallback {

    View layout;
    public Bitmap photo;
    public String checkInRestaurantName;
    public String checkInRestaurantId;
    ImageView imgHolder;
    ScrollView scrollView;
    EditText inputTip;
    TextView rName;
    public RelativeLayout dishSearch;

    LinearLayout lableAddDish;
    LinearLayout lableCheckin;

    EditText inputDishSearch;

    public Boolean searchView = false;

    List<DishListObj> dishList = new ArrayList<>();
    RecyclerView recyclerView;

    ApiCall apiCall;
    DatabaseHandler db;
    ApiCallback apiCallback;

    DishTaggingAdapter dishTaggingAdapter;
    DishTaggingCallback dishTaggingCallback;

    LinearLayoutManager linearLayoutManager;


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

        lableAddDish = (LinearLayout) layout.findViewById(R.id.lable_add_dish);
        lableCheckin = (LinearLayout) layout.findViewById(R.id.lable_checkin);

        inputDishSearch = (EditText) layout.findViewById(R.id.input_dish_search);

        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);

        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        apiCall = new ApiCall();
        db = new DatabaseHandler(getActivity());
        apiCallback = this;
        dishTaggingCallback = this;

        lableAddDish.setOnTouchListener(this);
        lableCheckin.setOnTouchListener(this);

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
        focusListener();
        textListener();
        try {
            getDishList();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //fordishSearch();
        return layout;
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
                        //fordishSearch();
                        break;
                }
                break;
            case R.id.lable_checkin:
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        break;
                }
                break;
        }
        return true;
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
                    recyclerView.setVisibility(View.GONE);
                    //btnNext.setTextColor(getResources().getColor(R.color.btn_disable));
                   // btnNextEnable = false;
                }else {
                    /*if (dishNameLoaded){
                        onTexChange(s.toString());
                    }*/
                    recyclerView.setVisibility(View.VISIBLE);
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

    private void getDishList() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
        apiCall.apiRequestPost(getActivity(),obj, Config.URL_DISH_NAME, "loadDishNameList", apiCallback);
    }
    @Override
    public void apiResponse(JSONObject response, String tag) {
        Log.d("NewPostShare",tag+" : "+response);
        try {
            loadDataIntoView(response, tag);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadDataIntoView(JSONObject response, String tag) throws JSONException {

        //this.response = response;

        JSONArray rListArray = response.getJSONArray("result");
        // Log.d("rListArray", "total: "+ rListArray.length());
        for (int i=0;i<rListArray.length();i++){
            DishListObj current = new DishListObj();
            current.id = rListArray.getJSONObject(i).getString("id");
            current.name = rListArray.getJSONObject(i).getString("name");
            current.postCount = rListArray.getJSONObject(i).getString("postCount");
            dishList.add(current);
        }
        //Log.d("send list", "total: "+restaurantList.size());
        if (getActivity() != null){
            dishTaggingAdapter = new DishTaggingAdapter(getActivity(),dishList , dishTaggingCallback);
            recyclerView.setAdapter(dishTaggingAdapter);
            //dishNameLoaded = true;
        }
    }

    @Override
    public void dishNameSelected(String dishName) {
        Log.d("NewPostShare","dishNameS: "+dishName);
    }

    @Override
    public void startRating(String dishName) {

    }
}
