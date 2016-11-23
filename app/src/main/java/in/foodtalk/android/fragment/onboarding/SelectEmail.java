package in.foodtalk.android.fragment.onboarding;

import android.app.Application;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.foodtalk.android.R;
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.communicator.OnBoardingCallback;

/**
 * Created by RetailAdmin on 02-09-2016.
 */
public class SelectEmail extends Fragment implements OnBoardingCallback {
    OnBoardingCallback onBoardingCallback;
    LinearLayout btnSend;
    EditText inputEmail;
    TextView txtError, txtHead;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z0-9]+\\.+[a-z0-9.]+";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.onboarding_email, container, false);
        btnSend = (LinearLayout) layout.findViewById(R.id.btn_send);
        txtError = (TextView) layout.findViewById(R.id.txt_error);
        txtError.setVisibility(View.GONE);
        txtHead = (TextView) layout.findViewById(R.id.txt_head);
        Log.d("SelectEmail","username "+AppController.userName);
        txtHead.setText(AppController.userName+",");


        inputEmail = (EditText) layout.findViewById(R.id.input_email);
        inputEmail.setText(AppController.fbEmailId);

        onBoardingCallback = (OnBoardingCallback) getActivity();
        //AppController.onBoardingCallback = onBoardingCallback;
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SelectEmail","button clicked send");
                String email = inputEmail.getText().toString().trim();
                Log.d("email", email+"");
                if (email.matches(emailPattern)){
                    onBoardingCallback.onboardingBtnClicked("next", null, null);
                    AppController.fbEmailId = email;
                    errorMsg(false, "");
                }else {
                    Log.d("select email", "wrong email");
                    errorMsg(true, "Ooops, this doesn't look like a valid email");
                }

            }
        });
        return layout;
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("SelectEmail","onResume");
        if (AppController.userName != null){
            if (!AppController.userName.equals("")){
                txtHead.setText(AppController.userName+",");
            }
        }
    }

    @Override
    public void onboardingBtnClicked(String btn, String key, String value) {
        Log.d("value","value: "+ value);
        if (key != null){
            if (key.equals("userName")){
                Log.d("SelectEmail", "username: "+ value);
                txtHead.setText(value+",");
            }
        }
    }
    private void errorMsg(Boolean error, String msg){
        if (error){
            txtError.setText(msg);
            //txtError.setAlpha(1);
            txtError.setVisibility(View.VISIBLE);
            Animation myFadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.blink_anim);
            txtError.startAnimation(myFadeInAnimation);
        }else {
            txtError.setVisibility(View.GONE);
        }
    }
}
