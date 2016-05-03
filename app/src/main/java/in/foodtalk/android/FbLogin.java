package in.foodtalk.android;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.GetLocation;
import in.foodtalk.android.module.Login;
import in.foodtalk.android.object.LoginInfo;
import in.foodtalk.android.object.LoginValue;

public class FbLogin extends AppCompatActivity implements OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {


    private CallbackManager callbackManager;

    private String TAG = Login.class.getSimpleName();

    private Config config;


    //--location provider vars----------
    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    String lat, lon;

    public LoginInfo loginInfo = new LoginInfo();

    private DatabaseHandler db;

    private Button btnPost;
    private ProgressDialog pDialog;
    //-----------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fb_login);
        config = new Config();

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(true);

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

        GetLocation getLocation = new GetLocation(this);
        String latitude = getLocation.getUserLocation().latitude;
        String longitude = getLocation.getUserLocation().longitude;
        String altitude = getLocation.getUserLocation().altitude;
        String speed = getLocation.getUserLocation().speed;

        // Log.d("location", "latitude: " + latitude);
        // Log.d("location", "longitude: " + longitude);
        // Log.d("location", "altitude: " + altitude);
        // Log.d("location", "speed: " + speed);

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);
        loginButton.setReadPermissions("public_profile", "email", "user_friends");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // System.out.print("Logged in");
                String userId = loginResult.getAccessToken().getUserId();
                Log.d("facebook", "Loged in: " + loginResult);
                //-----graph api---------facebook-------
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());
                                // Application code.
                                try {
                                    String id = object.getString("id");
                                    String email = object.getString("email");
                                    String birthday = object.getString("birthday"); // 01/31/1980 format
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                // OK button
                Log.d("faceboo: ", "click login button");
                showProgressDialog();
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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

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
    public void postLoginInfo(LoginInfo loginInfo, String tag) throws JSONException {
        //showProgressDialog();

        //----------
        JSONObject objTest = new JSONObject();
        objTest.put("signInType", "F");
        objTest.put("fullName", "Mandeep Singh");
        objTest.put("email","mandeep11@yahoo.com");
        objTest.put("facebookId","10209122833009011");
        objTest.put("latitude","28.6753863");
        objTest.put("longitude","77.180826");
        objTest.put("deviceToken","12344566776");
        objTest.put("image","https:\\/\\/graph.facebook.com\\/10209122833009021\\/picture?type=large");
        //---------------
        JSONObject obj = new JSONObject();
        obj.put("signInType", loginInfo.signInType);
        obj.put("fullName", loginInfo.fullName);
        obj.put("email",loginInfo.email);
        obj.put("facebookId",loginInfo.facebookId);
        obj.put("latitude",loginInfo.latitude);
        obj.put("longitude",loginInfo.longitude);
        obj.put("deviceToken","12344566776");
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
                                startActivity(i);

                            }else {
                                Intent i = new Intent(FbLogin.this, Home.class);
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
}