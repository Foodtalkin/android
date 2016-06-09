package in.foodtalk.android.adapter.newpost;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import in.foodtalk.android.R;
import in.foodtalk.android.communicator.CityListCallback;
import in.foodtalk.android.module.StringCase;

/**
 * Created by RetailAdmin on 07-06-2016.
 */
public class CityListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    LayoutInflater layoutInflater;

    StringCase stringCase;

    CityListCallback cityListCallback;
    ArrayList<String> cityList = new ArrayList<String>();
    public CityListAdapter(Context context, ArrayList<String> cityList, CityListCallback cityListCallback){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);

        this.cityListCallback = cityListCallback;

        Log.d("citylist", cityList+" "+ cityList.size());

        stringCase = new StringCase();

        this.cityList = cityList;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.card_city_list, parent, false);
        CityListHolder cityListHolder = new CityListHolder(view);
        return cityListHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CityListHolder cityListHolder = (CityListHolder) holder;

        cityListHolder.txtCity.setText(stringCase.caseSensitive(cityList.get(position)));

    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    class CityListHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{
        TextView txtCity;
        public CityListHolder(View itemView) {
            super(itemView);

            txtCity = (TextView) itemView.findViewById(R.id.txt_city_list);
            txtCity.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()){
                case R.id.txt_city_list:
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            //Log.d("on clicked","city "+cityList.get(getPosition()));
                            cityListCallback.selectCity(cityList.get(getPosition()));

                            break;
                    }
                    break;
            }
            return true;
        }
    }
}
