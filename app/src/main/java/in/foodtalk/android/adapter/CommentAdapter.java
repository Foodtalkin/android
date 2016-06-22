package in.foodtalk.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;
import java.util.zip.Inflater;

import in.foodtalk.android.communicator.PostBookmarkCallback;
import in.foodtalk.android.communicator.PostLikeCallback;
import in.foodtalk.android.communicator.PostOptionCallback;
import in.foodtalk.android.communicator.UserThumbCallback;
import in.foodtalk.android.module.HeadSpannable;
import in.foodtalk.android.object.CommentObj;
import in.foodtalk.android.object.PostObj;

/**
 * Created by RetailAdmin on 22-06-2016.
 */
public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>   {

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



    public CommentAdapter (Context context, List<CommentObj> postDataList, PostObj postObj){
        layoutInflater = LayoutInflater.from(context);
        this.postObj = postObj;
        this.postDataList = postDataList;

        likeCallback = (PostLikeCallback) context;
        bookmarkCallback = (PostBookmarkCallback) context;
        optionCallback = (PostOptionCallback) context;


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
}
