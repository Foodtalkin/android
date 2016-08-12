package in.foodtalk.android.adapter.newpost;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.fitness.request.ListSubscriptionsRequest;

import java.util.List;

import in.foodtalk.android.R;
import in.foodtalk.android.object.StoreObj;

/**
 * Created by RetailAdmin on 12-08-2016.
 */
public class StoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<StoreObj> listStore;
    LayoutInflater layoutInflater;

    public StoreAdapter (Context context, List<StoreObj>listStore){
        this.context = context;
        this.listStore = listStore;
        layoutInflater = LayoutInflater.from(context);

    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.card_store, parent, false);
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return listStore.size();
    }

    class StoreCardHolder extends RecyclerView.ViewHolder{
        TextView txtDes1, txtDes2, txtTitle, txtPoints;
        ImageView thumbImg;
        Button btn;

        public StoreCardHolder(View itemView) {
            super(itemView);

        }
    }
}
