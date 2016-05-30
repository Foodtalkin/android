package in.foodtalk.android.fragment.newpost;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import in.foodtalk.android.R;
import in.foodtalk.android.communicator.ReviewCallback;

/**
 * Created by RetailAdmin on 27-05-2016.
 */
public class ReviewFragment extends Fragment implements View.OnTouchListener {

    View layout;
    Bitmap photo;

    ImageView dishPic;
    EditText editReview;
    TextView btnPost;

    ReviewCallback reviewCallback;


    public ReviewFragment (Bitmap photo){
        this.photo = photo;


    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.review, container, false);

        dishPic = (ImageView) layout.findViewById(R.id.img_dish_review);
        editReview = (EditText) layout.findViewById(R.id.edit_dish_review);
        btnPost = (TextView) layout.findViewById(R.id.btn_post_review);

        btnPost.setOnTouchListener(this);

        dishPic.setImageBitmap(photo);

        reviewCallback = (ReviewCallback) getActivity();

        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        editReview.requestFocus();
        return layout;
    }

    private void cl(){
        Map config = new HashMap();
        config.put("cloud_name", "n07t21i7");
        config.put("api_key", "123456789012345");
        config.put("api_secret", "abcdeghijklmnopqrstuvwxyz12");
      //  Cloudinary cloudinary = new Cloudinary(config);
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()){
            case R.id.btn_post_review:
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        //Log.d("onTouch",editReview.getText().toString()+"");
                        if (editReview.getText().length() != 0){
                            reviewCallback.postData(editReview.getText().toString());
                        }else {
                            reviewCallback.postData("na");
                        }
                        break;
                }
                break;
        }
        return true;
    }
}
