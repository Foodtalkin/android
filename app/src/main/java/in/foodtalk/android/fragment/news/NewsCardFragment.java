package in.foodtalk.android.fragment.news;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import in.foodtalk.android.R;
import in.foodtalk.android.communicator.OpenFragmentCallback;
import in.foodtalk.android.module.DateFunction;
import in.foodtalk.android.object.NewsObj;

/**
 * Created by RetailAdmin on 22-12-2016.
 */

public class NewsCardFragment extends Fragment {
    View layout;
    public NewsObj newsObj;
    TextView txtTitle, txtDes, txtSource,  txtTime, txtReadmore;
    ImageView coverImg;
    RelativeLayout btnReadmore;
    public OpenFragmentCallback openFragmentCallback;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.news_card, container, false);
        txtTitle = (TextView) layout.findViewById(R.id.txt_title);
        txtDes = (TextView) layout.findViewById(R.id.txt_des);
        txtSource = (TextView) layout.findViewById(R.id.txt_source);
        btnReadmore = (RelativeLayout) layout.findViewById(R.id.btn_readmore);
        txtReadmore = (TextView) layout.findViewById(R.id.txt_readmore);
        coverImg = (ImageView) layout.findViewById(R.id.cover_img);
        //openFragmentCallback = (OpenFragmentCallback) getActivity();
        //upArrow = (ImageView) layout.findViewById(R.id.up_arrow);
        txtTime = (TextView) layout.findViewById(R.id.txt_time);
        setImageSize();
        setContent();
        return layout;
    }

    private void setImageSize(){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        int imgHeight = Math.round(width * .75f);

        coverImg.getLayoutParams().height = imgHeight;
    }

    private void setContent(){
        txtTitle.setText(newsObj.title);
        txtDes.setText(newsObj.description);
        if (newsObj.source.equals("")){
            txtSource.setText("");
        }else {
            txtSource.setText(newsObj.source);
        }


        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy-M-dd hh:mm:ss");

        try {
            Date createdate = simpleDateFormat.parse(newsObj.startDate);
            if (newsObj.source.equals("")){
               // txtSource.setText("");
                txtTime.setText(DateFunction.timeDiffCurrent(createdate));
            }else {
                txtTime.setText(DateFunction.timeDiffCurrent(createdate)+" / ");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (newsObj.source.equals("")){
            txtReadmore.setVisibility(View.INVISIBLE);
           // upArrow.setVisibility(View.VISIBLE);
        }else {
            txtReadmore.setVisibility(View.VISIBLE);
            //upArrow.setVisibility(View.GONE);
        }

        Picasso.with(getActivity())
                .load(newsObj.coverImage)
                .fit().centerCrop()
                //.fit()
                .placeholder(R.drawable.placeholder)
                .into(coverImg);

        txtReadmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragmentCallback.openFragment("newsWebView", newsObj.sourceUrl);
            }
        });
    }
}
