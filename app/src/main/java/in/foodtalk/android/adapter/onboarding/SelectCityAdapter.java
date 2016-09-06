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
import in.foodtalk.android.fragment.onboarding.SelectCity;
import in.foodtalk.android.object.SelectCityObj;

/**
 * Created by RetailAdmin on 06-09-2016.
 */
public class SelectCityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<SelectCityObj> cityList;
    LayoutInflater layoutInflater;
    Context context;
    public SelectCityAdapter(Context context, List<SelectCityObj> cityList){
        this.context = context;
        this.cityList = cityList;
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
    }
    @Override
    public int getItemCount() {
        return cityList.size();
    }

    class CityHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{
        TextView txtCity;
        LinearLayout btnCity;
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
                    Log.d("selectCity click", "clicked");
                    break;
            }
            return true;
        }
    }
}
