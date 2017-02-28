package in.foodtalk.android.adapter;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import in.foodtalk.android.R;
import in.foodtalk.android.communicator.CommentNewCallback;
import in.foodtalk.android.communicator.HeadSpannableCallback;
import in.foodtalk.android.communicator.PostBookmarkCallback;
import in.foodtalk.android.communicator.PostLikeCallback;
import in.foodtalk.android.communicator.PostOptionCallback;
import in.foodtalk.android.communicator.UserThumbCallback;
import in.foodtalk.android.module.DateTimeDifference;
import in.foodtalk.android.module.HeadSpannable;
import in.foodtalk.android.object.CommentObj;
import in.foodtalk.android.object.PostObj;

/**
 * Created by RetailAdmin on 22-06-2016.
 */
public class CommentAdapterNew extends RecyclerView.Adapter<RecyclerView.ViewHolder>   {

    List<CommentObj> postDataList;
    Context context;
    PostObj postObj;
    LayoutInflater layoutInflater;

    PostLikeCallback likeCallback;
    PostBookmarkCallback bookmarkCallback;
    PostOptionCallback optionCallback;

    HeadSpannable headSpannable;
    UserThumbCallback userThumbCallback;

    private long lastTouchTime = -1;

    private final int VIEW_POST = 0;
    private final int VIEW_COMMENT = 1;

    LinearLayout starHolder;

    DateTimeDifference dateTimeDifference;

    HeadSpannable spannable;
    HeadSpannableCallback headSpannableCallback;

    CommentNewCallback commentNewCallback;





    public CommentAdapterNew(Context context, List<CommentObj> postDataList, PostObj postObj, CommentNewCallback commentNewCallback){
        layoutInflater = LayoutInflater.from(context);
        this.postObj = postObj;
        this.postDataList = postDataList;

        headSpannableCallback = (HeadSpannableCallback) context;

        this.commentNewCallback = commentNewCallback;

        this.context = context;
        headSpannable = new HeadSpannable(context);

        userThumbCallback = (UserThumbCallback) context;

        likeCallback = (PostLikeCallback) context;
        bookmarkCallback = (PostBookmarkCallback) context;
        optionCallback = (PostOptionCallback) context;
        dateTimeDifference = new DateTimeDifference();

        spannable = new HeadSpannable(context);


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PostHolder postHolder;
        CommentHolder commentHolder;
        if (viewType == VIEW_POST){
            View view = layoutInflater.inflate(R.layout.card_view_post_comment, parent, false);
            postHolder = new PostHolder(view);
            return postHolder;
        }else {
            View view = layoutInflater.inflate(R.layout.card_comment, parent, false);
            commentHolder = new CommentHolder(view);
            return commentHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PostHolder){
            PostHolder postHolder = (PostHolder) holder;
           // postHolder.txtTime.setText(dateTimeDifference.difference(createdate, currentdate));
            postHolder.txtCountLike.setText(postObj.like_count);
            postHolder.txtTip.setText(postObj.tip);
            Log.d("likeCount", postObj.like_count+" like");
            postHolder.txtCountBookmark.setText(postObj.bookmarkCount);
            Log.d("commentCount", postObj.comment_count+" comment");
            postHolder.txtCountComment.setText(postObj.comment_count);

            postHolder.userId = postObj.userId;



            SimpleDateFormat simpleDateFormat =
                    new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
            try {
                Date createdate = simpleDateFormat.parse(postObj.createDate);
                Date currentdate = simpleDateFormat.parse(postObj.currentDate);
                postHolder.txtTime.setText(dateTimeDifference.difference(createdate, currentdate));

                // printDifference(date1, date2);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (postObj.restaurantIsActive.equals("1")) {
                headSpannable.code(postHolder.txtHeadLine, postObj.userName, postObj.dishName, postObj.restaurantName, postObj.userId, postObj.checkedInRestaurantId, true , "commentFragment");
            }else {
                headSpannable.code(postHolder.txtHeadLine, postObj.userName, postObj.dishName, postObj.restaurantName, postObj.userId, postObj.checkedInRestaurantId, false, "commentFragment");
            }

           setStarRating(postObj.rating, postHolder);
            //postHolder.postId = current.id;
           // postHolder.postObj1 = current;

            if(postObj.iLikedIt != null){
                if (postObj.iLikedIt.equals("1")){
                    postHolder.likeIconImg.setImageResource(R.drawable.heart_active);
                }else {
                    postHolder.likeIconImg.setImageResource(R.drawable.heart);
                }
            }else{
                Log.e("HomeFeedAdapter","null iLikeIt position: "+ position);
            }
            if(postObj.iBookark != null){
                if(postObj.iBookark.equals("1")){
                    postHolder.bookmarImg.setImageResource(R.drawable.bookmark_active);
                }else {
                    postHolder.bookmarImg.setImageResource(R.drawable.bookmark);
                }
            }else {
                Log.e("HomeFeedAdapter","null iBookark position: "+position);
            }
            //Log.d("image url", current.postImage);
            Picasso.with(context)
                    .load(postObj.postImage)
                    //.fit().centerCrop()
                    .fit()
                    .placeholder(R.drawable.placeholder)
                    .into(postHolder.dishImage);

            //Log.d("userThumb large",current.userThumb);
            Picasso.with(context)
                    .load(postObj.userImage)
                    .placeholder(R.drawable.placeholder)
                    .into(postHolder.userThumbnail);
        } else if (holder instanceof CommentHolder){
            CommentHolder commentHolder = (CommentHolder) holder;
            CommentObj current = postDataList.get(position);
            commentHolder.userName.setText(current.userName);
            commentHolder.fullUserName.setText(current.fullName);
            commentHolder.userId = current.userId;
            //commentHolder.txtComment.setText(current.comment);
            commentHolder.txtComment.setMovementMethod(LinkMovementMethod.getInstance());

            commentHolder.commentId = current.id;
            commentHolder.commentUserId = current.userId;


            commentHolder.userName.measure(0, 0);
            int textWidth = commentHolder.userName.getMeasuredWidth();
            commentHolder.txtComment.setText(spannable.commentSpannable(current.userName, current.userId, current.comment, current.userMentionsList, textWidth), TextView.BufferType.SPANNABLE);
            //spannable.commentSpannable(current.userName, current.comment, null);
            if (!current.userId.equals("")){

            }
            if (!current.userImage.isEmpty()){
                Picasso.with(context)
                        .load(current.userImage)
                        //.fit().centerCrop()
                        .fit()
                        .placeholder(R.drawable.user_placeholder)
                        .into(commentHolder.userThumb);
            }else {
                Log.e("CommentAdapterNew","userImage is emptry");
            }

        }
    }

    @Override
    public int getItemCount() {
        return postDataList.size();
    }



    @Override
    public int getItemViewType(int position) {
        int viewType;
        if (postDataList.get(position).viewType.equals("post")){
            viewType = 0;
        }else {
            viewType = 1;
        }
        return viewType;
    }

    public void setStarRating(String rating, PostHolder holder){
        if (rating.equals("0")){
            starHolder.setVisibility(View.GONE);
        }
        if(rating.equals("1")){
            holder.imgRating1.setVisibility(View.VISIBLE);
            holder.imgRating2.setVisibility(View.GONE);
            holder.imgRating3.setVisibility(View.GONE);
            holder.imgRating4.setVisibility(View.GONE);
            holder.imgRating5.setVisibility(View.GONE);
        }
        if(rating.equals("2")){
            holder.imgRating1.setVisibility(View.VISIBLE);
            holder.imgRating2.setVisibility(View.VISIBLE);
            holder.imgRating3.setVisibility(View.GONE);
            holder.imgRating4.setVisibility(View.GONE);
            holder.imgRating5.setVisibility(View.GONE);
        }
        if(rating.equals("3")){
            holder.imgRating1.setVisibility(View.VISIBLE);
            holder.imgRating2.setVisibility(View.VISIBLE);
            holder.imgRating3.setVisibility(View.VISIBLE);
            holder.imgRating4.setVisibility(View.GONE);
            holder.imgRating5.setVisibility(View.GONE);
        }
        if(rating.equals("4")){
            holder.imgRating1.setVisibility(View.VISIBLE);
            holder.imgRating2.setVisibility(View.VISIBLE);
            holder.imgRating3.setVisibility(View.VISIBLE);
            holder.imgRating4.setVisibility(View.VISIBLE);
            holder.imgRating5.setVisibility(View.GONE);
        }
        if(rating.equals("5")){
            holder.imgRating1.setVisibility(View.VISIBLE);
            holder.imgRating2.setVisibility(View.VISIBLE);
            holder.imgRating3.setVisibility(View.VISIBLE);
            holder.imgRating4.setVisibility(View.VISIBLE);
            holder.imgRating5.setVisibility(View.VISIBLE);
        }

    }

    public class PostHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{

        ImageView userThumbnail;
        TextView txtHeadLine;
        TextView txtTime;
        ImageView dishImage;
        TextView txtCountLike;
        TextView txtCountBookmark;
        TextView txtCountComment;

        ImageView likeHeart;
        ImageView likeIconImg;
        ImageView bookmarImg;
        Animation mAnimation;

        TextView txtTip;

        LinearLayout iconLike, iconBookmark, iconComment, iconOption;

        ImageView imgRating1;
        ImageView imgRating2;
        ImageView imgRating3;
        ImageView imgRating4;
        ImageView imgRating5;



        String userId;

        public PostHolder(final View itemView) {
            super(itemView);
            userThumbnail = (ImageView) itemView.findViewById(R.id.userThumb);
            txtHeadLine = (TextView) itemView.findViewById(R.id.txt_post_headline);
            txtTime = (TextView) itemView.findViewById(R.id.txt_time);
            dishImage = (ImageView) itemView.findViewById(R.id.dish_img);
            txtCountLike = (TextView) itemView.findViewById(R.id.txt_count_like);
            txtCountBookmark = (TextView) itemView.findViewById(R.id.txt_count_bookmark);
            txtCountComment = (TextView) itemView.findViewById(R.id.txt_count_comment);
            likeIconImg = (ImageView) itemView.findViewById(R.id.icon_heart_img);
            bookmarImg = (ImageView) itemView.findViewById(R.id.img_icon_bookmark);



            txtTip = (TextView) itemView.findViewById(R.id.txt_tip);

            likeHeart = (ImageView) itemView.findViewById(R.id.like_heart);

            iconLike = (LinearLayout) itemView.findViewById(R.id.icon_like_holder);
            iconBookmark = (LinearLayout) itemView.findViewById(R.id.icon_bookmark_holder);
            iconComment = (LinearLayout) itemView.findViewById(R.id.icon_comment_holder);
            iconOption = (LinearLayout) itemView.findViewById(R.id.icon_option_holder);

            starHolder = (LinearLayout) itemView.findViewById(R.id.star_rating_holder);

            imgRating1 = (ImageView) itemView.findViewById(R.id.img_rating1);
            imgRating2 = (ImageView) itemView.findViewById(R.id.img_rating2);
            imgRating3 = (ImageView) itemView.findViewById(R.id.img_rating3);
            imgRating4 = (ImageView) itemView.findViewById(R.id.img_rating4);
            imgRating5 = (ImageView) itemView.findViewById(R.id.img_rating5);

            dishImage.setOnTouchListener(this);
            iconLike.setOnTouchListener(this);
            iconBookmark.setOnTouchListener(this);
//            iconComment.setOnTouchListener(this);
            iconOption.setOnTouchListener(this);
            userThumbnail.setOnTouchListener(this);




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
                                if (postObj.iLikedIt.equals("0")){
                                    //-----update image when click on like icon--
                                    likeIconImg.setImageResource(R.drawable.heart_active);
                                    String likeCount = String.valueOf(Integer.parseInt(txtCountLike.getText().toString())+1);
                                    txtCountLike.setText(likeCount);

                                    //----update postObj for runtime-----------
                                    postObj.iLikedIt = "1";
                                    postObj.likeCount = likeCount;
                                    //postObj.set(getPosition(), postObj);
                                    //------------------------------------------
                                    if(likeCallback != null){
                                        likeCallback.like(getPosition(), postObj.id, true);
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
                            if (postObj.iLikedIt.equals("0")){
                                likeIconImg.setImageResource(R.drawable.heart_active);
                                String likeCount = String.valueOf(Integer.parseInt(txtCountLike.getText().toString())+1);
                                txtCountLike.setText(likeCount);

                                //----update postObj for runtime-----------
                                postObj.iLikedIt = "1";
                                postObj.likeCount = likeCount;
                               // postObj.set(getPosition(), postObj1);
                                //------------------------------------------
                                if(likeCallback != null){
                                    likeCallback.like(getPosition(), postObj.id, true);
                                }else{
                                    Log.e("HomeFeedAdapter","null likeCallback");
                                }
                            }else {
                                likeIconImg.setImageResource(R.drawable.heart);
                                String likeCount = String.valueOf(Integer.parseInt(txtCountLike.getText().toString())-1);
                                txtCountLike.setText(likeCount);

                                //----update postObj for runtime-----------
                                postObj.iLikedIt = "0";
                                postObj.likeCount = likeCount;
                               // postObj.set(getPosition(), postObj1);
                                //------------------------------------------
                                if(likeCallback != null){
                                    likeCallback.like(getPosition(), postObj.id, false);
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
                            if(postObj.iBookark.equals("0")){
                                String bookmarkCount = String.valueOf(Integer.parseInt(txtCountBookmark.getText().toString())+1);
                                txtCountBookmark.setText(bookmarkCount);

                                bookmarImg.setImageResource(R.drawable.bookmark_active);

                                //----update postObj for runtime-----------
                                postObj.iBookark = "1";
                                postObj.bookmarkCount = bookmarkCount;
                               // postObj.set(getPosition(), postObj1);
                                //------------------------------------------
                                bookmarkCallback.bookmark(getPosition(),postObj.id, true);
                            }else {
                                String bookmarkCount = String.valueOf(Integer.parseInt(txtCountBookmark.getText().toString())-1);
                                txtCountBookmark.setText(bookmarkCount);

                                bookmarImg.setImageResource(R.drawable.bookmark);

                                //----update postObj for runtime-----------
                                postObj.iBookark = "0";
                                postObj.bookmarkCount = bookmarkCount;
                                //postObj.set(getPosition(), postObj1);
                                //------------------------------------------
                                bookmarkCallback.bookmark(getPosition(),postObj.id, false);
                            }
                            break;
                    }
                }
                break;
                case R.id.icon_comment_holder:{
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            Log.d("clicked", "icon comment");
                           // commentCallback.openComment(postObj.id);
                            break;
                    }
                }
                break;
                case R.id.icon_option_holder:{
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            Log.d("clicked", "post user id"+postObj.userId +"post id: "+postObj.id );
                            optionCallback.option(getPosition(),postObj.id,postObj.userId, "CommentsPostFragment");
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
            }
            return true;
        }
    }
    private class CommentHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{

        String userId;
        ImageView userThumb;
        TextView userName;
        TextView fullUserName;
        TextView txtComment;
        private final int USER_PROFILE = 1;

        String commentId;
        String commentUserId;

        ImageView btnFlag;

        public CommentHolder(View itemView) {
            super(itemView);
            userThumb = (ImageView) itemView.findViewById(R.id.user_thumb);
            fullUserName = (TextView) itemView.findViewById(R.id.txt_username_full);
            userName = (TextView) itemView.findViewById(R.id.txt_username);
            txtComment = (TextView) itemView.findViewById(R.id.txt_comment);
            btnFlag = (ImageView) itemView.findViewById(R.id.btn_flag);
            btnFlag.setOnTouchListener(this);
            userName.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()){
                case R.id.txt_username:
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            Log.d("click user","name:"+userName+" id:"+userId);
                            headSpannableCallback.spannableTxt(userId, null, null, USER_PROFILE, "commentFragment");
                            break;
                    }
                    break;
                case R.id.btn_flag:
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            Log.d("CommentAdapterNew","btn flag clicked");
                            commentNewCallback.commentAdapter(commentUserId, commentId, getAdapterPosition());
                            break;
                    }
                    break;
            }
            return true;
        }
    }
}
