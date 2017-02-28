package in.foodtalk.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import in.foodtalk.android.R;
import in.foodtalk.android.communicator.OpenRestaurantCallback;
import in.foodtalk.android.fragment.CuratedFragment;
import in.foodtalk.android.module.GetRange;
import in.foodtalk.android.module.StringCase;
import in.foodtalk.android.object.RestaurantListObj;

/**
 * Created by RetailAdmin on 09-08-2016.
 */
public class CuratedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<RestaurantListObj> rList;
    LayoutInflater layoutInflater;
    StringCase stringCase;
    OpenRestaurantCallback openRestaurantCallback;

    public CuratedAdapter(Context context, List<RestaurantListObj> rList){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.rList = rList;
        stringCase = new StringCase();
        openRestaurantCallback = (OpenRestaurantCallback) context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.card_curated, parent, false);
        RestaurantHolder restaurantHolder = new RestaurantHolder(view);
        return restaurantHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RestaurantListObj current = rList.get(position);
        RestaurantHolder restaurantHolder = (RestaurantHolder) holder;
        restaurantHolder.rName.setText(stringCase.caseSensitive(current.restaurantName));
        restaurantHolder.rArea.setText(stringCase.caseSensitive(current.area));

        double distance = Double.parseDouble(current.distance);
        String km = new DecimalFormat("##.#").format(distance/1000);

        //postHolder.txtKm.setText(km+" KM");
        restaurantHolder.rDistance.setText(km+" KM");

        /*int price = Integer.valueOf(current.priceRange);

        String rs = context.getResources().getString(R.string.rs);

        if (price < 500){
            restaurantHolder.rRange.setText(rs+" Budget");
        }else if (price >= 500 && price < 1000){
            restaurantHolder.rRange.setText(rs+" Mid Range");
        } else {
            restaurantHolder.rRange.setText(rs+" Splurge");
        }*/

        restaurantHolder.rRange.setText(GetRange.getRangePrice(context, current.priceRange));
    }
    @Override
    public int getItemCount() {
        return rList.size();
    }

    class RestaurantHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{

        TextView rName, rArea, rDistance, rRange;
        LinearLayout btnRestaurant;
        public RestaurantHolder(View itemView) {
            super(itemView);
            rName = (TextView) itemView.findViewById(R.id.txt_name);
            rArea = (TextView) itemView.findViewById(R.id.txt_area);
            rDistance = (TextView) itemView.findViewById(R.id.txt_distance);
            rRange = (TextView) itemView.findViewById(R.id.txt_price_range);
            btnRestaurant = (LinearLayout) itemView.findViewById(R.id.restaurant_holder);
            btnRestaurant.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_UP:
                    Log.d("click", getPosition()+"");
                    openRestaurantCallback.restaurantOpen(rList.get(getPosition()).id);
                    break;
            }
            return true;
        }
    }
}
