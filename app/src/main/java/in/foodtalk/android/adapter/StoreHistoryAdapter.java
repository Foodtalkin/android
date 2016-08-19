package in.foodtalk.android.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import in.foodtalk.android.R;
import in.foodtalk.android.communicator.StoreCallback;
import in.foodtalk.android.fragment.StoreHistoryFragment;
import in.foodtalk.android.module.ToastShow;
import in.foodtalk.android.object.StoreHistoryObj;

/**
 * Created by RetailAdmin on 16-08-2016.
 */
public class StoreHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<StoreHistoryObj> storeHistoryList;
    LayoutInflater layoutInflater;
    StoreCallback storeCallback;

    public StoreHistoryAdapter(Context context, List<StoreHistoryObj> storeHistoryList){
        this.context = context;
        this.storeHistoryList = storeHistoryList;
        layoutInflater = LayoutInflater.from(context);
        storeCallback = (StoreCallback) context;

    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.card_store_history, parent, false);
        HistoryHolder historyHolder = new HistoryHolder(view);
        return historyHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        StoreHistoryObj current = storeHistoryList.get(position);
        HistoryHolder historyHolder = (HistoryHolder) holder;
        historyHolder.txtDate.setText(current.bookedOn);
        historyHolder.txtName.setText(current.title);
        if (current.type.equals("event")){
            historyHolder.txtValue.setText(current.points+" Point paid");
            historyHolder.txtDetail.setText("Your name will be on our guestlist.");
            historyHolder.holderOffer.setVisibility(View.GONE);
        }else if (current.type.equals("offers")){
            historyHolder.holderOffer.setVisibility(View.VISIBLE);
            historyHolder.txtDetail.setText(current.description2);
            historyHolder.txtDate.setText(current.bookedOn);
            historyHolder.txtCoupon.setText(current.couponCode);
        }

    }

    @Override
    public int getItemCount() {
        return storeHistoryList.size();
    }
    class HistoryHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

        TextView txtDate, txtName, txtDetail, txtValue, txtCoupon, btnBuyNow;
        LinearLayout holderOffer, holderCoupon;
        public HistoryHolder(View itemView) {
            super(itemView);
            holderOffer = (LinearLayout) itemView.findViewById(R.id.holder_offer);
            txtDate = (TextView) itemView.findViewById(R.id.txt_date);
            txtName = (TextView) itemView.findViewById(R.id.txt_name);
            txtDetail = (TextView) itemView.findViewById(R.id.txt_detail);
            txtValue = (TextView) itemView.findViewById(R.id.txt_value);
            btnBuyNow = (TextView) itemView.findViewById(R.id.btn_buy_now);
            txtCoupon = (TextView) itemView.findViewById(R.id.txt_coupon);
            holderCoupon = (LinearLayout) itemView.findViewById(R.id.holder_coupon);
            holderCoupon.setOnTouchListener(this);
            btnBuyNow.setOnTouchListener(this);
        }
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()){
                case R.id.holder_coupon:
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            copyToClipboard("coupon", txtCoupon.getText().toString());
                            break;
                    }
                    break;
                case R.id.btn_buy_now:
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            Log.d("storeHistory adapter","clicked buynow");
                            storeCallback.storeHistory(storeHistoryList.get(getPosition()).type, storeHistoryList.get(getPosition()).paymentUrl);
                            break;
                    }
                    break;
            }
            return true;
        }
    }
    private void copyToClipboard(String label, String text){
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);

        new ToastShow().showToast(context, "Copied.. "+ text);
       // Toast.makeText(context,"copied "+text,Toast.LENGTH_SHORT).show();
    }
}