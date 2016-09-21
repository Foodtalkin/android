package in.foodtalk.android;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import in.foodtalk.android.adapter.newpost.CityListAdapter;
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.CityListCallback;
import in.foodtalk.android.communicator.OnBoardingCallback;
import in.foodtalk.android.fragment.intro.DiscoverIntro;
import in.foodtalk.android.fragment.intro.EatIntro;
import in.foodtalk.android.fragment.intro.LandingIntro;
import in.foodtalk.android.fragment.intro.PagerAdapter;
import in.foodtalk.android.fragment.intro.ShareIntro;
import in.foodtalk.android.fragment.onboarding.PagerAdapterOb;
import in.foodtalk.android.fragment.onboarding.SelectCity;
import in.foodtalk.android.fragment.onboarding.SelectEmail;
import in.foodtalk.android.fragment.onboarding.SelectUsername;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.FixedSpeedScroller;
import in.foodtalk.android.module.StringCase;
import in.foodtalk.android.module.UserAgent;
import in.foodtalk.android.object.LoginInfo;
import in.foodtalk.android.object.LoginValue;

public class WelcomeUsername extends AppCompatActivity implements View.OnClickListener, OnBoardingCallback {

    EditText txtUser;
    ImageButton btnUser;
    TextView txtUserNameError;
    Boolean btnUserEnable = false;
    Config config;
    LoginInfo loginInfo;
    LinearLayout btnSelectCity;

    RecyclerView recyclerView;
    Dialog dialog;
    CityListAdapter cityListAdapter;
    TextView txtCity;
    StringCase stringCase;
    ArrayList<String> cityList;

    OnBoardingCallback onBoardingCallback;

    Boolean cityListLoaded = false;
    EditText inputEmail;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z0-9]+\\.+[a-z0-9]+";

    TextView txtEmailError;

    SelectUsername selectUsername = new SelectUsername();

    PagerAdapterOb mPagerAdapter;

    DatabaseHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.welcome_username);
        txtUser = (EditText) findViewById(R.id.txt_username);
        btnUser = (ImageButton) findViewById(R.id.btn_user_select);
        txtUserNameError = (TextView) findViewById(R.id.txt_username_error);
        inputEmail = (EditText) findViewById(R.id.txt_email);



        //setFragmentView(selectUsername, R.id.onboard_holder, false);

        txtEmailError = (TextView) findViewById(R.id.txt_email_error);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
           // newString= null;
        } else if (!extras.getString("email").equals("")){
            inputEmail.setText(extras.getString("email"));
        }


        stringCase  = new StringCase();
        btnSelectCity = (LinearLayout) findViewById(R.id.btn_select_city_welcome);
        txtCity = (TextView) findViewById(R.id.txt_city_welcome);

        btnSelectCity.setOnClickListener(this);

        Log.d("btnSelectCity", btnSelectCity+"");

        btnUser.setOnClickListener(this);

        initialisePaging();



        config = new Config();
        loginInfo = new LoginInfo();
        db = new DatabaseHandler(getApplicationContext());

        //----text change listener--------------

        txtUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("onTextChange","count "+count);
                if (count > 0){
                    Log.d("setImg","enable");
                    if (inputEmail.length() != 0){
                        btnUser.setImageResource(R.drawable.btn_user_enable);
                        btnUserEnable = true;
                    }
                }
                else {
                    Log.d("setImg","enable");
                    btnUser.setImageResource(R.drawable.btn_user_disabled);
                    btnUserEnable = false;
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                //Log.d("afterTextChange",s+"");
                //Log.d("afterTextChange","after change");
            }
        });

        inputEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0){
                    //Log.d("setImg","enable");
                    if (txtUser.length() != 0){
                        btnUser.setImageResource(R.drawable.btn_user_enable);
                        btnUserEnable = true;
                    }
                }
                else {
                    Log.d("setImg","enable");
                    btnUser.setImageResource(R.drawable.btn_user_disabled);
                    btnUserEnable = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        try {
            getCityList("load");
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    private void setFragmentView(Fragment newFragment, int container, boolean bStack) {
        String backStateName = newFragment.getClass().getName();

        Log.i("setFragmentView",newFragment.getClass().getSimpleName());
        FragmentManager manager = getFragmentManager();



        android.app.FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(container, newFragment);
        if (bStack) {
            transaction.addToBackStack(backStateName);
            //Log.d("addtobackstack", backStateName);
        }
        // Commit the transaction
        Log.d("setFragmentview","going to commit");
        transaction.commit();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_user_select:
                if (btnUserEnable){
                    Log.d("OnClick", "user btn clicked");
                    String email = inputEmail.getText().toString();
                    boolean atleastOneAlpha = txtUser.getText().toString().matches(".*[a-zA-Z]+.*");
                    if(atleastOneAlpha){
                        if (email.matches(emailPattern)){
                            txtEmailError.setAlpha(0);
                            try {
                                createUserName(txtUser.getText().toString(), inputEmail.getText().toString(), txtCity.getText().toString(), "postUserName");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            txtEmailError.setAlpha(1);
                        }
                    }else {
                        txtUserNameError.setText(getResources().getString(R.string.user_name_alphabet));
                        txtUserNameError.setAlpha(1);
                    }


                }
                break;
            case R.id.btn_select_city_welcome:
                Log.d("btn click","select city");
                if (cityListLoaded){
                    cityShow();
                }else {
                    String errorMessage = "Please wait, Cities is loading";
                    Toast toast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
                    toast.show();

                }
                break;
        }
    }
    private void createUserName(String userName, String email, String city, String tag) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("signInType", "F");
        obj.put("fullName", db.getUserDetails().get("fullName"));
        obj.put("userName",userName);
        //obj.put("email",loginInfo.email);
        obj.put("facebookId",db.getUserDetails().get("facebooId"));
        obj.put("email", email);
        obj.put("region", city);
        //obj.put("latitude",loginInfo.latitude);
        //obj.put("longitude",loginInfo.longitude);
        obj.put("deviceToken","12344566776");
        //obj.put("image",loginInfo.image);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                config.URL_LOGIN, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d(TAG, "After Sending JsongObj"+response.toString());
                        //msgResponse.setText(response.toString());
                        Log.d("Login Respond", response.toString());
                        try {
                            String status = response.getString("status");
                            if (!status.equals("error")){
                                db.resetTables();
                                getAndSave(response);
                            }else {
                                txtUserNameError.setText(getResources().getString(R.string.user_name_taken));
                                txtUserNameError.setAlpha(1);
                                //showErrorTxt();
                                Log.e("Response status", "error");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        /*JSONObject jObj = new JSONObject(response);
                        JSONObject status = jObj.getJSONObject("status");
                        String type = status.getString("type");*/
                        //--Start new activity--
                        //----------------------
                        //hideProgressDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // VolleyLog.d(TAG, "Error: " + error.getMessage());
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
                if (userAgent.getUserAgent(getApplicationContext()) != null ){
                    headers.put("User-agent", userAgent.getUserAgent(getApplicationContext()));
                }
                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjReq,tag);
    }

    private void getAndSave(JSONObject response) throws JSONException {
        JSONObject jObj = response.getJSONObject("profile");

        String fullName = jObj.getString("fullName");
        String fId = jObj.getString("facebookId");
        String userName = jObj.getString("userName");
        String uId = response.getString("userId");
        String sessionId = response.getString("sessionId");
        LoginValue loginValue = new LoginValue();
        loginValue.fbId = fId;
        loginValue.uId = uId;
        loginValue.sId = sessionId;
        loginValue.name = fullName;
        loginValue.email = jObj.getString("email");
        loginValue.cityId = jObj.getString("cityName");
        //loginValue.userName = userName;
        loginValue.userName = ((userName.equals("")) ? "N/A" : userName);

        //-- Log.d("check table", db.getRowCount()+"");
        db.addUser(loginValue);

        if(!userName.equals("") || !userName.equals(null)){
            Intent i = new Intent(WelcomeUsername.this, Home.class);
            startActivity(i);
            finish();
        }else {
            Log.d("Username class", "error with username");
        }
    }

    private void showErrorTxt(){
        Log.d("showErrorTxt","function call");
        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(3000);

        txtUserNameError.startAnimation(in);
        in.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
               // mSwitcher.setText("New Text");
               // mSwitcher.startAnimation(in);

            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
    private void cityShow(){
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_city_list);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.verticalMargin = 100;
        lp.gravity = Gravity.CENTER;

        // dialog.getWindow().setAttributes(lp);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView = (RecyclerView) dialog.findViewById(R.id.recycler_view_city_list);

        recyclerView.setLayoutManager(layoutManager);

        cityListAdapter = new CityListAdapter(this,cityList, cityListCallback);
        recyclerView.setAdapter(cityListAdapter);
        dialog.show();
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
                if (userAgent.getUserAgent(getApplicationContext()) != null ){
                    headers.put("User-agent", userAgent.getUserAgent(getApplicationContext()));
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
    ViewPager pager;
    List<android.support.v4.app.Fragment> fragments;

    private void initialisePaging(){
        fragments = new Vector<android.support.v4.app.Fragment>();
        fragments.add(android.support.v4.app.Fragment.instantiate(this,SelectUsername.class.getName()));
        fragments.add(android.support.v4.app.Fragment.instantiate(this,SelectEmail.class.getName()));
        fragments.add(android.support.v4.app.Fragment.instantiate(this,SelectCity.class.getName()));

        mPagerAdapter = new PagerAdapterOb(this.getSupportFragmentManager(), fragments);
        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(mPagerAdapter);

        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(pager.getContext());
            // scroller.setFixedDuration(5000);
            mScroller.set(pager, scroller);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }

        pager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return true;
            }
        });

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //pager.setCurrentItem(0, true);
            }
            @Override
            public void onPageSelected(int position) {
                /*if (position == 0){
                    vpNav1.setBackgroundResource(R.drawable.circle_selected_vp);
                    vpNav2.setBackgroundResource(R.drawable.circle_vp);
                    vpNav3.setBackgroundResource(R.drawable.circle_vp);
                    vpNav4.setBackgroundResource(R.drawable.circle_vp);
                }
                if (position == 1){
                    vpNav2.setBackgroundResource(R.drawable.circle_selected_vp);
                    vpNav1.setBackgroundResource(R.drawable.circle_vp);
                    vpNav3.setBackgroundResource(R.drawable.circle_vp);
                    vpNav4.setBackgroundResource(R.drawable.circle_vp);
                }
                if (position == 2){
                    vpNav3.setBackgroundResource(R.drawable.circle_selected_vp);
                    vpNav1.setBackgroundResource(R.drawable.circle_vp);
                    vpNav2.setBackgroundResource(R.drawable.circle_vp);
                    vpNav4.setBackgroundResource(R.drawable.circle_vp);
                }
                if (position == 3){
                    vpNav4.setBackgroundResource(R.drawable.circle_selected_vp);
                    vpNav1.setBackgroundResource(R.drawable.circle_vp);
                    vpNav2.setBackgroundResource(R.drawable.circle_vp);
                    vpNav3.setBackgroundResource(R.drawable.circle_vp);
                }*/
                Log.d("onPageSelected",position+"");
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    @Override
    public void onboardingBtnClicked(String btn, String key, String value) {
        Log.d("wu callback", btn);
        Log.d("check pager", pager.getCurrentItem()+"");
        if (btn.equals("next")){
            pager.setCurrentItem(getItem(+1), true);
        }else if (btn.equals("previos")){
            pager.setCurrentItem(getItem(-1), true);
        }
        if (key != null){
            if (key.equals("userName")){
                View v = pager.getChildAt(1);
                //TextView head = (TextView) v.findViewById(R.id.txt_head);
                try {
                    TextView txt = (TextView) fragments.get(1).getView().findViewById(R.id.txt_head);
                    txt.setText(value);
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                //getSupportFragmentManager().findFragmentById(R.id.fra)
                //head.setText(value+",");
                //Fragment fragment = getFragmentManager().findF
                //AppController.onBoardingCallback.onboardingBtnClicked(btn, key, value);
            }
        }
    }
    private int getItem(int i) {
        return pager.getCurrentItem() + i;
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() > 0) {
            pager.setCurrentItem(pager.getCurrentItem()-1, true);
        } else {
            super.onBackPressed();
        }
    }
}
