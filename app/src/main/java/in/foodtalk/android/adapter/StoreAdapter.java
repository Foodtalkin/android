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
import android.widget.LinearLayout;
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
import in.foodtalk.android.communicator.StoreCallback;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.SetDateFormat;
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
    StoreCallback storeCallback;

    private final int VIEW_OFFER = 0;

    public StoreAdapter (Context context, List<StoreObj>listStore){

        Log.d("store adapter", "list length "+listStore.size());
        this.context = context;
        this.listStore = listStore;
        layoutInflater = LayoutInflater.from(context);
        db = new DatabaseHandler(context);
        apiCallback = (ApiCallback) context;
        storeCallback = (StoreCallback) context;

    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        StoreCardHolder storeCardHolder;
        if (viewType == VIEW_OFFER){
            view = layoutInflater.inflate(R.layout.card_store1, parent, false);
            storeCardHolder = new StoreCardHolder(view);
            return storeCardHolder;
        }else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof StoreCardHolder){
            StoreObj storeObj = listStore.get(position);
            StoreCardHolder storeCardHolder = (StoreCardHolder) holder;
            Log.d("onBindV", storeObj.type+"");
            storeCardHolder.txtType.setText(storeObj.type);
            storeCardHolder.txtTitle.setText(storeObj.title);
            storeCardHolder.txtDes.setText(storeObj.description);
            storeCardHolder.txtPts.setText(storeObj.costPoints+" Pts");
            String date1 = SetDateFormat.convertFormat(storeObj.endDate,"yyyy/MM/dd HH:mm:ss","MMM dd");
            storeCardHolder.txtDate.setText(date1);
            Picasso.with(context)
                    .load(storeObj.cardImage)
                    .fit().centerCrop()
                    //.fit()
                    .placeholder(R.drawable.placeholder)
                    .into(storeCardHolder.imgCard);
        }
        /*StoreObj current = listStore.get(position);
        StoreCardHolder storeCardHolder = (StoreCardHolder) holder;
        storeCardHolder.txtDes1.setText(current.description);
        storeCardHolder.txtDes2.setText(current.description2);
        storeCardHolder.txtTitle.setText(current.title);

        storeCardHolder.type = current.type;

        double point = Double.parseDouble(current.avilablePoints);
        storeCardHolder.havePoints = (long) point;


        Picasso.with(context)
                .load(current.adImage)
                //.fit().centerCrop()
                .fit()
                .placeholder(R.drawable.placeholder)
                .into(storeCardHolder.thumbImg);*/

        /*switch (current.type){
            case "event":
                double rPoint = Double.parseDouble(current.points);
                //storeCardHolder.havePoints = (long) point;
                storeCardHolder.requiredPoints = (long) rPoint;
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
        }*/
    }
    @Override
    public int getItemCount() {
        return listStore.size();
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = -1;
        switch (listStore.get(position).type){
            case "OFFER":
                viewType = VIEW_OFFER;
                break;
        }
        return viewType;
    }

    class StoreCardHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{

        Button btn;
        String type;

        ImageView imgCard;
        TextView txtType, txtTitle, txtDes, txtDate, txtPts;

        LinearLayout btnCard;





        long requiredPoints;
        long havePoints;
        boolean purchased;

        public StoreCardHolder(View itemView) {
            super(itemView);

            imgCard = (ImageView) itemView.findViewById(R.id.img_card);
            txtType = (TextView) itemView.findViewById(R.id.txt_type);
            txtTitle = (TextView) itemView.findViewById(R.id.txt_title);
            txtDate = (TextView) itemView.findViewById(R.id.txt_date);
            txtDes = (TextView) itemView.findViewById(R.id.txt_des);
            txtPts = (TextView) itemView.findViewById(R.id.txt_pts);
            btnCard = (LinearLayout) itemView.findViewById(R.id.btn_card);

            btnCard.setOnTouchListener(this);

            // btn = (Button) itemView.findViewById(R.id.btn_book);
           // btn.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()){
                case R.id.btn_card:
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            Log.d("StoreAdapter", "card clicked");
                            storeCallback.openDetailsStore(listStore.get(getAdapterPosition()).type,listStore.get(getAdapterPosition()));
                            break;
                    }

                    break;
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
                        //--jsonObject.put("adId", listStore.get(position).id);
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
