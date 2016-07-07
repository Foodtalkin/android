package in.foodtalk.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import in.foodtalk.android.R;
import in.foodtalk.android.communicator.NotificationCallback;
import in.foodtalk.android.object.NotificationObj;

/**
 * Created by RetailAdmin on 04-07-2016.
 */
public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    LayoutInflater layoutInflater;
    List<NotificationObj> notiList;
    NotificationCallback notificationCallback;
    public NotificationAdapter(Context context, List<NotificationObj> notiList){
        Log.d("Noti adapter context", context+"");
        this.context = context;
        this.notiList = notiList;
        layoutInflater = LayoutInflater.from(context);
        notificationCallback = (NotificationCallback) context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.card_notification,parent, false);
        NotiHolder notiHolder = new NotiHolder(view);
        return notiHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        NotificationObj current = notiList.get(position);
        NotiHolder notiHolder = (NotiHolder) holder;
        notiHolder.raiserName = current.raiserName;
        notiHolder.eventDate = current.eventDate;
        String msg = current.message.replace(current.raiserName,"");
        notiHolder.txtMsg.setText(Html.fromHtml("<font color='#1d6bd5'>"+current.raiserName+"</font>"+msg));
        if (current.eventType == "2" || current.eventType == "4" || current.eventType == "9"){

        }
        switch (current.eventType){
            case "2":
                notiHolder.notificationIcon.setImageResource(R.drawable.like_icon_noti);
                break;
            case "4":
                notiHolder.notificationIcon.setImageResource(R.drawable.commen_icon_noti);
                break;
            case "5":
                notiHolder.notificationIcon.setImageResource(R.drawable.user_icon_noti);
                break;
            case "9":
                notiHolder.notificationIcon.setImageResource(R.drawable.mentions_icon_noti);
                break;
        }

        Picasso.with(context)
                .load(current.raiserImage)
                .fit()
                .placeholder(R.drawable.placeholder)
                .into(notiHolder.raiserImg);
    }

    @Override
    public int getItemCount() {
        return notiList.size();
    }

    class NotiHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{
        ImageView raiserImg, notificationIcon;
        TextView txtNotification;
        LinearLayout notificationHolder;
        String raiserName;
        String eventDate;
        String message;
        TextView txtMsg;
        public NotiHolder(View itemView) {
            super(itemView);

            raiserImg = (ImageView) itemView.findViewById(R.id.user_img);
            notificationIcon = (ImageView) itemView.findViewById(R.id.noti_icon);
            notificationHolder = (LinearLayout) itemView.findViewById(R.id.notification_holder);

            notificationHolder.setOnTouchListener(this);

            txtMsg = (TextView) itemView.findViewById(R.id.txt_message);
            txtMsg.setText(message);
            Log.d("txt noti",": "+message);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()){
                case R.id.notification_holder:
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            Log.d("clicked","notification");
                            notificationCallback.notiClicked(notiList.get(getPosition()).eventType, notiList.get(getPosition()).raiserImage , notiList.get(getPosition()).raiserId, notiList.get(getPosition()).eventDate, notiList.get(getPosition()).elementId);
                            break;
                    }
                    break;
            }
            return true;
        }
    }
}
