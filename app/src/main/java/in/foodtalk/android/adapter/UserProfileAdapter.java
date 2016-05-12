package in.foodtalk.android.adapter;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import in.foodtalk.android.R;
import in.foodtalk.android.communicator.ProfilePostOpenCallback;
import in.foodtalk.android.module.StringCase;
import in.foodtalk.android.object.UserPostObj;
import in.foodtalk.android.object.UserProfileObj;

/**
 * Created by RetailAdmin on 06-05-2016.
 */
public class UserProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<UserPostObj> postList;
    public UserProfileObj userProfileObj;
    private LayoutInflater layoutInflater;
    private Context context;

    private int width;
    private int height;

    private final int VIEW_PROFILE = 0;
    private final int VIEW_POST = 1;
    private final int VIEW_PROGRESS = 2;
    private final int VIEW_ERROR = 3;

    private StringCase stringCase;
    private String userId;

    private ProfilePostOpenCallback postOpenCallback;
    public UserProfileAdapter (Context context, List<UserPostObj> postList, UserProfileObj userProfile){
        layoutInflater = LayoutInflater.from(context);
        this.postList = postList;
        this.context = context;
        this.userProfileObj = userProfile;


        postOpenCallback = (ProfilePostOpenCallback) context;
        //Log.d("profile data", postList.size()+"");
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        width = size.y;
        stringCase = new StringCase();
        //Log.d("get screen size", "width: "+size.x +" height: "+size.y);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ProfileHolder profileHolder;
        PostHolderProfile postHolder;
        ProgressViewHolder progressViewHolder;
        ErrorCopyHolder errorCopyHolder;
        if (viewType == VIEW_PROFILE){
            View view = layoutInflater.inflate(R.layout.card_user_info, parent, false);
            profileHolder = new ProfileHolder(view);
            return profileHolder;
        }else if (viewType == VIEW_POST){
            View view = layoutInflater.inflate(R.layout.card_user_post_holder, parent,false);
            postHolder = new PostHolderProfile(view);
            return postHolder;
        }else if(viewType == VIEW_PROGRESS){
            View view = layoutInflater.inflate(R.layout.progress_load_more, parent,false);
            progressViewHolder = new ProgressViewHolder(view);
            return progressViewHolder;
        }else if(viewType == VIEW_ERROR){
            View view = layoutInflater.inflate(R.layout.error_copy, parent,false);
            errorCopyHolder = new ErrorCopyHolder(view);
            return errorCopyHolder;
        }else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProfileHolder){
            ProfileHolder profileHolder = (ProfileHolder) holder;
            profileHolder.fullName.setText(stringCase.caseSensitive(userProfileObj.fullName));
            profileHolder.checkins.setText(userProfileObj.checkInCount);
            profileHolder.followers.setText(userProfileObj.followersCount);
            profileHolder.following.setText(userProfileObj.followingCount);
            userId = userProfileObj.userId;
            //Log.d("profile img", userProfileObj.image);
            Picasso.with(context)
                    .load(userProfileObj.image)
                    //.fit().centerCrop()
                    .fit()
                    .placeholder(R.drawable.user_placeholder)
                    .into(profileHolder.profileImg);
            final ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams sglp = (StaggeredGridLayoutManager.LayoutParams) lp;
                sglp.setFullSpan(true);
                holder.itemView.setLayoutParams(sglp);
            }

        }else if (holder instanceof PostHolderProfile){
            UserPostObj current = postList.get(position);
            PostHolderProfile postHolderProfile = (PostHolderProfile) holder;

            //float scale = context.getResources().getDisplayMetrics().density;
            //px = dp_that_you_want * (scale / 160);

            postHolderProfile.userId = current.userId;

            postHolderProfile.postImg.getLayoutParams().width = width/3-200;
            postHolderProfile.postImg.getLayoutParams().height = width/3-200;
            Picasso.with(context)
                    .load(current.postImage)
                    //.fit().centerCrop()
                    .fit()
                    .placeholder(R.drawable.placeholder)
                    .into(postHolderProfile.postImg);
        } else if(holder instanceof ProgressViewHolder){
            ProgressViewHolder progressViewHolder = (ProgressViewHolder) holder;
            progressViewHolder.progressBar.setIndeterminate(true);
            final ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams sglp = (StaggeredGridLayoutManager.LayoutParams) lp;
                sglp.setFullSpan(true);
                holder.itemView.setLayoutParams(sglp);
            }
        }else if (holder instanceof ErrorCopyHolder){
            ErrorCopyHolder errorCopyHolder = (ErrorCopyHolder) holder;
            errorCopyHolder.txtErrorCopy.setText(R.string.user_profile_copy);
            spanItem(holder);
        }
    }
    public void spanItem(RecyclerView.ViewHolder holder){
        final ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams sglp = (StaggeredGridLayoutManager.LayoutParams) lp;
            sglp.setFullSpan(true);
            holder.itemView.setLayoutParams(sglp);
        }
    }
    @Override
    public int getItemCount() {
        return postList.size();
    }
    @Override
    public int getItemViewType(int position) {
        int viewType = 1;
        if(postList.get(position)!= null){
            if(postList.get(position).viewType.equals("profileInfo")){
                viewType = VIEW_PROFILE;
            }else if(postList.get(position).viewType.equals("postImg")){
                viewType = VIEW_POST;
            }else if(postList.get(position).viewType.equals("errorCopy")){
                viewType = VIEW_ERROR;
            }
        } else  {
            viewType = VIEW_PROGRESS;
        }
        return viewType;
    }
    class ProfileHolder extends RecyclerView.ViewHolder{
        ImageView profileImg;
        TextView fullName;
        TextView checkins;
        TextView followers;
        TextView following;
        public ProfileHolder(View itemView) {
            super(itemView);
            profileImg = (ImageView) itemView.findViewById(R.id.user_img_profile);
            fullName = (TextView) itemView.findViewById(R.id.full_name_profile);
            checkins = (TextView) itemView.findViewById(R.id.txt_checkins_profile);
            followers = (TextView) itemView.findViewById(R.id.txt_followers_profile);
            following = (TextView) itemView.findViewById(R.id.txt_following_profile);
        }
    }
    class PostHolderProfile extends RecyclerView.ViewHolder implements View.OnTouchListener {
        ImageView postImg;
        String userId;
        public PostHolderProfile(View itemView) {
            super(itemView);
            postImg = (ImageView) itemView.findViewById(R.id.img_user_post);
            postImg.setOnTouchListener(this);
        }
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()){
                case R.id.img_user_post:
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            //Log.d("image clicked",getPosition()+"");
                            postOpenCallback.postOpen(postList, String.valueOf(getPosition()), userId);
                            break;
                    }
                    break;
            }
            return true;
        }
    }
    class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public ProgressViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);
        }
    }
    class ErrorCopyHolder extends RecyclerView.ViewHolder{
        public TextView txtErrorCopy;
        public ErrorCopyHolder(View itemView) {
            super(itemView);
            txtErrorCopy = (TextView) itemView.findViewById(R.id.txt_error_copy);
        }
    }
}
