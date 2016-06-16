package in.foodtalk.android.fragment.newpost;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import in.foodtalk.android.adapter.newpost.DishTaggingAdapter;
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.DishTaggingCallback;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.object.DishListObj;
import in.foodtalk.android.object.RestaurantListObj;

/**
 * Created by RetailAdmin on 25-05-2016.
 */
public class DishTagging extends Fragment implements DishTaggingCallback, View.OnTouchListener, View.OnKeyListener {

    View layout;

    Bitmap photo;

    ImageView picHolder;
    EditText inputDishName;

    TextView btnNext;

    Boolean dishNameLoaded = false;

    RelativeLayout relativeLayout;

    Boolean btnNextEnable = false;

    DatabaseHandler db;
    Config config;

    JSONObject response;
    DishTaggingAdapter dishTaggingAdapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    DishTaggingCallback dishTaggingCallback;
    DishTaggingCallback dishTaggingCallback1;

    List<DishListObj> dishList = new ArrayList<>();

    public DishTagging (Bitmap photo){
        this.photo = photo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.dish_tagging, container, false);


        picHolder = (ImageView) layout.findViewById(R.id.img_dish_tagging);
        inputDishName = (EditText) layout.findViewById(R.id.edit_dish_name);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view_dish);

        btnNext = (TextView) layout.findViewById(R.id.btn_next_dishtagging);

        btnNext.setOnTouchListener(this);

        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        dishTaggingCallback = this;
        dishTaggingCallback1 = (DishTaggingCallback) getActivity();

       //relativeLayout = (RelativeLayout) layout.findViewById(R.id.root_dish);

        picHolder.setImageBitmap(photo);

        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        inputDishName.requestFocus();

        db = new DatabaseHandler(getActivity());
        config = new Config();

        try {
            getDishList("load");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        inputDishName.setOnKeyListener(this);

        //showSoftKeyboard(inputDishName);
        textListener();
        return layout;
    }

    private void textListener(){
        inputDishName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Log.d("beforeTextChange", s+"");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.d("onTextChanged", s+"");
                if (count == 0){
                    recyclerView.setVisibility(View.GONE);
                    btnNext.setTextColor(getResources().getColor(R.color.btn_disable));
                    btnNextEnable = false;
                }else {
                    if (dishNameLoaded){
                        onTexChange(s.toString());
                    }
                    recyclerView.setVisibility(View.VISIBLE);
                    btnNext.setTextColor(getResources().getColor(R.color.btn_enable));
                    btnNextEnable = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Log.d("afterTextChange", s+"");


            }
        });
    }

    public void showSoftKeyboard(EditText txt) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        txt.requestFocus();
        inputMethodManager.showSoftInput(txt, 0);
    }

    public void getDishList(final String tag) throws JSONException {

        //Log.d("getPostFeed", "post data");
        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
        //obj.put("latitude","28.4820495");
       // obj.put("longitude","77.0832561");
        //obj.put("includeCount", "1");
        //obj.put("includeFollowed","1");
       // obj.put("postUserId",db.getUserDetails().get("userId"));
        //Log.d("getPostFeed","pageNo: "+pageNo);
        //obj.put("page",Integer.toString(pageNo));
        // obj.put("recordCount","10");
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                config.URL_DISH_NAME, obj,
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
                return headers;
            }
        };

        final int DEFAULT_TIMEOUT = 6000;
        // Adding request to request queue
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq,"gethomefeed");
    }
    private void loadDataIntoView(JSONObject response, String tag) throws JSONException {

        this.response = response;

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
        dishTaggingAdapter = new DishTaggingAdapter(getActivity(),dishList , dishTaggingCallback);
        recyclerView.setAdapter(dishTaggingAdapter);
        dishNameLoaded = true;
    }

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
        dishTaggingAdapter.animateTo(filteredModelList);
        recyclerView.scrollToPosition(0);
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
    public void dishNameSelected(String dishName) {
        Log.d("dish name", dishName);
        inputDishName.setText(dishName);
        recyclerView.setVisibility(View.GONE);
        inputDishName.setSelection(dishName.length());

    }

    @Override
    public void startRating(String dishName) {
        Log.d("startRating fragment", dishName);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()){
            case R.id.btn_next_dishtagging:
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        if (btnNextEnable){
                            Log.d("onTouch", "next btn clicked");
                            dishTaggingCallback1.startRating(inputDishName.getText().toString());
                        }
                        break;
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        Log.d("key down", keyCode+"");
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
            //Log.d("key press", "enter key");
            if (btnNextEnable){
                Log.d("onTouch", "next btn clicked");
                dishTaggingCallback1.startRating(inputDishName.getText().toString());
            }
        }
        return false;
    }
}
