package in.foodtalk.android.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.fitness.request.ListSubscriptionsRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import in.foodtalk.android.R;
import in.foodtalk.android.apicall.ApiCall;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.ApiCallback;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.object.StoreObj;

/**
 * Created by RetailAdmin on 12-08-2016.
 */
public class StoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<StoreObj> listStore;
    LayoutInflater layoutInflater;
    ApiCall apiCall = new ApiCall();
    DatabaseHandler db;
    ApiCallback apiCallback;

    public StoreAdapter (Context context, List<StoreObj>listStore){
        this.context = context;
        this.listStore = listStore;
        layoutInflater = LayoutInflater.from(context);
        db = new DatabaseHandler(context);
        apiCallback = (ApiCallback) context;

    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.card_store, parent, false);
        StoreCardHolder storeCardHolder = new StoreCardHolder(view);
        return storeCardHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        StoreObj current = listStore.get(position);
        StoreCardHolder storeCardHolder = (StoreCardHolder) holder;
        storeCardHolder.txtDes1.setText(current.description);
        storeCardHolder.txtDes2.setText(current.description2);
        storeCardHolder.txtTitle.setText(current.title);

        storeCardHolder.type = current.type;

        storeCardHolder.havePoints = Integer.parseInt(current.avilablePoints);
        Log.d("store points", current.points);

        Picasso.with(context)
                .load(current.adImage)
                //.fit().centerCrop()
                .fit()
                .placeholder(R.drawable.placeholder)
                .into(storeCardHolder.thumbImg);

        switch (current.type){
            case "event":
                storeCardHolder.requiredPoints = Integer.parseInt(current.points);
                if (current.iRedeemed.equals("0")){
                    storeCardHolder.btn.setText("BOOK NOW");
                    storeCardHolder.purchased = false;
                }else {
                    storeCardHolder.purchased = true;
                    storeCardHolder.btn.setText("BOOKED");
                    storeCardHolder.btn.getBackground().setAlpha(128);
                }

                if (current.points != null && !current.points.equals("")){
                    storeCardHolder.txtPoints.setText(current.points+" Pts");
                }
                break;
            case "offers":
                storeCardHolder.requiredPoints = 0;
                if (current.iRedeemed.equals("0")){
                    storeCardHolder.purchased = false;
                    storeCardHolder.btn.setText("REDEEM");
                }else {
                    storeCardHolder.purchased = true;
                    storeCardHolder.btn.setText("REDEEMED");
                    storeCardHolder.btn.getBackground().setAlpha(128);
                }
                break;
        }
    }
    @Override
    public int getItemCount() {
        return listStore.size();
    }

    class StoreCardHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{
        TextView txtDes1, txtDes2, txtTitle, txtPoints;
        ImageView thumbImg;
        Button btn;
        String type;

        int requiredPoints;
        int havePoints;
        boolean purchased;

        public StoreCardHolder(View itemView) {
            super(itemView);

            txtDes1 = (TextView) itemView.findViewById(R.id.txt_des);
            txtDes2 = (TextView) itemView.findViewById(R.id.txt_des1);
            txtTitle = (TextView) itemView.findViewById(R.id.txt_title);
            txtPoints = (TextView) itemView.findViewById(R.id.txt_points);
            thumbImg = (ImageView) itemView.findViewById(R.id.thumb_img);
            btn = (Button) itemView.findViewById(R.id.btn_book);
            btn.setOnTouchListener(this);

        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()){
                case R.id.btn_book:
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            Log.d("store", "book clicked");
                            if (purchased){
                                apiCallback.apiResponse(null,"bookSlot");
                            }else{

                                if (type.equals("event")){
                                    Log.d("store on event", "have: "+havePoints+" required: "+requiredPoints);
                                    if (havePoints>=requiredPoints){
                                        callDialog(getPosition());
                                        Log.d("store", "have");
                                    }else {
                                        Log.d("store", "no have");
                                        noEnoughDialog();
                                    }
                                }else if (type.equals("offers")){
                                    callDialog(getPosition());
                                }
                            }
                            break;
                    }

                    break;
            }
            return true;
        }
    }

    private void callDialog(final int position){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_store);
        dialog.show();

        TextView btnCancel = (TextView) dialog.findViewById(R.id.btn_cancel);
        TextView btnYes = (TextView) dialog.findViewById(R.id.btn_yes);



        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Log.d("callDialog", "clicked cancel");
            }
        });
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("callDialog","click");
                dialog.cancel();
                String type = listStore.get(position).type;
                if (type.equals("event") || type.equals("offers")){
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("sessionId", db.getUserDetails().get("sessionId"));
                        jsonObject.put("adId", listStore.get(position).id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    apiCall.apiRequestPost(context,jsonObject, Config.URL_ADWORD_BOOKSLOT, "bookSlot", apiCallback);
                }
                Log.d("dialog",listStore.get(position).type);
            }
        });
    }
    private void noEnoughDialog(){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_store1);
        dialog.show();

        TextView btnCancel = (TextView) dialog.findViewById(R.id.btn_cancel);


        //You don't have enough points.

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

    }
}
