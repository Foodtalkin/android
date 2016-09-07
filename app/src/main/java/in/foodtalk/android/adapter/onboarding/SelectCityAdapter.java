package in.foodtalk.android.adapter.onboarding;

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
import in.foodtalk.android.communicator.SelectCityCallback;
import in.foodtalk.android.fragment.onboarding.SelectCity;
import in.foodtalk.android.object.SelectCityObj;

/**
 * Created by RetailAdmin on 06-09-2016.
 */
public class SelectCityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<SelectCityObj> cityList;
    LayoutInflater layoutInflater;
    Context context;
    SelectCityCallback selectCityCallback;
    public SelectCityAdapter(Context context, List<SelectCityObj> cityList, SelectCityCallback selectCityCallback){
        this.context = context;
        this.cityList = cityList;
        layoutInflater = LayoutInflater.from(context);
        Log.d("city adapter","length"+cityList.size());
        this.selectCityCallback = selectCityCallback;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.card_onboard_city, parent, false);
        CityHolder cityHolder = new CityHolder(view);
        return cityHolder;

    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SelectCityObj current = cityList.get(position);
        CityHolder cityHolder = (CityHolder) holder;
        cityHolder.txtCity.setText(current.description);
        cityHolder.desctiption = current.description;
        cityHolder.cityId = current.place_id;
    }
    @Override
    public int getItemCount() {
        return cityList.size();
    }

    class CityHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{
        TextView txtCity;
        LinearLayout btnCity;
        String desctiption;
        String cityId;
        public CityHolder(View itemView) {
            super(itemView);
            txtCity = (TextView) itemView.findViewById(R.id.txt_city);
            btnCity = (LinearLayout) itemView.findViewById(R.id.btn_city);
            btnCity.setOnTouchListener(this);
        }
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()){
                case R.id.btn_city:
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            Log.d("selectCity click", "city: "+ desctiption);
                            selectCityCallback.getSelectedCity(desctiption, cityId);
                            break;
                    }
                    break;
            }
            return true;
        }
    }
}
