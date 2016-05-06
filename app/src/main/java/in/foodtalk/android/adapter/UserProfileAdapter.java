package in.foodtalk.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import in.foodtalk.android.object.UserPostObj;

/**
 * Created by RetailAdmin on 06-05-2016.
 */
public class UserProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<UserPostObj> postList;
    private LayoutInflater layoutInflater;
    private Context context;

    private final int VIEW_PROFILE = 0;
    private final int VIEW_POST = 1;
    public UserProfileAdapter (Context context, List<UserPostObj> postList){
        layoutInflater = LayoutInflater.from(context);
        this.postList = postList;
        this.context = context;

    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ProfileHolder extends RecyclerView.ViewHolder{

        public ProfileHolder(View itemView) {
            super(itemView);
        }
    }
    class PostHolder extends RecyclerView.ViewHolder {
        public PostHolder(View itemView) {
            super(itemView);
        }
    }
}
