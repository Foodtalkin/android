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

import in.foodtalk.android.R;

/**
 * Created by RetailAdmin on 27-05-2016.
 */
public class ReviewFragment extends Fragment implements View.OnTouchListener {

    View layout;
    Bitmap photo;

    ImageView dishPic;
    EditText editReview;
    TextView btnPost;


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

        dishPic.setImageBitmap(photo);

        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        editReview.requestFocus();
        return layout;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()){
            case R.id.btn_post_review:
                Log.d("onTouch","clicked post");
                break;
        }
        return true;
    }
}
