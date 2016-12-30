package in.foodtalk.android.fragment.store;

import android.support.v4.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import in.foodtalk.android.R;
import in.foodtalk.android.module.SetDateFormat;
import in.foodtalk.android.object.PurchasesObj;

/**
 * Created by RetailAdmin on 29-12-2016.
 */

public class StorePurchasesCard extends Fragment {
    View layout;

    public PurchasesObj purchasesObj;

    TextView txtEventPass, txtTitle, txtDate, txtTime, txtLocation, txtName, txtAdmitCount, txtRefNo;
    ImageView imgCard;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.store_purchases_card, container, false);
        txtEventPass = (TextView) layout.findViewById(R.id.txt_event_pass);
        txtTitle = (TextView) layout.findViewById(R.id.txt_title);
        txtDate = (TextView) layout.findViewById(R.id.txt_date);
        txtTime = (TextView) layout.findViewById(R.id.txt_time);
        txtLocation = (TextView) layout.findViewById(R.id.txt_location);
        txtName = (TextView) layout.findViewById(R.id.txt_name);
        txtAdmitCount = (TextView) layout.findViewById(R.id.txt_admit_count);
        txtRefNo = (TextView) layout.findViewById(R.id.txt_ref_no);
        imgCard = (ImageView) layout.findViewById(R.id.img_card);

        Typeface face= Typeface.createFromAsset(getActivity().getAssets(), "fonts/american_typewriter_regular.ttf");
        txtEventPass.setTypeface(face);
        txtDate.setTypeface(face);
        txtTime.setTypeface(face);
        txtLocation.setTypeface(face);
        txtName.setTypeface(face);
        txtAdmitCount.setTypeface(face);
        txtRefNo.setTypeface(face);

        setData();
        return layout;
    }

    private void setData(){
        txtTitle.setText(purchasesObj.title);
        txtDate.setText(SetDateFormat.convertFormat(purchasesObj.endDate,"yyyy/MM/dd HH:mm:ss","MMM dd, yyyy"));
        txtTime.setText(SetDateFormat.convertFormat(purchasesObj.endDate,"yyyy/MM/dd HH:mm:ss","h:mm a"));
        txtLocation.setText(purchasesObj.cityText);
        if (!purchasesObj.cardImage.equals("")){
            Picasso.with(getActivity())
                    .load(purchasesObj.cardImage)
                    .fit().centerCrop()
                    //.fit()
                    .placeholder(R.drawable.placeholder)
                    .into(imgCard);
        }
    }
}
