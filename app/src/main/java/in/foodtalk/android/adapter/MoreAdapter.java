package in.foodtalk.android.adapter;

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
import in.foodtalk.android.communicator.MoreBtnCallback;
import in.foodtalk.android.object.FavoritesObj;
import in.foodtalk.android.object.UserProfileObj;

/**
 * Created by RetailAdmin on 05-05-2016.
 */
public class MoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<FavoritesObj> favorites;
    public LayoutInflater layoutInflater;
    public UserProfileObj userProfile;
    public Context context;

    String userName, fullName, userImage;

    MoreBtnCallback moreBtnCallback;


    public MoreAdapter(Context context, UserProfileObj userProfile, List<FavoritesObj> favorites ){
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.favorites = favorites;
        this.userProfile = userProfile;

        userName = userProfile.userName;
        userImage = userProfile.userImage;
        fullName = userProfile.fullName;

        moreBtnCallback = (MoreBtnCallback) context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.card_more_user, parent, false);
        ProfileHolder profileHolder = new ProfileHolder(view);

        return profileHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ProfileHolder profileHolder = (ProfileHolder) holder;
        profileHolder.txtUserName.setText(userName);
        String upperString = fullName.substring(0,1).toUpperCase() + fullName.substring(1);

        Log.d("position of space name", fullName.indexOf(" ")+"");
        profileHolder.txtUserNameFull.setText(upperString);

        Log.d("userImage",userImage);

        Picasso.with(context)
                .load(userImage)
                .placeholder(R.drawable.more_legal)
                .fit()
                .into(profileHolder.userThumb);
    }
    @Override
    public int getItemCount() {
        return 1;
    }
    class  ProfileHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {
        LinearLayout btnProfile, btnFav, btnLoc, btnOpt, btnReport, btnLegal;
        TextView txtUserName, txtUserNameFull;
        ImageView userThumb;

        public ProfileHolder(View itemView) {
            super(itemView);

            btnProfile = (LinearLayout) itemView.findViewById(R.id.btn_profile_more);
            btnFav = (LinearLayout) itemView.findViewById(R.id.btn_fav_more);
            btnLoc = (LinearLayout) itemView.findViewById(R.id.btn_loc_more);
            btnReport = (LinearLayout) itemView.findViewById(R.id.btn_report_more);
            btnOpt = (LinearLayout) itemView.findViewById(R.id.btn_loc_more);
            btnLegal = (LinearLayout) itemView.findViewById(R.id.btn_legal_more);

            userThumb = (ImageView) itemView.findViewById(R.id.userThumb_more);

            txtUserName = (TextView) itemView.findViewById(R.id.txt_username_more);
            txtUserNameFull = (TextView) itemView.findViewById(R.id.txt_username_full_more);

            btnProfile.setOnTouchListener(this);
            btnFav.setOnTouchListener(this);
            btnLoc.setOnTouchListener(this);
            btnOpt.setOnTouchListener(this);
            btnReport.setOnTouchListener(this);
            btnLegal.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()){
                case R.id.btn_profile_more:{
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            Log.d("profile", "down");

                            break;
                        case MotionEvent.ACTION_UP:
                            Log.d("profile", "up");
                            moreBtnCallback.btnClick("profile", getPosition());
                            break;
                    }
                }
                    break;
                case R.id.btn_fav_more:
                    Log.d("btnClick", "Fav");
                    break;
                case R.id.btn_loc_more:
                    Log.d("btnClick", "location");
                    break;
                case R.id.btn_option_more:
                    Log.d("btnClick", "option");
                    break;
                case R.id.btn_report_more:
                    Log.d("btnClick", "report");
                    break;
                case R.id.btn_legal_more:
                    Log.d("btnClick", "legal");
                    break;
            }
            return true;
        }
    }
}
