package in.foodtalk.android.adapter.newpost;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import in.foodtalk.android.R;
import in.foodtalk.android.communicator.SearchResultCallback;
import in.foodtalk.android.object.SearchResultObj;
import in.foodtalk.android.search.SearchResult;

/**
 * Created by RetailAdmin on 15-06-2016.
 */
public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    LayoutInflater layoutInflater;

    List<SearchResultObj> searchResultList;
    int pageNumber;

    static final int DISH_SEARCH = 0;
    static final int USER_SEARCH = 1;
    static final int RESTAURANT_SEARCH = 2;

    SearchResultCallback searchResultCallback;

    public SearchAdapter(Context context, List<SearchResultObj> searchResultList, int pageNumber){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.searchResultList = searchResultList;
        this.pageNumber = pageNumber;
        searchResultCallback = (SearchResultCallback) context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.card_search_result, parent, false);
        ResultHolder resultHolder = new ResultHolder(view);

        return resultHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SearchResultObj current = searchResultList.get(position);
        ResultHolder resultHolder = (ResultHolder) holder;

        switch (pageNumber){
            case DISH_SEARCH:
                resultHolder.userThumb.setVisibility(View.GONE);
                resultHolder.dishName = current.txt1;
                break;
            case USER_SEARCH:
                resultHolder.userThumb.setVisibility(View.VISIBLE);
                //resultHolder.userThumb.setImageResource();
                Picasso.with(context)
                        .load(current.image)
                        //.fit().centerCrop()
                        .fit()
                        .placeholder(R.drawable.user_placeholder)
                        .into(resultHolder.userThumb);
                break;
            case RESTAURANT_SEARCH:
                resultHolder.userThumb.setVisibility(View.GONE);
                break;
        }

        resultHolder.id = current.id;




        resultHolder.txtName.setText(current.txt1);
        resultHolder.txtSub.setText(current.txt2);
    }

    @Override
    public int getItemCount() {
        return searchResultList.size();
    }

    class ResultHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {
        TextView txtName;
        TextView txtSub;
        ImageView userThumb;
        String id;
        LinearLayout btnCard;
        String dishName;

        public ResultHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txt_name_result);
            txtSub = (TextView) itemView.findViewById(R.id.txt_sub_result);
            userThumb = (ImageView) itemView.findViewById(R.id.userThumb_search);
            btnCard = (LinearLayout) itemView.findViewById(R.id.card_search_result);

            btnCard.setOnTouchListener(this);

        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()){
                case R.id.card_search_result:
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            Log.d("clicked on result","call");
                            searchResultCallback.resultClick(pageNumber, id, dishName);
                            break;
                    }
                    break;
            }
            return true;
        }
    }


    //------------
    public void animateTo(List<SearchResultObj> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<SearchResultObj> newModels) {
        for (int i = searchResultList.size() - 1; i >= 0; i--) {
            final SearchResultObj model = searchResultList.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<SearchResultObj> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final SearchResultObj model = newModels.get(i);
            if (!searchResultList.contains(model)) {
                addItem(i, model);
            }
        }
    }
    private void applyAndAnimateMovedItems(List<SearchResultObj> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final SearchResultObj model = newModels.get(toPosition);
            final int fromPosition = searchResultList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public SearchResultObj removeItem(int position) {
        final SearchResultObj model = searchResultList.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, SearchResultObj model) {
        searchResultList.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final SearchResultObj model = searchResultList.remove(fromPosition);
        searchResultList.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}
