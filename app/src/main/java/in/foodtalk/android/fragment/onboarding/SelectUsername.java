package in.foodtalk.android.fragment.onboarding;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import in.foodtalk.android.R;
import in.foodtalk.android.WelcomeUsername;
import in.foodtalk.android.apicall.ApiCall;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.ApiCallback;
import in.foodtalk.android.communicator.OnBoardingCallback;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.ToastShow;

/**
 * Created by RetailAdmin on 01-09-2016.
 */
public class SelectUsername extends Fragment implements ApiCallback {

    LinearLayout btnSend;
    TextView txtUser, txtError;

    OnBoardingCallback onBoardingCallback;

    View layout;

    Boolean btnClickable = false;
    DatabaseHandler db;

    ApiCall apiCall;
    ApiCallback apiCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.onboarding_username, container, false);
        btnSend = (LinearLayout) layout.findViewById(R.id.btn_send);

        txtUser = (TextView) layout.findViewById(R.id.input_username);
        txtError = (TextView) layout.findViewById(R.id.txt_error);

        onBoardingCallback = (OnBoardingCallback) getActivity();

        apiCallback = this;

        db = new DatabaseHandler(getActivity());

        textListener();

        apiCall = new ApiCall();

        txtError.setAlpha(0);

        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (btnClickable){
                    Log.d("SelectUsername","button clicked send");
                    //onBoardingCallback.onboardingBtnClicked("next", null);
                    boolean atleastOneAlpha = txtUser.getText().toString().matches(".*[a-zA-Z]+.*");
                    if(atleastOneAlpha){
                       /* if (email.matches(emailPattern)){
                            txtEmailError.setAlpha(0);
                            try {
                                createUserName(txtUser.getText().toString(), inputEmail.getText().toString(), txtCity.getText().toString(), "postUserName");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            txtEmailError.setAlpha(1);
                        }*/
                        try {
                            createUserName(txtUser.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //txtError.setAlpha(0);
                        //errorMsg(false, "");
                        errorMsg(true, "Please wait...");

                    }else {
                        errorMsg(true, getResources().getString(R.string.user_name_alphabet));
                        /*txtError.setText(getResources().getString(R.string.user_name_alphabet));
                        txtError.setAlpha(1);
                        Animation myFadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.blink_anim);
                        txtError.startAnimation(myFadeInAnimation);*/
                    }
                }else {
                    Log.d("SelectUsername","button clickable set to false");
                }
            }
        });
        // Check if no view has focus:
        hideKeyboard();

        return layout;
    }
    private void hideKeyboard(){
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void textListener(){
        btnSend.setAlpha(0.5f);
        txtUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("onTextChange","count "+count+ " start "+start+" before "+before);
                if (start > 0 || count > 0){
                    Log.d("setImg","enable");
                    btnClickable = true;
                    btnSend.setAlpha(1);
                    /*if (inputEmail.length() != 0){
                        btnUser.setImageResource(R.drawable.btn_user_enable);
                        btnUserEnable = true;
                    }*/
                }
                else {
                    btnClickable = false;
                    btnSend.setAlpha(0.5f);
                    Log.d("setImg","disable");
                   /* btnUser.setImageResource(R.drawable.btn_user_disabled);
                    btnUserEnable = false;*/
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                //Log.d("afterTextChange",s+"");
                //Log.d("afterTextChange","after change");
            }
        });
    }

    private void createUserName(String userName)throws JSONException{

        btnClickable = false;
        hideKeyboard();
        JSONObject obj = new JSONObject();
        obj.put("signInType", "F");
        obj.put("fullName", db.getUserDetails().get("fullName"));
        obj.put("userName",userName);
        obj.put("facebookId",db.getUserDetails().get("facebooId"));
        //obj.put("email", email);
        //obj.put("region", city);
        obj.put("deviceToken","12344566776");
        apiCall.apiRequestPost(getActivity(), obj, Config.URL_LOGIN, "createUsername", apiCallback);
    }
    @Override
    public void apiResponse(JSONObject response, String tag) {
        errorMsg(false, "");
        btnClickable = true;
        if (response != null){
            Log.d("SelectUsername",tag+" : "+response);
            btnClickable = true;
            try {
                String status = response.getString("status");
                if (status.equals("OK")){
                    onBoardingCallback.onboardingBtnClicked("next", null);
                    errorMsg(false,"");
                }else if (status.equals("error")){
                    String errorCode = response.getString("errorCode");
                    if (errorCode.equals("23000")){
                        errorMsg(true, getResources().getString(R.string.user_name_taken));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            Log.d("selectUsername", "response is null");
            ToastShow.showToast(getActivity(), "Please check your internet connection");
        }
    }
    private void errorMsg(Boolean error, String msg){
        if (error){
            txtError.setText(msg);
            txtError.setAlpha(1);
            Animation myFadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.blink_anim);
            txtError.startAnimation(myFadeInAnimation);
        }else {
            txtError.setAlpha(0);
        }
    }
}
