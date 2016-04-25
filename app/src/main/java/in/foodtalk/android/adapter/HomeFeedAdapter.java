package in.foodtalk.android.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.squareup.picasso.Picasso;

import java.util.List;

import in.foodtalk.android.R;
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.object.PostObj;

/**
 * Created by RetailAdmin on 25-04-2016.
 */
public class HomeFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<PostObj> postObj;
    private LayoutInflater layoutInflater;
    private Context context;

    public HomeFeedAdapter(Context context, List<PostObj> postObj){
        layoutInflater = LayoutInflater.from(context);
        this.postObj = postObj;
        this.context = context;
        //Log.d("from adapter function", postObj.get(0).dishName);
       // Log.d("from adapter function", postObj.get(1).dishName);

        for (int i=0; postObj.size()>i;i++){
           // Log.d("from adapter function", postObj.get(i).dishName);
        }

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
        postHolder.txtHeadLine.setText(current.userName+" is having "+current.dishName+" at "+current.restaurantName);
        postHolder.txtTime.setText("4d");
        postHolder.txtCountLike.setText(current.likeCount);
        postHolder.txtCountBookmark.setText(current.bookmarkCount);
        postHolder.txtCountComment.setText(current.commentCount);

        //imgLoader(postHolder.dishImage, current.postImage);
        Picasso.with(context)
                .load(current.postImage)
                .fit().centerCrop()
                .into(postHolder.dishImage);
        Picasso.with(context)
                .load(current.userThumb)
                .into(postHolder.userThumbnail);
        /*if(current.postImage != null){
            Picasso.with(context)
                    .load(current.postImage)
                    .fit().centerCrop()
                    .into(postHolder.dishImage);
            // Log.d("value from current", myUrl);
        }else{
            //Log.d("value from current", "null");
            //postHolder.imgHolder.setImageResource(R.drawable.nav_top_img);
            Picasso.with(context)
                    .load(R.drawable.placeholder)
                    .fit().centerCrop()
                    .into(postHolder.dishImage);
        }*/

        //Log.d("from adapter", current.userName+" is having "+current.dishName+" at "+current.restaurantName);

    }
    @Override
    public int getItemCount() {
        return postObj.size();
    }


    class PostHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{

        ImageView userThumbnail;
        TextView txtHeadLine;
        TextView txtTime;
        ImageView dishImage;
        TextView txtCountLike;
        TextView txtCountBookmark;
        TextView txtCountComment;

        public PostHolder(View itemView) {
            super(itemView);
            userThumbnail = (ImageView) itemView.findViewById(R.id.userThumb);
            txtHeadLine = (TextView) itemView.findViewById(R.id.txt_post_headline);
            txtTime = (TextView) itemView.findViewById(R.id.txt_time);
            dishImage = (ImageView) itemView.findViewById(R.id.dish_img);
            txtCountLike = (TextView) itemView.findViewById(R.id.txt_count_like);
            txtCountBookmark = (TextView) itemView.findViewById(R.id.txt_count_bookmark);
            txtCountComment = (TextView) itemView.findViewById(R.id.txt_count_comment);
        }
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }

    }
}
