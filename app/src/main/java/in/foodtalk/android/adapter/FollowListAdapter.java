package in.foodtalk.android.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
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

import org.json.JSONException;

import java.util.List;

import in.foodtalk.android.R;
import in.foodtalk.android.apicall.UserFollow;
import in.foodtalk.android.communicator.OpenFragmentCallback;
import in.foodtalk.android.object.FollowListObj;
import in.foodtalk.android.object.LikeListObj;

/**
 * Created by RetailAdmin on 30-11-2016.
 */

public class FollowListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<FollowListObj> followList;
    Context context;
    LayoutInflater layoutInflater;
    OpenFragmentCallback openFragmentCallback;
    UserFollow userFollow;
    String tag;
    public FollowListAdapter(Context context, List<FollowListObj> followList, String tag){
        this.context = context;
        this.followList = followList;
        layoutInflater = layoutInflater.from(context);
        openFragmentCallback = (OpenFragmentCallback) context;
        userFollow = new UserFollow(context);
        this.tag = tag;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.card_like,parent, false);
        FollowHolder followHolder = new FollowHolder(view);
        return followHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FollowListObj current = followList.get(position);
        FollowHolder followHolder = (FollowHolder) holder;
        followHolder.txtUsername.setText(current.userName);
        followHolder.txtFullname.setText(current.fullName);
        followHolder.iFollowIt = current.iFollowIt;
        followHolder.userId = current.id;
        if (current.iFollowIt != null){
            if (current.iFollowIt.equals("0")){
                followHolder.txtFollow.setText("Follow");
                followHolder.txtFollow.setTextColor(ContextCompat.getColor(context, R.color.active));
            }else {
                followHolder.txtFollow.setText("Following");
                followHolder.txtFollow.setTextColor(ContextCompat.getColor(context, R.color.positive));
            }
        }else {
            followHolder.txtFollow.setVisibility(View.GONE);
        }
        if (!current.image.equals("") && current.image != null){
            Picasso.with(context)
                    .load(current.image)
                    .fit()
                    .placeholder(R.drawable.user_placeholder)
                    .into(followHolder.userThumb);
        }
    }

    @Override
    public int getItemCount() {
        return followList.size();
    }

    class FollowHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{
        TextView txtUsername, txtFullname, txtFollow;
        ImageView userThumb;
        String iFollowIt;
        String userId;
        LinearLayout btnUser;

        public FollowHolder(View itemView) {
            super(itemView);
            txtUsername = (TextView) itemView.findViewById(R.id.txt_username);
            txtFullname = (TextView) itemView.findViewById(R.id.txt_fullname);
            txtFollow = (TextView) itemView.findViewById(R.id.txt_follow);
            userThumb = (ImageView) itemView.findViewById(R.id.user_thumb);
            btnUser = (LinearLayout) itemView.findViewById(R.id.btn_user);

            btnUser.setOnTouchListener(this);
            txtFollow.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()){
                case R.id.txt_follow:
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            if (iFollowIt.equals("0")){
                                Log.d("Likelist adapter", "follow");
                                FollowListObj followListObj = followList.get(getPosition());
                                followListObj.iFollowIt = "1";
                                iFollowIt = "1";
                                followList.set(getPosition(),followListObj);
                                txtFollow.setText("Following");
                                txtFollow.setTextColor(ContextCompat.getColor(context, R.color.positive));
                                try {
                                    userFollow.follow(true, userId);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else if (iFollowIt.equals("1")){
                                FollowListObj followListObj = followList.get(getPosition());
                                followListObj.iFollowIt = "0";
                                iFollowIt = "0";
                                followList.set(getPosition(),followListObj);
                                txtFollow.setText("Follow");
                                Log.d("Likelist adapter", "unfollow");
                                txtFollow.setTextColor(ContextCompat.getColor(context, R.color.active));
                                try {
                                    userFollow.follow(false, userId);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                    }
                    break;
                case R.id.btn_user:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_UP:
                            Log.d("LikeListPostAdapter","Acton up clicked");
                            openFragmentCallback.openFragment("openUserProfile",followList.get(getAdapterPosition()).id);
                            break;
                    }
                    break;
            }
            return true;
        }
    }
}
