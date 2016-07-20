package in.foodtalk.android.adapter.newpost;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import in.foodtalk.android.R;
import in.foodtalk.android.communicator.DishTaggingCallback;
import in.foodtalk.android.module.TextValidation;
import in.foodtalk.android.object.DishListObj;
import in.foodtalk.android.object.RestaurantListObj;

/**
 * Created by RetailAdmin on 25-05-2016.
 */
public class DishTaggingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<DishListObj> dishList;
    LayoutInflater layoutInflater;

    DishTaggingCallback dishTaggingCallback;

    public DishTaggingAdapter(Context context, List<DishListObj> dishList, DishTaggingCallback dishTaggingCallback){

        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.dishList = dishList;
        this.dishTaggingCallback = dishTaggingCallback;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.card_checkin_dish, parent, false);
        DishHolder dishHolder = new DishHolder(view);


        return dishHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        DishListObj current = dishList.get(position);
        DishHolder dishHolder = (DishHolder) holder;

        dishHolder.dishname.setText(current.name);


    }

    @Override
    public int getItemCount() {
        return dishList.size();
    }

    class DishHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{

        TextView dishname;
        LinearLayout btnDish;

        public DishHolder(View itemView) {
            super(itemView);

            dishname = (TextView) itemView.findViewById(R.id.txt_dname_tagging);
            btnDish = (LinearLayout) itemView.findViewById(R.id.btn_dish_tagging);

            btnDish.setOnTouchListener(this);


        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()){
                case R.id.btn_dish_tagging:
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            Log.d("dish name", getPosition()+"");
                            dishTaggingCallback.dishNameSelected(dishList.get(getPosition()).name);
                            break;
                    }
                    break;
            }
            return true;
        }
    }

    //------------
    public void animateTo(List<DishListObj> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<DishListObj> newModels) {
        for (int i = dishList.size() - 1; i >= 0; i--) {
            final DishListObj model = dishList.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<DishListObj> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final DishListObj model = newModels.get(i);
            if (!dishList.contains(model)) {
                addItem(i, model);
            }
        }
    }
    private void applyAndAnimateMovedItems(List<DishListObj> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final DishListObj model = newModels.get(toPosition);
            final int fromPosition = dishList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public DishListObj removeItem(int position) {
        final DishListObj model = dishList.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, DishListObj model) {
        dishList.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final DishListObj model = dishList.remove(fromPosition);
        dishList.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}
