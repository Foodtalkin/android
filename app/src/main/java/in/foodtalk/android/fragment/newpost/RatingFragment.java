package in.foodtalk.android.fragment.newpost;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.media.Rating;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import in.foodtalk.android.R;
import in.foodtalk.android.communicator.RatingCallback;

/**
 * Created by RetailAdmin on 27-05-2016.
 */
public class RatingFragment extends Fragment implements View.OnTouchListener {

    View layout;
    ImageView btnStar1, btnStar2, btnStar3, btnStar4, btnStar5;

    int rating;

    Bitmap photo;
    ImageView dishPic;

    RatingCallback ratingCallback;

    public RatingFragment (Bitmap photo){
        this.photo = photo;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.rating, container, false);

        btnStar1 = (ImageView) layout.findViewById(R.id.btn_star_rating1);
        btnStar2 = (ImageView) layout.findViewById(R.id.btn_star_rating2);
        btnStar3 = (ImageView) layout.findViewById(R.id.btn_star_rating3);
        btnStar4 = (ImageView) layout.findViewById(R.id.btn_star_rating4);
        btnStar5 = (ImageView) layout.findViewById(R.id.btn_star_rating5);

        dishPic = (ImageView) layout.findViewById(R.id.img_dish_rating);

        dishPic.setImageBitmap(photo);

        ratingCallback = (RatingCallback) getActivity();


        btnStar1.setOnTouchListener(this);
        btnStar2.setOnTouchListener(this);
        btnStar3.setOnTouchListener(this);
        btnStar4.setOnTouchListener(this);
        btnStar5.setOnTouchListener(this);
        return layout;
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()){
            case R.id.btn_star_rating1:
                switch(event.getAction()){
                    case MotionEvent.ACTION_UP:
                        Log.d("start clicked", "s1");
                        rate(1);
                        break;
                }
                break;
            case R.id.btn_star_rating2:
                switch(event.getAction()){
                    case MotionEvent.ACTION_UP:
                        Log.d("start clicked", "s2");
                        rate(2);
                        break;
                }
                break;
            case R.id.btn_star_rating3:
                switch(event.getAction()){
                    case MotionEvent.ACTION_UP:
                        Log.d("start clicked", "s3");
                        rate(3);
                        break;
                }
                break;
            case R.id.btn_star_rating4:
                switch(event.getAction()){
                    case MotionEvent.ACTION_UP:
                        Log.d("start clicked", "s4");
                        rate(4);
                        break;
                }
                break;
            case R.id.btn_star_rating5:
                switch(event.getAction()){
                    case MotionEvent.ACTION_UP:
                        Log.d("start clicked", "s5");
                        rate(5);
                        break;
                }
                break;
        }
        return true;
    }
    private void rate(int rate){
        rating = rate;
        switch (rate){
            case 1:
                btnStar1.setImageResource(R.drawable.star_rating_a);
                btnStar2.setImageResource(R.drawable.star_rating_p);
                btnStar3.setImageResource(R.drawable.star_rating_p);
                btnStar4.setImageResource(R.drawable.star_rating_p);
                btnStar5.setImageResource(R.drawable.star_rating_p);
                break;
            case 2:
                btnStar1.setImageResource(R.drawable.star_rating_a);
                btnStar2.setImageResource(R.drawable.star_rating_a);
                btnStar3.setImageResource(R.drawable.star_rating_p);
                btnStar4.setImageResource(R.drawable.star_rating_p);
                btnStar5.setImageResource(R.drawable.star_rating_p);
                break;
            case 3:
                btnStar1.setImageResource(R.drawable.star_rating_a);
                btnStar2.setImageResource(R.drawable.star_rating_a);
                btnStar3.setImageResource(R.drawable.star_rating_a);
                btnStar4.setImageResource(R.drawable.star_rating_p);
                btnStar5.setImageResource(R.drawable.star_rating_p);
                break;
            case 4:
                btnStar1.setImageResource(R.drawable.star_rating_a);
                btnStar2.setImageResource(R.drawable.star_rating_a);
                btnStar3.setImageResource(R.drawable.star_rating_a);
                btnStar4.setImageResource(R.drawable.star_rating_a);
                btnStar5.setImageResource(R.drawable.star_rating_p);
                break;
            case 5:
                btnStar1.setImageResource(R.drawable.star_rating_a);
                btnStar2.setImageResource(R.drawable.star_rating_a);
                btnStar3.setImageResource(R.drawable.star_rating_a);
                btnStar4.setImageResource(R.drawable.star_rating_a);
                btnStar5.setImageResource(R.drawable.star_rating_a);
                break;
        }

        ratingCallback.goforReview(Integer.toString(rate));
    }
}