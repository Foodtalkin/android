package in.foodtalk.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import in.foodtalk.android.R;
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.communicator.CommentCallback;
import in.foodtalk.android.communicator.OpenFragmentCallback;
import in.foodtalk.android.communicator.PostBookmarkCallback;
import in.foodtalk.android.communicator.PostLikeCallback;
import in.foodtalk.android.communicator.PostOptionCallback;
import in.foodtalk.android.communicator.UserThumbCallback;
import in.foodtalk.android.module.DateTimeDifference;
import in.foodtalk.android.module.HeadSpannable;
import in.foodtalk.android.object.PostObj;

/**
 * Created by RetailAdmin on 25-04-2016.
 */
public class HomeFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>   {

    public List<PostObj> postObj;
    private LayoutInflater layoutInflater;
    private Context context;

    private long lastTouchTime = -1;

    PostLikeCallback likeCallback;
    PostBookmarkCallback bookmarkCallback;
    PostOptionCallback optionCallback;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    LinearLayout starHolder;

    HeadSpannable headSpannable;
    UserThumbCallback userThumbCallback;

    CommentCallback commentCallback;

    DateTimeDifference dateTimeDifference;

    OpenFragmentCallback openFragmentCallback;

    public HomeFeedAdapter(Context context, List<PostObj> postObj, PostLikeCallback postLikeCallback){
        layoutInflater = LayoutInflater.from(context);
        this.postObj = postObj;
        this.context = context;
        //Log.d("from adapter function", postObj.get(0).dishName);
       // Log.d("from adapter function", postObj.get(1).dishName);

        likeCallback = (PostLikeCallback) context;
        bookmarkCallback = (PostBookmarkCallback) context;
        optionCallback = (PostOptionCallback) context;

        openFragmentCallback = (OpenFragmentCallback) context;

        headSpannable = new HeadSpannable(context);
        userThumbCallback = (UserThumbCallback) context;

        commentCallback = (CommentCallback) context;
        //likeCallback = postLikeCallback;

        dateTimeDifference = new DateTimeDifference();

    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PostHolder postHolder;
        ProgressViewHolder progressViewHolder;

        if(viewType == VIEW_ITEM){
            View view = layoutInflater.inflate(R.layout.card_view_post, parent, false);
            postHolder = new PostHolder(view);
            return postHolder;
        }else if(viewType == VIEW_PROG) {
            View view = layoutInflater.inflate(R.layout.progress_load_more, parent, false);
            progressViewHolder = new ProgressViewHolder(view);
            return progressViewHolder;
        }else {
            return null;
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof PostHolder){
            PostObj current = postObj.get(position);
            PostHolder postHolder = (PostHolder) holder;
            //postHolder.txtHeadLine.setText(current.userName+" is having "+current.dishName+" at "+current.restaurantName);
            if (current.restaurantName.equals("")){
                String htmlHeadline = "<font color='#0369db'>"+current.userName+"</font> <font color='#1a1a1a'>is having </font><font color='#0369db'> "+current.dishName+"</font><font color='#1a1a1a'>.</font>";
            }else {
                String htmlHeadline = "<font color='#0369db'>"+current.userName+"</font> <font color='#1a1a1a'>is having </font><font color='#0369db'> "+current.dishName+"</font><font color='#1a1a1a'> at </font><font color='#369db'>"+current.restaurantName+"</font><font color='#1a1a1a'>.</font>";
            }

            SimpleDateFormat simpleDateFormat =
                    new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
            try {
                Date createdate = simpleDateFormat.parse(current.createDate);
                Date currentdate = simpleDateFormat.parse(current.currentDate);
                postHolder.txtTime.setText(dateTimeDifference.difference(createdate, currentdate));

               // printDifference(date1, date2);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //postHolder.txtHeadLine.setText(Html.fromHtml(htmlHeadline));

            postHolder.txtCountLike.setText(current.likeCount);
            postHolder.txtCountBookmark.setText(current.bookmarkCount);
            postHolder.txtCountComment.setText(current.commentCount);

            if (current.likeCount.equals("0") && current.commentCount.equals("0") && current.bookmarkCount.equals("0")){
                postHolder.countHolder.setVisibility(View.GONE);
            }else {
                postHolder.countHolder.setVisibility(View.VISIBLE);
            }

            if (current.likeCount.equals("0")){
                postHolder.btnLike.setVisibility(View.GONE);
            }else {
                postHolder.btnLike.setVisibility(View.VISIBLE);
            }
            if (current.commentCount.equals("0")){
                postHolder.btnComment.setVisibility(View.GONE);
            }else {
                postHolder.btnComment.setVisibility(View.VISIBLE);
            }
            if (current.bookmarkCount.equals("0")){
                postHolder.btnBookmark.setVisibility(View.GONE);
            }else {
                postHolder.btnBookmark.setVisibility(View.VISIBLE);
            }
            if (current.likeCount.equals("1")){
                postHolder.txtLikeCopy.setText("Like");
            }else {
                postHolder.txtLikeCopy.setText("Likes");
            }
            if (current.bookmarkCount.equals("1")){
                postHolder.txtBookmarkCopy.setText("Bookmark");
            }else {
                postHolder.txtBookmarkCopy.setText("Bookmarks");
            }
            if (current.commentCount.equals("1")){
                postHolder.txtCommentCopy.setText("Comment");
            }else {
                postHolder.txtCommentCopy.setText("Comments");
            }

            String reviewTip = current.tip.trim();
            if(reviewTip.equals("")){
                postHolder.txtTip.setVisibility(View.GONE);
            }else {
                postHolder.txtTip.setVisibility(View.VISIBLE);
                postHolder.txtTip.setText(reviewTip);
            }
            postHolder.userId = current.userId;

            if (current.restaurantIsActive.equals("1")) {
                if (current.region.equals("")){
                    headSpannable.code(postHolder.txtHeadLine, current.userName, current.dishName, current.restaurantName, current.userId, current.checkedInRestaurantId, true , "HomeFeed");
                }else {
                    headSpannable.code(postHolder.txtHeadLine, current.userName, current.dishName, current.restaurantName+", "+current.region, current.userId, current.checkedInRestaurantId, true , "HomeFeed");
                }
            }else {
                if (current.region.equals("")){
                    headSpannable.code(postHolder.txtHeadLine, current.userName, current.dishName, current.restaurantName, current.userId, current.checkedInRestaurantId, false, "HomeFeed");
                }else {
                    headSpannable.code(postHolder.txtHeadLine, current.userName, current.dishName, current.restaurantName+", "+current.region, current.userId, current.checkedInRestaurantId, false, "HomeFeed");
                }
            }

            setStarRating(current.rating, postHolder);
            //postHolder.postId = current.id;
            postHolder.postObj1 = current;

            if(current.iLikedIt != null){
                if (current.iLikedIt.equals("1")){
                    postHolder.likeIconImg.setImageResource(R.drawable.ic_heart_filled);
                    //postHolder.likeIconImg.setColorFilter(ContextCompat.getColor(context,R.color.com_facebook_blue));
                }else {
                    postHolder.likeIconImg.setImageResource(R.drawable.ic_like_card_24);
                }
            }else{
                Log.e("HomeFeedAdapter","null iLikeIt position: "+ position);
            }
            if(current.iBookark != null){
                if(current.iBookark.equals("1")){
                    postHolder.bookmarImg.setImageResource(R.drawable.ic_bookmark_filled);
                }else {
                    postHolder.bookmarImg.setImageResource(R.drawable.ic_bookmark_card_24);
                }
            }else {
                Log.e("HomeFeedAdapter","null iBookark position: "+position);
            }
            //Log.d("image url", current.postImage);
            Picasso.with(context)
                    .load(current.postImage)
                    //.fit().centerCrop()
                    .fit()
                    .placeholder(R.drawable.placeholder)
                    .into(postHolder.dishImage);

            //Log.d("userThumb large",current.userThumb);
            Picasso.with(context)
                    .load(current.userImage)
                    .placeholder(R.drawable.placeholder)
                    .into(postHolder.userThumbnail);
        }else {
           // holder.set
            ProgressViewHolder progressViewHolder = (ProgressViewHolder) holder;
            progressViewHolder.progressBar.setIndeterminate(true);
        }
    }






    @Override
    public int getItemCount() {
        return postObj.size();
    }
    public void onchange(){
        notifyDataSetChanged();
    }

    public void clear() {
        postObj.clear();
        notifyDataSetChanged();
    }
    public void addData(int position){
        notifyItemInserted(position);
    }

    public void setStarRating(String rating, PostHolder holder){
        if (rating.equals("0")){
            starHolder.setVisibility(View.GONE);
        }
        if(rating.equals("1")){
            holder.imgRating1.setImageResource(R.drawable.star_active);
            holder.imgRating2.setImageResource(R.drawable.star_passive);
            holder.imgRating3.setImageResource(R.drawable.star_passive);
            holder.imgRating4.setImageResource(R.drawable.star_passive);
            holder.imgRating5.setImageResource(R.drawable.star_passive);
        }
        if(rating.equals("2")){
            holder.imgRating1.setImageResource(R.drawable.star_active);
            holder.imgRating2.setImageResource(R.drawable.star_active);
            holder.imgRating3.setImageResource(R.drawable.star_passive);
            holder.imgRating4.setImageResource(R.drawable.star_passive);
            holder.imgRating5.setImageResource(R.drawable.star_passive);
        }
        if(rating.equals("3")){
            holder.imgRating1.setImageResource(R.drawable.star_active);
            holder.imgRating2.setImageResource(R.drawable.star_active);
            holder.imgRating3.setImageResource(R.drawable.star_active);
            holder.imgRating4.setImageResource(R.drawable.star_passive);
            holder.imgRating5.setImageResource(R.drawable.star_passive);
        }
        if(rating.equals("4")){
            holder.imgRating1.setImageResource(R.drawable.star_active);
            holder.imgRating2.setImageResource(R.drawable.star_active);
            holder.imgRating3.setImageResource(R.drawable.star_active);
            holder.imgRating4.setImageResource(R.drawable.star_active);
            holder.imgRating5.setImageResource(R.drawable.star_passive);
        }
        if(rating.equals("5")){
            holder.imgRating1.setImageResource(R.drawable.star_active);
            holder.imgRating2.setImageResource(R.drawable.star_active);
            holder.imgRating3.setImageResource(R.drawable.star_active);
            holder.imgRating4.setImageResource(R.drawable.star_active);
            holder.imgRating5.setImageResource(R.drawable.star_active);
        }
    }

    // Add a list of items
    public void addAll(List<PostObj> list) {
        //Log.d("addAll ",list.get(0).dishName);
        //postObj.clear();
        //postObj.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return postObj.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);

        }
    }


    class PostHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{

        ImageView userThumbnail;
        TextView txtHeadLine;
        TextView txtTime;
        ImageView dishImage;
        TextView txtCountLike;
        TextView txtCountBookmark;
        TextView txtCountComment;

        TextView txtTip;

        ImageView likeHeart;
        ImageView likeIconImg;
        ImageView bookmarImg;
        Animation mAnimation;

        ImageView imgRating1;
        ImageView imgRating2;
        ImageView imgRating3;
        ImageView imgRating4;
        ImageView imgRating5;

        String userId;



        //String postId;
        PostObj postObj1;

        LinearLayout iconLike, iconBookmark, iconComment, iconShare, iconOption, btnLike, btnBookmark, btnComment, btnDetails;

        TextView txtLikeCopy, txtCommentCopy, txtBookmarkCopy;

        LinearLayout countHolder;

        public PostHolder(final View itemView) {
            super(itemView);
            txtLikeCopy = (TextView) itemView.findViewById(R.id.txt_like_copy);
            txtCommentCopy = (TextView) itemView.findViewById(R.id.txt_comment_copy);
            txtBookmarkCopy = (TextView) itemView.findViewById(R.id.txt_bookmark_copy);
            userThumbnail = (ImageView) itemView.findViewById(R.id.userThumb);
            txtHeadLine = (TextView) itemView.findViewById(R.id.txt_post_headline);
            txtTime = (TextView) itemView.findViewById(R.id.txt_time);
            dishImage = (ImageView) itemView.findViewById(R.id.dish_img);
            txtCountLike = (TextView) itemView.findViewById(R.id.txt_like_count);
            txtCountBookmark = (TextView) itemView.findViewById(R.id.txt_bookmark_count);
            txtCountComment = (TextView) itemView.findViewById(R.id.txt_comment_count);
            likeIconImg = (ImageView) itemView.findViewById(R.id.icon_heart_img);
            txtTip = (TextView) itemView.findViewById(R.id.txt_tip);
            bookmarImg = (ImageView) itemView.findViewById(R.id.img_icon_bookmark);

            countHolder = (LinearLayout) itemView.findViewById(R.id.count_holder);

            btnLike = (LinearLayout) itemView.findViewById(R.id.btn_like);
            btnBookmark = (LinearLayout) itemView.findViewById(R.id.btn_bookmark);
            btnComment = (LinearLayout) itemView.findViewById(R.id.btn_comment);


            likeHeart = (ImageView) itemView.findViewById(R.id.like_heart);

            imgRating1 = (ImageView) itemView.findViewById(R.id.img_rating1);
            imgRating2 = (ImageView) itemView.findViewById(R.id.img_rating2);
            imgRating3 = (ImageView) itemView.findViewById(R.id.img_rating3);
            imgRating4 = (ImageView) itemView.findViewById(R.id.img_rating4);
            imgRating5 = (ImageView) itemView.findViewById(R.id.img_rating5);

            btnDetails = (LinearLayout) itemView.findViewById(R.id.btn_details);

            starHolder = (LinearLayout) itemView.findViewById(R.id.star_rating_holder);


            iconLike = (LinearLayout) itemView.findViewById(R.id.icon_like_holder);
            iconBookmark = (LinearLayout) itemView.findViewById(R.id.icon_bookmark_holder);
            iconComment = (LinearLayout) itemView.findViewById(R.id.icon_comment_holder);
            iconOption = (LinearLayout) itemView.findViewById(R.id.icon_option_holder);
            iconShare = (LinearLayout) itemView.findViewById(R.id.icon_share_holder);


            dishImage.setOnTouchListener(this);
            iconLike.setOnTouchListener(this);
            iconBookmark.setOnTouchListener(this);
            iconComment.setOnTouchListener(this);
            iconOption.setOnTouchListener(this);
            userThumbnail.setOnTouchListener(this);
            iconShare.setOnTouchListener(this);

            //btnLike.setOnTouchListener(this);
           // btnBookmark.setOnTouchListener(this);
           // btnComment.setOnTouchListener(this);

            btnDetails.setOnTouchListener(this);







            mAnimation = AnimationUtils.loadAnimation(context, R.anim.like_anim);

            mAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation arg0) {
                    //Functionality here
                    new CountDownTimer(500, 1000) {
                        public void onFinish() {
                            // When timer is finished
                            // Execute your code here
                            Log.d("timer","finish");
                            likeHeart.setVisibility(itemView.GONE);
                        }
                        public void onTick(long millisUntilFinished) {
                            // millisUntilFinished    The amount of time until finished.
                            Log.d("timer","onTicker");
                        }
                    }.start();
                }
                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()){
                case R.id.dish_img: {
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:{
                            //Log.d("clicked", "dish image"+ getPosition());
                            long thisTime = System.currentTimeMillis();
                            if (thisTime - lastTouchTime < 250) {
                                Log.d("clicked", "img double");

                                likeHeart.setVisibility(View.VISIBLE);
                                likeHeart.startAnimation(mAnimation);
                                if (postObj1.iLikedIt.equals("0")){
                                    //-----update image when click on like icon--
                                    likeIconImg.setImageResource(R.drawable.ic_heart_filled);
                                    String likeCount = String.valueOf(Integer.parseInt(txtCountLike.getText().toString())+1);
                                    txtCountLike.setText(likeCount);

                                    //----update postObj for runtime-----------
                                    postObj1.iLikedIt = "1";
                                    postObj1.likeCount = likeCount;
                                    postObj.set(getPosition(), postObj1);
                                    //------------------------------------------
                                    if(likeCallback != null){
                                        likeCallback.like(getPosition(), postObj1.id, true);
                                    }else{
                                        Log.e("HomeFeedAdapter","null likeCallback");
                                    }
                                }

                                // Double click
                                //p = mapView.getProjection().fromPixels((int) e.getX(), (int) e.getY());
                                lastTouchTime = -1;
                            } else {
                                // too slow
                                Log.d("clicked", "img single");
                                lastTouchTime = thisTime;
                            }
                        }
                        break;
                    }
                }
                break;
                case R.id.icon_like_holder:{
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            Log.d("clicked", "icon like");
                            if (postObj1.iLikedIt.equals("0")){
                                likeIconImg.setImageResource(R.drawable.ic_heart_filled);
                                String likeCount = String.valueOf(Integer.parseInt(txtCountLike.getText().toString())+1);
                                txtCountLike.setText(likeCount);

                                //----update postObj for runtime-----------
                                postObj1.iLikedIt = "1";
                                postObj1.likeCount = likeCount;
                                postObj.set(getPosition(), postObj1);
                                //------------------------------------------
                                if(likeCallback != null){
                                    likeCallback.like(getPosition(), postObj1.id, true);
                                }else{
                                    Log.e("HomeFeedAdapter","null likeCallback");
                                }
                            }else {
                                likeIconImg.setImageResource(R.drawable.ic_like_card_24);
                                String likeCount = String.valueOf(Integer.parseInt(txtCountLike.getText().toString())-1);
                                txtCountLike.setText(likeCount);

                                //----update postObj for runtime-----------
                                postObj1.iLikedIt = "0";
                                postObj1.likeCount = likeCount;
                                postObj.set(getPosition(), postObj1);
                                //------------------------------------------
                                if(likeCallback != null){
                                    likeCallback.like(getPosition(), postObj1.id, false);
                                }else{
                                    Log.e("HomeFeedAdapter","null likeCallback");
                                }
                            }
                            break;
                    }
                }
                break;
                case R.id.icon_bookmark_holder:{
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            Log.d("clicked", "icon bookmark");
                            if(postObj1.iBookark.equals("0")){
                                String bookmarkCount = String.valueOf(Integer.parseInt(txtCountBookmark.getText().toString())+1);
                                txtCountBookmark.setText(bookmarkCount);

                                bookmarImg.setImageResource(R.drawable.ic_bookmark_filled);

                                //----update postObj for runtime-----------
                                postObj1.iBookark = "1";
                                postObj1.bookmarkCount = bookmarkCount;
                                postObj.set(getPosition(), postObj1);
                                //------------------------------------------
                                bookmarkCallback.bookmark(getPosition(),postObj1.id, true);
                            }else {
                                String bookmarkCount = String.valueOf(Integer.parseInt(txtCountBookmark.getText().toString())-1);
                                txtCountBookmark.setText(bookmarkCount);

                                bookmarImg.setImageResource(R.drawable.ic_bookmark_card_24);

                                //----update postObj for runtime-----------
                                postObj1.iBookark = "0";
                                postObj1.bookmarkCount = bookmarkCount;
                                postObj.set(getPosition(), postObj1);
                                //------------------------------------------
                                bookmarkCallback.bookmark(getPosition(),postObj1.id, false);
                            }
                            break;
                    }
                }
                break;
                case R.id.icon_comment_holder:{
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            Log.d("clicked", "icon comment");
                           //commentCallback.openComment(postObj1.id);
                            openFragmentCallback.openFragment("commentListPost", postObj1.id);
                            break;
                    }
                }
                break;
                case R.id.icon_option_holder:{
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            Log.d("clicked", "post user id"+postObj1.userId +"post id: "+postObj1.id );
                            optionCallback.option(getPosition(),postObj1.id,postObj1.userId);
                            break;
                    }
                }
                break;
                case R.id.icon_share_holder:{
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            share(postObj1.id);
                            break;
                    }
                }
                break;
                case R.id.userThumb:
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            Log.d("clicked", "user thumnails");
                            userThumbCallback.thumbClick(userId);
                            break;
                    }
                    break;
                case R.id.btn_like:
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            Log.d("HomeFeedAdapter", "btn like clicked");

                            break;
                    }
                    break;
                case R.id.btn_bookmark:
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            Log.d("HomeFeedAdapter", "btn bookmark clicked");
                            break;
                    }
                    break;
                case R.id.btn_comment:
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            Log.d("HomeFeedAdapter", "btn comment clicked");
                            break;
                    }
                    break;
                case R.id.btn_details:
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            openFragmentCallback.openFragment("postDetails", postObj1.id);
                            break;
                    }
                    break;
            }
            return true;
        }
    }
    private void share(String postId){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "http://foodtalk.in/post/"+postId);
        context.startActivity(Intent.createChooser(shareIntent, "Share link using"));
    }
}
