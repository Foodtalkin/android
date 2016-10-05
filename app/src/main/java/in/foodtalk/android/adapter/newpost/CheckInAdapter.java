package in.foodtalk.android.adapter.newpost;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import in.foodtalk.android.R;
import in.foodtalk.android.communicator.CheckInCallback;
import in.foodtalk.android.module.StringCase;
import in.foodtalk.android.object.RestaurantListObj;

/**
 * Created by RetailAdmin on 19-05-2016.
 */
public class CheckInAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<RestaurantListObj> rList;

    LayoutInflater layoutInflater;

    StringCase stringCase;

    CheckInCallback checkInCallback;


    public CheckInAdapter(Context context, List<RestaurantListObj> restaurantListObjs){
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.rList = restaurantListObjs;

        checkInCallback = (CheckInCallback) context;

        stringCase = new StringCase();

        //Log.d("adapter", "total list "+ restaurantListObjs.size());
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
        restaurantHolder.txtRName.setText(stringCase.caseSensitive(current.restaurantName));
        //Log.d("restaurantIsActive", current.restaurantIsActive+"");
        if (current.restaurantIsActive != null){
            if (current.restaurantIsActive.equals("true")){
                restaurantHolder.txtAria.setText(current.area);
                restaurantHolder.txtAria.setTextColor(context.getResources().getColor(R.color.blackText1));
            }else {
                restaurantHolder.txtAria.setText("Unverified");
                restaurantHolder.txtAria.setTextColor(Color.RED);
            }
        }
        restaurantHolder.id = current.id;
    }

    @Override
    public int getItemCount() {
        return rList.size();
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
            btnRestaurant.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()){
                case MotionEvent.ACTION_UP:
                    switch (v.getId()){
                        case R.id.btn_restaurant_checkin:
                            Log.d("btn_restaurant_checkin", getPosition()+"");
                            Log.d("clicked","name"+rList.get(getPosition()).restaurantName);
                            checkInCallback.checkInRestaurant(rList.get(getPosition()).id, rList.get(getPosition()).restaurantName);
                            break;
                    }
                    break;
            }
            return true;
        }
    }

    //------------
    public void animateTo(List<RestaurantListObj> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<RestaurantListObj> newModels) {
        for (int i = rList.size() - 1; i >= 0; i--) {
            final RestaurantListObj model = rList.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<RestaurantListObj> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final RestaurantListObj model = newModels.get(i);
            if (!rList.contains(model)) {
                addItem(i, model);
            }
        }
    }
    private void applyAndAnimateMovedItems(List<RestaurantListObj> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final RestaurantListObj model = newModels.get(toPosition);
            final int fromPosition = rList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public RestaurantListObj removeItem(int position) {
        final RestaurantListObj model = rList.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, RestaurantListObj model) {
        rList.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final RestaurantListObj model = rList.remove(fromPosition);
        rList.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}
