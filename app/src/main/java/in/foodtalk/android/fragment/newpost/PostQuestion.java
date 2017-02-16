package in.foodtalk.android.fragment.newpost;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cloudinary.Api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import in.foodtalk.android.R;
import in.foodtalk.android.apicall.ApiCall;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.ApiCallback;
import in.foodtalk.android.communicator.OpenFragmentCallback;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.ToastShow;

/**
 * Created by RetailAdmin on 09-02-2017.
 */

public class PostQuestion extends Fragment implements ApiCallback {
    View layout;
    EditText inputQuestion;
    LinearLayout btnPost;
    ApiCall apiCall;
    DatabaseHandler db;
    ApiCallback apiCallback;
    ProgressBar progressBar;
    TextView txtBtn;
    OpenFragmentCallback openFragmentCallback;
    TextView txtCountInfo;

    Boolean posting = false;
    Boolean enablePost = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        layout = inflater.inflate(R.layout.question_post_fragment, container, false);
        inputQuestion = (EditText) layout.findViewById(R.id.input_question);
        btnPost = (LinearLayout) layout.findViewById(R.id.btn_post);
        progressBar = (ProgressBar) layout.findViewById(R.id.progress_bar);
        txtBtn = (TextView) layout.findViewById(R.id.txt_btn);
        openFragmentCallback = (OpenFragmentCallback) getActivity();

        txtCountInfo = (TextView) layout.findViewById(R.id.txt_count_info);

        btnPost.setAlpha((float) 0.5);

        db = new DatabaseHandler(getActivity());

        apiCallback = this;

        apiCall = new ApiCall();

        setTextListner();
        txtCountInfo.setText("Write your question to post.");

        final Animation shaking = AnimationUtils.loadAnimation(getActivity(), R.anim.shaking_anim);


        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("PostQuestion","btnpost clicked");
                if (!posting){
                    if (enablePost){
                        try {
                            postQuestion(inputQuestion.getText().toString());
                            progressBar.setVisibility(View.VISIBLE);
                            txtBtn.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Log.d("PostQuestion","less character");

                        txtCountInfo.startAnimation(shaking);
                    }
                }else {
                    Log.d("PostQuestion","posting...");
                }
            }
        });

        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        inputQuestion.requestFocus();
        return layout;
    }

    private void setTextListner(){

        inputQuestion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                String txt = s.toString();
                Log.d("PostQuestion","afterTextChanged: "+ txt +"count: "+txt.length());
                if (txt.length() == 0){
                    txtCountInfo.setText("Write your question to post.");
                    enablePost = false;
                    btnPost.setAlpha((float) 0.5);

                }else if (txt.length() < 10){
                    txtCountInfo.setText("Write "+(10 - txt.length()) +" more character to post.");
                    enablePost = false;
                    btnPost.setAlpha((float) 0.5);

                }else {
                    txtCountInfo.setText(txt.length()+"/150");
                    enablePost = true;
                    btnPost.setAlpha(1);
                }
            }
        });
    }

    private void postQuestion(String question) throws JSONException {
        posting = true;
        String base64Question = "";
        byte[] dataQuestion;
        try {
            dataQuestion = question.getBytes("UTF-8");
            base64Question = Base64.encodeToString(dataQuestion, Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
        obj.put("tip", base64Question);
        obj.put("type", "question");

        apiCall.apiRequestPost(getActivity(),obj, Config.URL_POST_CREATE, "postQuestion", apiCallback);
    }

    @Override
    public void apiResponse(JSONObject response, String tag) {
        Log.d("PostQuestion","apiResponse");
        if (tag.equals("postQuestion")){
            posting = false;
            if (response != null){
                Log.d("PostQuestion","done");
                try {
                    Log.d("status",response.getString("status")+"");
                    if (response.getString("status").equals("OK")){
                        hideSoftKeyboard();
                        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
                        openFragmentCallback.openFragment("homeRefresh","");
                    }else {
                        progressBar.setVisibility(View.GONE);
                        txtBtn.setVisibility(View.VISIBLE);
                        Log.e("PostQuestion","status: "+response.getString("status"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                Log.d("PostQuestion","try again");
                progressBar.setVisibility(View.GONE);
                txtBtn.setVisibility(View.VISIBLE);
                ToastShow.showToast(getActivity(), "Please try again.");
            }
        }
    }

    public void hideSoftKeyboard() {
        if(getActivity().getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }
}
