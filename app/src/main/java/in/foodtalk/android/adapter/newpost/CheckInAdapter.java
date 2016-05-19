package in.foodtalk.android.adapter.newpost;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import in.foodtalk.android.R;
import in.foodtalk.android.object.RestaurantListObj;

/**
 * Created by RetailAdmin on 19-05-2016.
 */
public class CheckInAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<RestaurantListObj> rList;

    LayoutInflater layoutInflater;


    public CheckInAdapter(Context context, List<RestaurantListObj> restaurantListObjs){
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.rList = restaurantListObjs;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = layoutInflater.inflate(R.layout.card_checkin_restaurant, parent, false);
        RestaurantHolder restaurantHolder = new RestaurantHolder(view);
        return restaurantHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RestaurantListObj current = rList.get(position);
        RestaurantHolder restaurantHolder = (RestaurantHolder) holder;

        restaurantHolder.txtRName.setText(current.restaurantName);
        restaurantHolder.txtAria.setText(current.area);
        restaurantHolder.id = current.id;

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class RestaurantHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{

        TextView txtRName;
        TextView txtAria;
        LinearLayout btnRestaurant;

        String id;
        public RestaurantHolder(View itemView) {
            super(itemView);

            txtRName = (TextView) itemView.findViewById(R.id.txt_rname_checkin);
            txtAria = (TextView) itemView.findViewById(R.id.txt_aria_checkin);
            btnRestaurant = (LinearLayout) itemView.findViewById(R.id.btn_restaurant_checkin);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }
    }
}
