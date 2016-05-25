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
import in.foodtalk.android.module.TextValidation;
import in.foodtalk.android.object.DishListObj;

/**
 * Created by RetailAdmin on 25-05-2016.
 */
public class DishTaggingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<DishListObj> dishList;
    LayoutInflater layoutInflater;

    public DishTaggingAdapter(Context context, List<DishListObj> dishList){

        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.dishList = dishList;
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
                            break;
                    }
                    break;
            }
            return true;
        }
    }
}
