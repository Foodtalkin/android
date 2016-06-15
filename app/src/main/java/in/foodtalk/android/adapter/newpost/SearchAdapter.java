package in.foodtalk.android.adapter.newpost;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.foodtalk.android.R;
import in.foodtalk.android.object.SearchResultObj;
import in.foodtalk.android.search.SearchResult;

/**
 * Created by RetailAdmin on 15-06-2016.
 */
public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    LayoutInflater layoutInflater;

    List<SearchResultObj> searchResultList;

    public SearchAdapter(Context context, List<SearchResultObj> searchResultList){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.searchResultList = searchResultList;
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

        resultHolder.txtName.setText(current.dishName);
        resultHolder.txtSub.setText(current.postCount+" Dishes");
    }

    @Override
    public int getItemCount() {
        return searchResultList.size();
    }

    class ResultHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        TextView txtSub;

        public ResultHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txt_name_result);
            txtSub = (TextView) itemView.findViewById(R.id.txt_sub_result);

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
