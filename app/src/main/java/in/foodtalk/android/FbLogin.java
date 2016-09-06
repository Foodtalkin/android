package in.foodtalk.android;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.applinks.AppLinkData;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryAgentListener;
import com.flurry.android.FlurryEventRecordStatus;
import com.flurry.android.FlurryInstallReceiver;
import com.flurry.android.FlurrySyndicationEventName;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.fragment.intro.DiscoverIntro;
import in.foodtalk.android.fragment.intro.EatIntro;
import in.foodtalk.android.fragment.intro.LandingIntro;
import in.foodtalk.android.fragment.intro.PagerAdapter;
import in.foodtalk.android.fragment.intro.ShareIntro;
import in.foodtalk.android.helper.ParseUtils;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.Login;
import in.foodtalk.android.module.UserAgent;
import in.foodtalk.android.object.LoginInfo;
import in.foodtalk.android.object.LoginValue;

public class FbLogin extends AppCompatActivity implements OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {


    private CallbackManager callbackManager;

    private String TAG = Login.class.getSimpleName();

    private Config config;

    private ParseUtils parseUtils;

    LinearLayout btnFbAppRemove;
    AccessToken accessToken;

    private PagerAdapter mPagerAdapter;

    //--location provider vars----------
    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    String lat, lon;

    public LoginInfo loginInfo = new LoginInfo();

    private DatabaseHandler db;

    private Button btnPost;
    private ProgressDialog pDialog;
    AppController appController = new AppController();


    View vpNav1;
    View vpNav2;
    View vpNav3;
    View vpNav4;





    //-----------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fb_login);
        config = new Config();

        parseUtils = new ParseUtils();


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(true);

        btnFbAppRemove = (LinearLayout) findViewById(R.id.btn_fbapp_remove);
        btnFbAppRemove.setOnClickListener(this);

        vpNav1 = (View)findViewById(R.id.c1);
        vpNav2 = (View)findViewById(R.id.c2);
        vpNav3 = (View)findViewById(R.id.c3);
        vpNav4 = (View)findViewById(R.id.c4);


        // btnSelectCity.setOnClickListener(this);
       /* btnSelectCity.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("btn", "select city");
                if (cityListLoaded){
                    cityShow();
                }else {
                    String errorMessage = "Please wait, Cities is loading";
                    Toast toast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
                    toast.show();

                }
            }
        });*/

        initialisePaging();

        getkey();
        //btnPost = (Button) findViewById(R.id.btn_post);
        //btnPost.setOnClickListener(this);

        db = new DatabaseHandler(getApplicationContext());
        //Log.d("data count", db.getRowCount()+"");
        /********** get Gps location service LocationManager object ***********/
        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		/*
		  Parameters :
		     First(provider)    :  the name of the provider with which to register
		     Second(minTime)    :  the minimum time interval for notifications, in milliseconds. This field is only used as a hint to conserve power, and actual time between location updates may be greater or lesser than this value.
		     Third(minDistance) :  the minimum distance interval for notifications, in meters
		     Fourth(listener)   :  a {#link LocationListener} whose onLocationChanged(Location) method will be called for each location update
        */
        /********* After registration onLocationChanged method called periodically after each 3 sec ***********/

        buildGoogleApiClient();
        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);
        loginButton.setReadPermissions("public_profile", "email", "user_friends");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                 System.out.print("Logged in");
                String userId = loginResult.getAccessToken().getUserId();
                Log.d("facebook", "Loged in: " + loginResult);
                //-----graph api---------facebook-------
                /*GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());
                                // Application code.
                                try {
                                    String id = object.getString("id");
                                    String email = object.getString("email");
                                    String birthday;
                                    if (object.has("birthday")){
                                        birthday = object.getString("birthday");

                                    }else {
                                        Log.d("fb birthday", "null");
                                        birthday = "N/A";
                                    }
                                    String name = object.getString("name");
                                    String gender = object.getString("gender");
                                    Log.d("fb user info", "id: " + id + "name: " + name + " email: " + email + " gender: " + gender + " birthday: " + birthday);

                                    loginInfo.fullName = name;
                                    loginInfo.email = email;
                                    loginInfo.gender = gender;
                                    loginInfo.facebookId = id;
                                    loginInfo.latitude = ((lat == null) ? "N/A" : lat);
                                    loginInfo.longitude = ((lon == null) ? "N/A" : lon);
                                    loginInfo.signInType = "F";
                                    loginInfo.deviceToken = "548698784";
                                    loginInfo.image = "https://graph.facebook.com/" + id + "/picture?type=large";

                                    // Login login = new Login(getApplicationContext());
                                    postLoginInfo(loginInfo, "login");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();*/
                //-------------
            }

            @Override
            public void onCancel() {
                // App code
                Log.d("facebook", "oncancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                String errormsg = getString(R.string.connection_error);
                Log.i("Error", "Error");
                Toast.makeText(FbLogin.this,
                        errormsg, Toast.LENGTH_LONG).show();
            }
        });


        //----------
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                       // Log.d("df",loginResult.getRecentlyDeniedPermissions());
                        Log.d("LoginManager","loginResult: "+ loginResult.getRecentlyDeniedPermissions());
                        Log.d("LoginManager","loginResult: "+ loginResult.getAccessToken());
                        Log.d("LoginManager","loginResult: "+ loginResult.getRecentlyGrantedPermissions());
                        if (loginResult.getRecentlyGrantedPermissions().contains("email")) {
                            System.out.println("email avilable");
                            getGraphInfo(loginResult.getAccessToken());
                        } else {
                            getGraphInfo(loginResult.getAccessToken());
                           // System.out.println("email permission denied");
                            //LoginManager.getInstance().logOut();
                           // deleteFacebookApplication(loginResult.getAccessToken());
                            accessToken = loginResult.getAccessToken();
                           // hideProgressDialog();
                        }
                    }
                    @Override
                    public void onCancel() {
                        // App code
                        Log.d("LoginManager","cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.d("LoginManager","error: "+ exception);
                        hideProgressDialog();
                    }
                });
    }

    private void getGraphInfo(AccessToken accessToken){
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("LoginActivity", response.toString());
                        // Application code.
                        try {
                            String id = object.getString("id");
                            String email;
                            if (object.has("email")){
                                email = object.getString("email");
                            }else {
                                email = "";
                            }

                            String birthday;
                            if (object.has("birthday")){
                                birthday = object.getString("birthday");

                            }else {
                                Log.d("fb birthday", "null");
                                birthday = "N/A";
                            }
                            String name = object.getString("name");
                            String gender = object.getString("gender");
                            Log.d("fb user info", "id: " + id + "name: " + name + " email: " + email + " gender: " + gender + " birthday: " + birthday);

                            loginInfo.fullName = name;
                            loginInfo.email = email;
                            loginInfo.gender = gender;
                            loginInfo.facebookId = id;
                            loginInfo.latitude = ((lat == null) ? "N/A" : lat);
                            loginInfo.longitude = ((lon == null) ? "N/A" : lon);
                            loginInfo.signInType = "F";
                            loginInfo.deviceToken = "548698784";
                            loginInfo.image = "https://graph.facebook.com/" + id + "/picture?type=large";
                            // Login login = new Login(getApplicationContext());
                            postLoginInfo(loginInfo, "login");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender, birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }
    private void deleteFacebookApplication(AccessToken accessToken){
        new GraphRequest(accessToken.getCurrentAccessToken(), "/me/permissions", null, HttpMethod.DELETE, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                boolean isSuccess = false;
                System.out.println("response: delete app "+ response);
                LoginManager.getInstance().logOut();
                /*try {
                    isSuccess = response.getJSONObject().getBoolean("success");
                    System.out.println("response"+ response);
                    System.out.println("issuccess"+ isSuccess);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isSuccess && response.getError()==null){
                    // Application deleted from Facebook account
                }*/
            }
        }).executeAsync();
    }

    private void fblogin() {
        ArrayList<String> permissions = new ArrayList();
        permissions.add("manage_pages");
        permissions.add("publish_actions");
        LoginManager.getInstance().logInWithPublishPermissions(this, permissions);
    }

    private void showProgressDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog.isShowing())
            pDialog.hide();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    int removeInt = 0;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                // OK button
                Log.d("faceboo: ", "click login button");
                showProgressDialog();
                break;
            case R.id.btn_fbapp_remove:
                removeInt++;
                if (removeInt > 7){
                    deleteFacebookApplication(accessToken);
                    Log.d("fb app","removed");
                    removeInt = 0;
                }
                break;


        }
    }

    //---------------location provider-------------------------------

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(100); // Update location every second

        //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        init();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            lat = String.valueOf(mLastLocation.getLatitude());
            lon = String.valueOf(mLastLocation.getLongitude());
        }
        updateUI();
    }

    public void init() {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        Log.d("check version", "v" + currentapiVersion);
        if (currentapiVersion >= 23) {
            Log.d("check version", "Marshmallow");
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            // Do something for lollipop and above versions
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            Log.d("check version", " less then Marshmallow");
            // do something for phones running an SDK before lollipop
        }
    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permission result", " permission granted");
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        Log.d("checkSelfP","No");
                        return;
                    }else {
                        Log.d("checkSelfP","Yes");
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        //  3000,   // 3 sec
                        //  10, this);
                    }
                } else {
                    Log.d("permission result","denied permission");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onLocationChanged(Location location) {
        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());
        //updateUI();
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }
    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }
    void updateUI() {
        Log.d("GPS location","Latitude"+lat);
        Log.d("GPS location","Longitude"+lon);
        //txtOutputLat.setText(lat);
        //txtOutputLon.setText(lon);
    }
    //----------------post to api------------------------------
    public void postLoginInfo(final LoginInfo loginInfo, String tag) throws JSONException {
        //showProgressDialog();
        //---------------
        JSONObject obj = new JSONObject();
        obj.put("signInType", loginInfo.signInType);
        obj.put("fullName", loginInfo.fullName);
        if (!loginInfo.email.equals("")){
            obj.put("email",loginInfo.email);
        }
        obj.put("facebookId",loginInfo.facebookId);
        obj.put("latitude",loginInfo.latitude);
        obj.put("longitude",loginInfo.longitude);
        obj.put("deviceToken",loginInfo.deviceToken);
        obj.put("gender",loginInfo.gender);
        /*if (appController.deviceToken != null){
            obj.put("deviceToken",appController.deviceToken);
            Log.d("fblg devicetoken", appController.deviceToken);
        }else {
            obj.put("deviceToken","0");
            Log.d("fblg devicetoken", ""+ appController.deviceToken);
        }*/
        obj.put("image",loginInfo.image);

        //obj.put("twitterId","");
        //obj.put("googleId","");
        //Log.d("JSon obj",obj+"");
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                config.URL_LOGIN, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        hideProgressDialog();
                        //Log.d(TAG, "After Sending JsongObj"+response.toString());
                        //msgResponse.setText(response.toString());
                        Log.d("onResponse", "check resonse");
                        Log.d("Login Respond", response.toString());

                        /*JSONObject jObj = new JSONObject(response);
                        JSONObject status = jObj.getJSONObject("status");
                        String type = status.getString("type");*/
                        try {
                            String apiMessage  = response.getString("apiMessage");

                            JSONObject jObj = response.getJSONObject("profile");
                            String fullName = jObj.getString("fullName");
                            String fId = jObj.getString("facebookId");
                            String userName = jObj.getString("userName");
                            String uId = response.getString("userId");
                            String sessionId = response.getString("sessionId");
                            String channels = jObj.getString("channels");
                            String region;
                            if (jObj.has("region")){
                                region = jObj.getString("region");
                            } else {
                                region = "";
                            }

                            FlurryAgent.setUserId(userName);


                            String str = "android,ios,web,desktop";

                           // parseInst(uId);
                            //subscribeWithInfo(String userId,String locationIdentifire, String work, String channels);
                            parseUtils.subscribeWithInfo(uId,"en-IN","development", region);
                            List<String> items = Arrays.asList(channels.split("\\s*,\\s*"));

                            for (int i=0;i<items.size();i++){
                                Log.d("items", items.get(i));
                                parseUtils.subscribeToChannels(items.get(i));
                            }
                            //parseUtils.subscribeToChannels("Android");
                           // parseUtils.subscribeToChannels("Web");
                           // parseUtils.subscribeToChannels("Ios");
                           // parseUtils.unSubscribeToChannels(channels);
                           // parseUtils.unSubscribeToChannels("channels");

                            LoginValue loginValue = new LoginValue();
                            loginValue.fbId = fId;
                            loginValue.uId = uId;
                            loginValue.sId = sessionId;
                            loginValue.name = fullName;
                            loginValue.lat = lat;
                            loginValue.lon = lon;
                            //loginValue.userName = userName;

                            loginValue.userName = ((userName.equals("")) ? "N/A" : userName);

                           // Log.d("check table", db.getRowCount()+"");
                            db.addUser(loginValue);

                            //------Start new activity according to api response
                            if(userName.equals("") || userName.equals(null)){
                                Intent i = new Intent(FbLogin.this, WelcomeUsername.class);
                                i.putExtra("email", loginInfo.email);
                                AppController.fbEmailId = loginInfo.email;
                               // AppController.fullName = loginInfo.fullName;
                                //Log.d("FbLogin", "fullName "+loginInfo.fullName);
                                startActivity(i);
                            }else {
                                //Intent i = new Intent(FbLogin.this, Home.class);
                                Intent i = new Intent(FbLogin.this, WelcomeUsername.class);
                                AppController.fbEmailId = loginInfo.email;
                                i.putExtra("email", loginInfo.email);
                                startActivity(i);
                            }
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //----------------------
                        //hideProgressDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
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
        final int DEFAULT_TIMEOUT = 60000;
        // Adding request to request queue
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq,tag);
        // Cancelling request
        // ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);
    }

    private void parseInst(String userId){
        if (ParseUser.getCurrentUser() == null) {
            ParseUser.enableAutomaticUser();
            Log.d("getCurrentUser","currentuser null");
        }
        ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                String deviceToken = (String) ParseInstallation.getCurrentInstallation().get("deviceToken");
                Log.d("deviceToken callback", deviceToken+"");
            }
        });
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("userId", userId);
        installation.put("work", "development");
        installation.put("localeIdentifier","en-IN");
        installation.saveInBackground();
    }

    private void initialisePaging(){
        final List<Fragment> fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this,LandingIntro.class.getName()));
        fragments.add(Fragment.instantiate(this,EatIntro.class.getName()));
        fragments.add(Fragment.instantiate(this,ShareIntro.class.getName()));
        fragments.add(Fragment.instantiate(this,DiscoverIntro.class.getName()));

        mPagerAdapter = new PagerAdapter(this.getSupportFragmentManager(), fragments);
        ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(mPagerAdapter);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0){
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
                }

                Log.d("onPageSelected",position+"");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void getkey(){
        PackageInfo info;
        Log.d("getKey","key hash");
        try {
            info = getPackageManager().getPackageInfo("in.foodtalk.android", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }
    private void deepLinkfb(){
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppLinkData.fetchDeferredAppLinkData(getApplicationContext(),
                new AppLinkData.CompletionHandler() {
                    @Override
                    public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                        if (appLinkData != null) {
                            Bundle bundle = appLinkData.getArgumentBundle();
                            Log.i("DEBUG_FACEBOOK_SDK", bundle.getString("target_url"));
                            String url = bundle.getString("target_url");
                            List<String> items = Arrays.asList(url.split("\\s*/\\s*"));
                            if (items.size() > 3){
                                Log.d("DebugFb urlvalue", items.get(2));
                                Log.d("DebugFb urlvalue", items.get(3));
                                //openNotificationFragment(items.get(2), items.get(3));
                            }else if (items.size() > 2){
                                //openNotificationFragment(items.get(2), "");
                                Log.d("DebugFb urlvalue", items.get(2));
                            }

                        } else {
                            Log.i("DEBUG_FACEBOOK_SDK", "AppLinkData is Null");
                        }
                    }
                });
    }
}