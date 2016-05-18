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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import in.foodtalk.android.R;
import in.foodtalk.android.communicator.PhoneCallback;
import in.foodtalk.android.communicator.ProfilePostOpenCallback;
import in.foodtalk.android.communicator.ProfileRPostOpenCallback;
import in.foodtalk.android.object.RestaurantPostObj;
import in.foodtalk.android.object.RestaurantProfileObj;


/**
 * Created by RetailAdmin on 17-05-2016.
 */
public class RestaurantProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<RestaurantPostObj> postList = new ArrayList<>();
    public RestaurantProfileObj rProfileObj;
    private LayoutInflater layoutInflater;
    private Context context;

    private int width;
    private int height;

    private final int VIEW_PROFILE = 0;
    private final int VIEW_POST = 1;
    private final int VIEW_PROGRESS = 2;
    private final int VIEW_ERROR = 3;

    PhoneCallback phoneCallback;

    private ProfileRPostOpenCallback postOpenCallback;

    public RestaurantProfileAdapter (Context context, List<RestaurantPostObj> rPostList, RestaurantProfileObj rProfileObj){
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.postList = rPostList;
        this.rProfileObj = rProfileObj;

        postOpenCallback = (ProfileRPostOpenCallback) context;
        phoneCallback = (PhoneCallback) context;



        //------------
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        width = size.y;
        //----------
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RPostHolder rPostHolder;
        RProfileHolder rProfileHolder;
        ProgressViewHolder progressViewHolder;
        ErrorCopyHolder errorCopyHolder;

        if (viewType == VIEW_PROFILE){
            View view = layoutInflater.inflate(R.layout.card_restaurant_info, parent, false);
            rProfileHolder = new RProfileHolder(view);
            return rProfileHolder;
        }else if (viewType == VIEW_POST){
            View view = layoutInflater.inflate(R.layout.card_profile_post_holder, parent, false);
            rPostHolder = new RPostHolder(view);
            return rPostHolder;
        }else if (viewType == VIEW_PROGRESS){
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
        if (holder instanceof RProfileHolder){
            RProfileHolder rProfileHolder = (RProfileHolder) holder;
            rProfileHolder.rName.setText(rProfileObj.restaurantName);
            rProfileHolder.rAddress.setText(rProfileObj.address);
            rProfileHolder.checkinCount.setText(rProfileObj.checkInCount+" Checkin");
            Log.d("checkinCount", rProfileObj.checkInCount+"");
            rProfileHolder.phone1 = rProfileObj.phone1;
            rProfileHolder.phone2 = rProfileObj.phone2;




            final ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams sglp = (StaggeredGridLayoutManager.LayoutParams) lp;
                sglp.setFullSpan(true);
                holder.itemView.setLayoutParams(sglp);
            }
        }else if (holder instanceof RPostHolder){
            RestaurantPostObj current = postList.get(position);
            RPostHolder rPostHolder = (RPostHolder) holder;

            rPostHolder.postImg.getLayoutParams().width = width/3-200;
            rPostHolder.postImg.getLayoutParams().height = width/3-200;

            rPostHolder.restaurantId = current.checkedInRestaurantId;

            Picasso.with(context)
                    .load(current.postImage)
                    //.fit().centerCrop()
                    .fit()
                    .placeholder(R.drawable.placeholder)
                    .into(rPostHolder.postImg);
        }else if(holder instanceof ProgressViewHolder){
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
    class RPostHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {
        ImageView postImg;
        String restaurantId;

        public RPostHolder(View itemView) {
            super(itemView);
            postImg = (ImageView) itemView.findViewById(R.id.img_profile_post);
            postImg.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()){
                case R.id.img_profile_post:
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            Log.d("image clicked",getPosition()+" "+postList.size());
                            postOpenCallback.rPostOpen(postList, String.valueOf(getPosition()), restaurantId);
                            //postOpenCallback.postOpen(postList, String.valueOf(getPosition()), userId);
                            break;
                    }
                    break;
            }
            return true;
        }
    }

    class RProfileHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{

        TextView rName;
        TextView rAddress;
        TextView checkinCount;
        String phone1;
        String phone2;
        Button btnCall;

        public RProfileHolder(View itemView) {
            super(itemView);
            rName = (TextView) itemView.findViewById(R.id.txt_name_restaurant);
            rAddress = (TextView) itemView.findViewById(R.id.txt_address_restaurant);
            checkinCount = (TextView) itemView.findViewById(R.id.checkin_view_restaurant);
            btnCall = (Button) itemView.findViewById(R.id.btn_call_restaurant);
            btnCall.setOnTouchListener(this);


        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()){
                case R.id.btn_call_restaurant:
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            phoneCallback.phoneBtn(phone1, phone2);
                            Log.d("clicked","call button");

                            break;
                    }
                    break;
            }
            return false;
        }
    }
    class ProgressViewHolder extends RecyclerView.ViewHolder{
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
