package in.foodtalk.android.fragment.store;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import in.foodtalk.android.R;
import in.foodtalk.android.communicator.WebpageCallback;
import in.foodtalk.android.module.SetDateFormat;
import in.foodtalk.android.object.PurchasesObj;

/**
 * Created by RetailAdmin on 29-12-2016.
 */

public class StorePurchasesCard extends Fragment {
    View layout;

    public PurchasesObj purchasesObj;

    TextView txtEventPass, txtTitle, txtDate, txtTime, txtCoupon, txtName, txtAdmitCount, txtRefNo, txtTapToCopy, txtUrl;
    ImageView imgCard;

    LinearLayout couponHolder, redeemTab;
    WebpageCallback webpageCallback;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.store_purchases_card, container, false);
        txtEventPass = (TextView) layout.findViewById(R.id.txt_event_pass);
        txtTitle = (TextView) layout.findViewById(R.id.txt_title);
        txtDate = (TextView) layout.findViewById(R.id.txt_date);
        txtTime = (TextView) layout.findViewById(R.id.txt_time);
        txtCoupon = (TextView) layout.findViewById(R.id.txt_coupon);
        txtName = (TextView) layout.findViewById(R.id.txt_name);
        txtAdmitCount = (TextView) layout.findViewById(R.id.txt_admit_count);
        txtRefNo = (TextView) layout.findViewById(R.id.txt_ref_no);
        imgCard = (ImageView) layout.findViewById(R.id.img_card);
        txtTapToCopy = (TextView) layout.findViewById(R.id.txt_taptoCopy);
        txtUrl = (TextView) layout.findViewById(R.id.txt_url);

        redeemTab = (LinearLayout) layout.findViewById(R.id.redeem_tab);

        webpageCallback = (WebpageCallback) getActivity();

        couponHolder = (LinearLayout) layout.findViewById(R.id.holder_coupon);

        Typeface face= Typeface.createFromAsset(getActivity().getAssets(), "fonts/american_typewriter_regular.ttf");
        txtEventPass.setTypeface(face);
        txtDate.setTypeface(face);
        txtTime.setTypeface(face);
        txtCoupon.setTypeface(face);
        //txtName.setTypeface(face);
        txtAdmitCount.setTypeface(face);
        txtRefNo.setTypeface(face);



        setData();
        return layout;
    }

    String couponCode;
    String redemptionUrl;
    String validTill;
    String type;

    private void setData(){

        JSONObject metaData = new JSONObject();
        try {

            metaData = new JSONObject(purchasesObj.metaData);
            couponCode = metaData.getString("couponCode");
            redemptionUrl = metaData.getString("redemptionUrl");
            validTill = metaData.getString("validTill");
            type = metaData.getString("type");

            //Log.d("My App", metaData.toString());

        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON: \"" + purchasesObj.metaData + "\"");
        }





        txtTitle.setText(purchasesObj.title);
        txtDate.setText(SetDateFormat.convertFormat(purchasesObj.endDate,"yyyy/MM/dd HH:mm:ss","MMM dd, yyyy"));
        txtTime.setText(SetDateFormat.convertFormat(purchasesObj.endDate,"yyyy/MM/dd HH:mm:ss","h:mm a"));
        txtCoupon.setText(couponCode);
        txtUrl.setText(redemptionUrl);

        if (!purchasesObj.cardImage.equals("")){
            Picasso.with(getActivity())
                    .load(purchasesObj.cardImage)
                    .fit().centerCrop()
                    //.fit()
                    .placeholder(R.drawable.placeholder)
                    .into(imgCard);
        }

        //---compair dates--------
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        //Log.d("compair date", compareToDay(Date date1, Date date2))




        final ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);


        couponHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("storePurchasesCard","clicked coupon");
                txtTapToCopy.setText("copied");
                ClipData clip = ClipData.newPlainText("Coupon", couponCode);
                clipboard.setPrimaryClip(clip);
            }
        });
        redeemTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webpageCallback.inAppBrowser(false,"", redemptionUrl);
            }
        });
    }
    public static int compareToDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return 0;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return sdf.format(date1).compareTo(sdf.format(date2));
    }
}
