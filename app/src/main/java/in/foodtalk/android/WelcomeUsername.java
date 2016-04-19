package in.foodtalk.android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.object.LoginInfo;
import in.foodtalk.android.object.LoginValue;

public class WelcomeUsername extends AppCompatActivity implements View.OnClickListener {

    EditText txtUser;
    ImageButton btnUser;
    TextView txtUserNameError;
    Boolean btnUserEnable = false;
    Config config;
    LoginInfo loginInfo;

    DatabaseHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_username);
        txtUser = (EditText) findViewById(R.id.txt_username);
        btnUser = (ImageButton) findViewById(R.id.btn_user_select);
        txtUserNameError = (TextView) findViewById(R.id.txt_username_error);


        btnUser.setOnClickListener(this);

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
                    btnUser.setImageResource(R.drawable.btn_user_enable);
                    btnUserEnable = true;
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
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_user_select:
                if (btnUserEnable){
                    Log.d("OnClick", "user btn clicked");
                    try {
                        createUserName(txtUser.getText().toString(), "postUserName");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
    private void createUserName(String userName, String tag) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("signInType", "F");
        obj.put("fullName", db.getUserDetails().get("fullName"));
        obj.put("userName",userName);
        //obj.put("email",loginInfo.email);
        obj.put("facebookId",db.getUserDetails().get("facebooId"));
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
}
