package in.foodtalk.android.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
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
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.TimeUnit;

import in.foodtalk.android.R;
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.communicator.PostBookmarkCallback;
import in.foodtalk.android.communicator.PostLikeCallback;
import in.foodtalk.android.communicator.PostOptionCallback;
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

    public HomeFeedAdapter(Context context, List<PostObj> postObj, PostLikeCallback postLikeCallback){
        layoutInflater = LayoutInflater.from(context);
        this.postObj = postObj;
        this.context = context;
        //Log.d("from adapter function", postObj.get(0).dishName);
       // Log.d("from adapter function", postObj.get(1).dishName);

        likeCallback = (PostLikeCallback) context;
        bookmarkCallback = (PostBookmarkCallback) context;
        optionCallback = (PostOptionCallback) context;
        //likeCallback = postLikeCallback;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.card_view_post, parent, false);
        PostHolder postHolder = new PostHolder(view);
        return postHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PostObj current = postObj.get(position);
        PostHolder postHolder = (PostHolder) holder;
        //postHolder.txtHeadLine.setText(current.userName+" is having "+current.dishName+" at "+current.restaurantName);
        String htmlHeadline = "<font color='#0369db'>"+current.userName+"</font> <font color='#1a1a1a'>is having </font><font color='#0369db'> "+current.dishName+"</font><font color='#1a1a1a'> at </font><font color='#369db'>"+current.restaurantName+"</font><font color='#1a1a1a'>.</font>";
        postHolder.txtHeadLine.setText(Html.fromHtml(htmlHeadline));
        postHolder.txtTime.setText("4d");
        postHolder.txtCountLike.setText(current.likeCount);
        postHolder.txtCountBookmark.setText(current.bookmarkCount);
        postHolder.txtCountComment.setText(current.commentCount);

        //postHolder.postId = current.id;
        postHolder.postObj1 = current;

        if(current.iLikedIt != null){
            if (current.iLikedIt.equals("1")){
                postHolder.likeIconImg.setImageResource(R.drawable.heart_active);
            }else {
                postHolder.likeIconImg.setImageResource(R.drawable.heart);
            }
        }else{
            Log.e("HomeFeedAdapter","null iLikeIt position: "+ position);
        }
        if(current.iBookark != null){
            if(current.iBookark.equals("1")){
                postHolder.bookmarImg.setImageResource(R.drawable.bookmark_active);
            }else {
                postHolder.bookmarImg.setImageResource(R.drawable.bookmark);
            }
        }else {
            Log.e("HomeFeedAdapter","null iBookark position: "+position);
        }




        Log.d("image url", current.postImage);
        Picasso.with(context)
                .load(current.postImage)
                //.fit().centerCrop()
                .fit()
                .placeholder(R.drawable.placeholder)
                .into(postHolder.dishImage);
        Picasso.with(context)
                .load(current.userThumb)
                .placeholder(R.drawable.placeholder)
                .into(postHolder.userThumbnail);

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

    // Add a list of items
    public void addAll(List<PostObj> list) {
        Log.d("addAll ",list.get(0).dishName);
        postObj.addAll(list);
        notifyDataSetChanged();
    }


    class PostHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{

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

        //String postId;
        PostObj postObj1;

        LinearLayout iconLike, iconBookmark, iconComment, iconOption;

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

            likeHeart = (ImageView) itemView.findViewById(R.id.like_heart);



            iconLike = (LinearLayout) itemView.findViewById(R.id.icon_like_holder);
            iconBookmark = (LinearLayout) itemView.findViewById(R.id.icon_bookmark_holder);
            iconComment = (LinearLayout) itemView.findViewById(R.id.icon_comment_holder);
            iconOption = (LinearLayout) itemView.findViewById(R.id.icon_option_holder);

            dishImage.setOnTouchListener(this);
            iconLike.setOnTouchListener(this);
            iconBookmark.setOnTouchListener(this);
            iconComment.setOnTouchListener(this);
            iconOption.setOnTouchListener(this);


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
                            Log.d("clicked", "dish image"+ getPosition());
                            long thisTime = System.currentTimeMillis();
                            if (thisTime - lastTouchTime < 250) {
                                Log.d("clicked", "img double");

                                likeHeart.setVisibility(View.VISIBLE);
                                likeHeart.startAnimation(mAnimation);
                                if (postObj1.iLikedIt.equals("0")){
                                    //-----update image when click on like icon--
                                    likeIconImg.setImageResource(R.drawable.heart_active);
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
                                likeIconImg.setImageResource(R.drawable.heart_active);
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
                                likeIconImg.setImageResource(R.drawable.heart);
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

                                bookmarImg.setImageResource(R.drawable.bookmark_active);

                                //----update postObj for runtime-----------
                                postObj1.iBookark = "1";
                                postObj1.bookmarkCount = bookmarkCount;
                                postObj.set(getPosition(), postObj1);
                                //------------------------------------------
                                bookmarkCallback.bookmark(getPosition(),postObj1.id, true);
                            }else {
                                String bookmarkCount = String.valueOf(Integer.parseInt(txtCountBookmark.getText().toString())-1);
                                txtCountBookmark.setText(bookmarkCount);

                                bookmarImg.setImageResource(R.drawable.bookmark);

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
            }
            return true;
        }
    }
}
