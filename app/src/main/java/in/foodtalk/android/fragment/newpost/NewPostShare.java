package in.foodtalk.android.fragment.newpost;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import in.foodtalk.android.R;
import in.foodtalk.android.module.StringCase;

/**
 * Created by RetailAdmin on 22-09-2016.
 */
public class NewPostShare extends Fragment {

    View layout;
    public Bitmap photo;
    public String checkInRestaurantName;
    public String checkInRestaurantId;
    ImageView imgHolder;
    ScrollView scrollView;
    EditText inputTip;
    TextView rName;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.new_post_share,container,false);
        imgHolder = (ImageView) layout.findViewById(R.id.img_holder);
        inputTip = (EditText) layout.findViewById(R.id.input_tip);
        scrollView = (ScrollView) layout.findViewById(R.id.scroll_view);
        final View activityRootView = layout.findViewById(R.id.activityRoot);
        rName = (TextView) layout.findViewById(R.id.txt_rName);

        Log.d("NewPOstShare","rName: "+checkInRestaurantName);
        if (!checkInRestaurantName.equals("")){
            rName.setText(StringCase.caseSensitive(checkInRestaurantName));
        }else {
            rName.setText("CheckIn");
        }
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (getActivity() != null){
                    if (heightDiff > dpToPx(getActivity(), 200)) { // if more than 200 dp, it's probably a keyboard...
                        // ... do something here
                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.scrollTo(0, 300);
                            }
                        });
                    }
                }
            }
        });
        if (photo != null){
            imgHolder.setImageBitmap(photo);
        }
        focusListener();
        return layout;
    }
    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    private void focusListener(){
        inputTip.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    Log.d("NewPostShare","onFocus");
                    //scrollView.scrollTo(0,800);
                }else {
                    Log.d("NewPostShare","offFocus");
                }
            }
        });
    }
}
