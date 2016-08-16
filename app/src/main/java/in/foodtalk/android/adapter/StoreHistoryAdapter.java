package in.foodtalk.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.foodtalk.android.R;
import in.foodtalk.android.fragment.StoreHistoryFragment;
import in.foodtalk.android.object.StoreHistoryObj;

/**
 * Created by RetailAdmin on 16-08-2016.
 */
public class StoreHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<StoreHistoryObj> storeHistoryList;
    LayoutInflater layoutInflater;

    public StoreHistoryAdapter(Context context, List<StoreHistoryObj> storeHistoryList){
        this.context = context;
        this.storeHistoryList = storeHistoryList;
        layoutInflater = LayoutInflater.from(context);

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
        }else if (current.type.equals("offers")){
            historyHolder.txtDate.setText(current.couponCode);
        }

    }

    @Override
    public int getItemCount() {
        return storeHistoryList.size();
    }
    class HistoryHolder extends RecyclerView.ViewHolder {

        TextView txtDate, txtName, txtDetail, txtValue;
        public HistoryHolder(View itemView) {
            super(itemView);
            txtDate = (TextView) itemView.findViewById(R.id.txt_date);
            txtName = (TextView) itemView.findViewById(R.id.txt_name);
            txtDetail = (TextView) itemView.findViewById(R.id.txt_detail);
            txtValue = (TextView) itemView.findViewById(R.id.txt_value);
        }
    }
}
