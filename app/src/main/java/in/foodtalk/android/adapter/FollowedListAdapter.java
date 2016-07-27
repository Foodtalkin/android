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

import java.util.List;

import in.foodtalk.android.R;
import in.foodtalk.android.communicator.MentionCallback;
import in.foodtalk.android.object.FollowedUsersObj;

/**
 * Created by RetailAdmin on 27-07-2016.
 */
public class FollowedListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<FollowedUsersObj> fUserList;
    LayoutInflater layoutInflater;
    Context fContext;
    MentionCallback mentionCallback;

    public FollowedListAdapter(Context context, List<FollowedUsersObj> fUserList, MentionCallback mentionCallback){
        this.context = context;
        this.fUserList = fUserList;
        layoutInflater = LayoutInflater.from(context);
        this.fContext = fContext;
        this.mentionCallback = mentionCallback;

        Log.d("fUserList", fUserList.size()+"");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.card_mention_user, parent, false);
        UserHolder userHolder = new UserHolder(view);
        return userHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FollowedUsersObj current = fUserList.get(position);
        UserHolder userHolder = (UserHolder) holder;



        userHolder.txtUserName.setText(current.userName);
        userHolder.userId = current.id;
    }

    @Override
    public int getItemCount() {
        return fUserList.size();
    }

    class UserHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{

        TextView txtUserName;
        LinearLayout btnUser;
        String userId;

        public UserHolder(View itemView) {
            super(itemView);
            txtUserName = (TextView) itemView.findViewById(R.id.txt_user_mention);
            btnUser = (LinearLayout) itemView.findViewById(R.id.btn_user_mention);
            btnUser.setOnTouchListener(this);
        }
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()){
                case R.id.btn_user_mention:
                   // Log.d("user", fUserList.get(getPosition()).userName);
                    mentionCallback.mentionUser(fUserList.get(getPosition()).userName, fUserList.get(getPosition()).id);
                    break;
            }
            return false;
        }
    }

    //------------
    public void animateTo(List<FollowedUsersObj> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<FollowedUsersObj> newModels) {
        for (int i = fUserList.size() - 1; i >= 0; i--) {
            final FollowedUsersObj model = fUserList.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<FollowedUsersObj> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final FollowedUsersObj model = newModels.get(i);
            if (!fUserList.contains(model)) {
                addItem(i, model);
            }
        }
    }
    private void applyAndAnimateMovedItems(List<FollowedUsersObj> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final FollowedUsersObj model = newModels.get(toPosition);
            final int fromPosition = fUserList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public FollowedUsersObj removeItem(int position) {
        final FollowedUsersObj model = fUserList.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, FollowedUsersObj model) {
        fUserList.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final FollowedUsersObj model = fUserList.remove(fromPosition);
        fUserList.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}
