package in.foodtalk.android.fragment.store;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import in.foodtalk.android.R;
import in.foodtalk.android.communicator.WebpageCallback;
import in.foodtalk.android.module.DateFunction;
import in.foodtalk.android.object.PurchasesObj;

/**
 * Created by RetailAdmin on 29-12-2016.
 */

public class StorePurchasesCard extends Fragment {
    View layout;

    public PurchasesObj purchasesObj;

    TextView txtEventPass, txtTitle, txtDate, txtTime, txtCoupon, txtName, txtAdmitCount, txtRefNo, txtTapToCopy, txtUrl, txtExpired, txtAction, txtInfoDinein;
    ImageView imgCard, iconCall;

    LinearLayout couponHolder, redeemTab, redeemTab1, dineInInfo, infoRedeem;
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
        //txtAdmitCount = (TextView) layout.findViewById(R.id.txt_admit_count);
       // txtRefNo = (TextView) layout.findViewById(R.id.txt_ref_no);
        imgCard = (ImageView) layout.findViewById(R.id.img_card);
        txtTapToCopy = (TextView) layout.findViewById(R.id.txt_taptoCopy);
        txtUrl = (TextView) layout.findViewById(R.id.txt_url);
        txtExpired = (TextView) layout.findViewById(R.id.txt_expired);
        txtAction = (TextView) layout.findViewById(R.id.txt_action);
        txtInfoDinein = (TextView) layout.findViewById(R.id.txt_dine_in);
        dineInInfo = (LinearLayout) layout.findViewById(R.id.dine_in_info);
        infoRedeem = (LinearLayout) layout.findViewById(R.id.info_redeem);

        redeemTab = (LinearLayout) layout.findViewById(R.id.redeem_tab);
        redeemTab1 = (LinearLayout) layout.findViewById(R.id.redeem_tab1);


        iconCall = (ImageView) layout.findViewById(R.id.icon_call);

        webpageCallback = (WebpageCallback) getActivity();

        couponHolder = (LinearLayout) layout.findViewById(R.id.holder_coupon);

        Typeface face= Typeface.createFromAsset(getActivity().getAssets(), "fonts/american_typewriter_regular.ttf");
        txtEventPass.setTypeface(face);
        txtDate.setTypeface(face);
        txtTime.setTypeface(face);
        txtCoupon.setTypeface(face);
        txtExpired.setTypeface(face);
        txtInfoDinein.setTypeface(face);
        //txtName.setTypeface(face);
       // txtAdmitCount.setTypeface(face);
      //  txtRefNo.setTypeface(face);
        setData();
        return layout;
    }

    String couponCode;
    String redemptionUrl;
    String redemptionPhone;
    String validTill;
    String type;

    private void setData(){

        JSONObject metaData = new JSONObject();
        try {

            metaData = new JSONObject(purchasesObj.metaData);
            couponCode = metaData.getString("couponCode");
            redemptionUrl = metaData.getString("redemptionUrl");
            redemptionPhone = metaData.getString("redemptionPhone");
            validTill = metaData.getString("validTill");
            type = metaData.getString("type");

            //Log.d("My App", metaData.toString());

        } catch (Throwable t) {
            Log.e("StorePurchasesCard", "setData Could not parse malformed JSON:");
        }





        txtTitle.setText(purchasesObj.title);
        txtDate.setText(DateFunction.convertFormat(purchasesObj.endDate,"yyyy-MM-dd HH:mm:ss","MMM dd, yyyy"));
        txtTime.setText(DateFunction.convertFormat(purchasesObj.endDate,"yyyy-MM-dd HH:mm:ss","h:mm a"));
        txtCoupon.setText(couponCode);
        Log.d("StorePurchasesCard","redemptionUrl: "+ redemptionUrl);

        if (purchasesObj.type.equals("DINE-IN")){
            dineInInfo.setVisibility(View.VISIBLE);
            infoRedeem.setVisibility(View.GONE);
            iconCall.setVisibility(View.GONE);
            txtTapToCopy.setVisibility(View.GONE);
            //txtTapToCopy.setVisibility(View.INVISIBLE);
            //Log.d("StorePurchasesCard","type dine-in");
        }else {
            if (redemptionUrl.length() > 0){
                Log.d("StorePurchasesCard","this is link offer");
                txtUrl.setText(redemptionUrl);
                txtAction.setText("Tap to open");
                iconCall.setVisibility(View.GONE);
                redeemTab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        webpageCallback.inAppBrowser(false,"", redemptionUrl);
                    }
                });
            }else {
                Log.d("StorePurchasesCard","this is call offer");
                txtUrl.setText(redemptionPhone);
                txtAction.setText("Tap to call");
                iconCall.setVisibility(View.VISIBLE);
                redeemTab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //webpageCallback.inAppBrowser(false,"", redemptionUrl);
                        Log.d("StorePpurchases","call");
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+redemptionPhone));
                        startActivity(callIntent);
                    }
                });
            }
        }



       /*if (redemptionUrl != null && !redemptionUrl.equals("")){
            Log.d("StorePurchasesCard","this is link offer");
            txtUrl.setText(redemptionUrl);
            txtAction.setText("Tap to open");
            iconCall.setVisibility(View.GONE);
            redeemTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    webpageCallback.inAppBrowser(false,"", redemptionUrl);
                }
            });
        }else if (redemptionPhone != null && !redemptionPhone.equals("")){
            Log.d("StorePurchasesCard","this is call offer");
            txtUrl.setText(redemptionPhone);
            txtAction.setText("Tap to call");
            iconCall.setVisibility(View.VISIBLE);
            redeemTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //webpageCallback.inAppBrowser(false,"", redemptionUrl);
                    Log.d("StorePpurchases","call");
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+redemptionPhone));
                    startActivity(callIntent);
                }
            });
        }else {
            Log.d("StorepurchasesCard", "redemptionUrl or redemptionPhone is null/blank");
        }*/

        txtEventPass.setText(purchasesObj.type);

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
       // System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        //----
        //String dtStart = "2016-01-01 13:33:59";
       // Log.d("compairToCurrentDate", DateFunction.compareToCurrentDate("yyyy-MM-dd HH:mm:ss",dtStart)+"");


        /*SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            //Date date = format.parse(purchasesObj.endDate);
            Date date = format.parse(dtStart);
            Date todayDate = new Date();
            Log.d("compair date", compareToDay(date, todayDate)+"");

            //System.out.println(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/



        if (DateFunction.compareToCurrentDate("yyyy-MM-dd HH:mm:ss",purchasesObj.endDate) < 0){
            redeemTab.setVisibility(View.GONE);
            redeemTab1.setVisibility(View.VISIBLE);
        }else {
            if(purchasesObj.isUsed.equals("0")){
                redeemTab.setVisibility(View.VISIBLE);
                redeemTab1.setVisibility(View.GONE);
            }else {
                redeemTab.setVisibility(View.GONE);
                redeemTab1.setVisibility(View.VISIBLE);
                txtExpired.setText("redeemed");
            }
        }

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

    }
    public static int compareToDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return 0;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return sdf.format(date1).compareTo(sdf.format(date2));
    }
}
