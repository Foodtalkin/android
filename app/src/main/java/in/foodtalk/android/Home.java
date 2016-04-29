package in.foodtalk.android;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

import in.foodtalk.android.apicall.PostBookmarkApi;
import in.foodtalk.android.apicall.PostLikeApi;
import in.foodtalk.android.apicall.PostReportApi;
import in.foodtalk.android.communicator.PostBookmarkCallback;
import in.foodtalk.android.communicator.PostDeleteCallback;
import in.foodtalk.android.communicator.PostLikeCallback;
import in.foodtalk.android.communicator.PostOptionCallback;
import in.foodtalk.android.fragment.DiscoverFragment;
import in.foodtalk.android.fragment.HomeFragment;
import in.foodtalk.android.fragment.MoreFragment;
import in.foodtalk.android.fragment.NewpostFragment;
import in.foodtalk.android.fragment.NotiFragment;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.Login;

public class Home extends AppCompatActivity implements View.OnClickListener ,
        PostLikeCallback , PostBookmarkCallback, PostOptionCallback, PostDeleteCallback{

    DatabaseHandler db;
    LinearLayout btnHome, btnDiscover, btnNewPost, btnNotifications, btnMore;
    ImageView homeIcon, discoverIcon,newpostIcon,notiIcon,moreIcon;
    TextView txtHomeIcon, txtDiscoverIcon, txtNewpostIcon, txtNotiIcon, txtMoreIcon;

    private ImageView[] icons;
    private TextView[] txtIcons;
    private int[] imgR;
    private int[] imgRA;

    private LinearLayout btnLogout;


    HomeFragment homeFragment;
    DiscoverFragment discoverFragment;
    NewpostFragment newpostFragment;
    NotiFragment notiFragment;
    MoreFragment moreFragment;

    PostLikeApi postLikeApi;
    PostBookmarkApi postBookmarkApi;
    PostReportApi postReportApi;

    Dialog dialogPost;
    LinearLayout alertReport;
    LinearLayout alertDelete;
    LinearLayout actionBtns;
    TextView btnReportAlertNo;
    TextView btnReportAlertYes;
    TextView btnDeleteAlertNo;
    TextView btnDeleteAlertYes;

    String sessionId;
    String userId;
    String currentPostUserId;
    String currentPostId;


    int pageNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHandler(getApplicationContext());

        //-------api init--------------------------
        postLikeApi = new PostLikeApi(this);
        postBookmarkApi = new PostBookmarkApi(this);
        postReportApi = new PostReportApi(this);
        //-----------------------------------------


        setContentView(R.layout.activity_home);

        btnLogout = (LinearLayout) findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(this);

        btnHome = (LinearLayout) findViewById(R.id.btn_home);
        btnDiscover = (LinearLayout) findViewById(R.id.btn_discover);
        btnNewPost = (LinearLayout) findViewById(R.id.btn_newpost);
        btnNotifications = (LinearLayout) findViewById(R.id.btn_notification);
        btnMore = (LinearLayout) findViewById(R.id.btn_more);

        homeIcon = (ImageView) findViewById(R.id.home_icon);
        discoverIcon = (ImageView) findViewById(R.id.discover_icon);
        newpostIcon = (ImageView) findViewById(R.id.newpost_icon);
        notiIcon = (ImageView) findViewById(R.id.noti_icon);
        moreIcon = (ImageView) findViewById(R.id.more_icon);

        txtHomeIcon = (TextView) findViewById(R.id.home_txt_icon);
        txtDiscoverIcon = (TextView) findViewById(R.id.discover_txt_icon);
        txtNewpostIcon = (TextView) findViewById(R.id.newpost_txt_icon);
        txtNotiIcon = (TextView) findViewById(R.id.noti_txt_icon);
        txtMoreIcon = (TextView) findViewById(R.id.more_txt_icon);

        //-----------
        icons = new ImageView[]{homeIcon, discoverIcon, newpostIcon, notiIcon, moreIcon};
        txtIcons = new TextView[]{txtHomeIcon, txtDiscoverIcon, txtNewpostIcon, txtNotiIcon, txtMoreIcon};
        imgR = new int[]{R.drawable.home,R.drawable.discover,R.drawable.newpost,R.drawable.notifications,R.drawable.more};
        imgRA = new int[]{R.drawable.home_active,R.drawable.discover_active,R.drawable.newpost_active,R.drawable.notifications_active, R.drawable.more_active};
        //----

        btnDiscover.setOnClickListener(this);
        btnNewPost.setOnClickListener(this);
        btnNotifications.setOnClickListener(this);
        btnMore.setOnClickListener(this);
        btnHome.setOnClickListener(this);
       // Log.d("getInfo",db.getRowCount()+"");
       // Log.d("get user info", db.getUserDetails().get("userName")+"");

       // Log.d("get user info", "session id: "+db.getUserDetails().get("sessionId"));
       // Log.d("get user info", "user id: "+db.getUserDetails().get("userId"));
        //Log.d("get user info", "full name: "+db.getUserDetails().get("fullName"));
       //Log.d("get user info", "user name: "+db.getUserDetails().get("userName"));

        userId = db.getUserDetails().get("userId");
        sessionId = db.getUserDetails().get("sessionId");

        homeFragment = new HomeFragment();
        discoverFragment =  new DiscoverFragment();
        newpostFragment = new NewpostFragment();
        notiFragment = new NotiFragment();
        moreFragment = new MoreFragment();

        // Add the fragment to the 'fragment_container' FrameLayout
        getFragmentManager().beginTransaction()
                .add(R.id.container, homeFragment).commit();
        pageNo = 0;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_home:
                Log.d("onClick","btn home");
                if(pageNo != 0){
                    setFragmentView(homeFragment , 0);
                    pageNo = 0;
                }
                break;
            case R.id.btn_discover:
                Log.d("onClick", "btn discover");
                if (pageNo != 1){
                    setFragmentView (discoverFragment , 1);
                    pageNo = 1;
                }
                break;
            case R.id.btn_newpost:
                if(pageNo != 2){
                    setFragmentView (newpostFragment, 2);
                    pageNo = 2;
                }
                Log.d("onClick","btn newpost");
                break;
            case R.id.btn_notification:
                if (pageNo != 3){
                    setFragmentView (notiFragment, 3);
                    pageNo = 3;
                }

                Log.d("onClick", "btn notification");
                break;
            case R.id.btn_more:
                if (pageNo != 4){
                    setFragmentView (moreFragment, 4);
                    pageNo = 4;
                }
                Log.d("onClick", "btn more");
                break;
            case R.id.btn_logout:
                Log.d("btn clicked", "logout");
                logOut();
                break;
        }
    }
    private void setFragmentView(Fragment newFragment, int pageN){
        icons[pageNo].setImageResource(imgR[pageNo]);
        icons[pageN].setImageResource(imgRA[pageN]);
        txtIcons[pageN].setTextColor(getResources().getColor(R.color.icon_txt_active));
        txtIcons[pageNo].setTextColor(getResources().getColor(R.color.icon_txt));
        //icons[pageN].setImageResource(R.drawable.home);
        // Create new fragment and transaction
        Fragment discoverF = new DiscoverFragment();
        android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.replace(R.id.container, newFragment);
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    private void logOut(){
        db.resetTables();
        Intent i = new Intent(this, FbLogin.class);
        startActivity(i);
        finish();
    }

    @Override
    public void like(int position, String postId, Boolean like) {
        Log.d("likeResponse",position+" postid: "+ postId);
        try {
            postLikeApi.postLike(postId, like);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void bookmark(int position, String postId, Boolean bookmark) {
        //Log.d("bookmark", "position"+ position);
        try {
            postBookmarkApi.postBookmark(postId, bookmark);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void option(int position, String postId, String userId) {
        Log.d("option callback","post id: "+ postId);
        showDialog(postId, userId);
    }
    private void showDialog(String postId, final String userId){

        currentPostUserId = userId;
        currentPostId = postId;
        dialogPost = new Dialog(this);
        dialogPost.setContentView(R.layout.dialog_post);

        TextView btnReport = (TextView) dialogPost.findViewById(R.id.btn_report_post);
        TextView btnCancel = (TextView) dialogPost.findViewById(R.id.btn_cancel_post);
        TextView btnDelete = (TextView) dialogPost.findViewById(R.id.btn_delete_post);
        alertReport = (LinearLayout) dialogPost.findViewById(R.id.alert_report);
        alertDelete = (LinearLayout) dialogPost.findViewById(R.id.alert_delete);
        actionBtns = (LinearLayout) dialogPost.findViewById(R.id.action_btns_dialog);

        btnReportAlertNo = (TextView) dialogPost.findViewById(R.id.btn_report_alert_no);
        btnReportAlertYes = (TextView) dialogPost.findViewById(R.id.btn_report_alert_yes);

        btnDeleteAlertNo = (TextView) dialogPost.findViewById(R.id.btn_delete_alert_no);
        btnDeleteAlertYes = (TextView) dialogPost.findViewById(R.id.btn_delete_alert_yes);

        alertReport.setVisibility(View.GONE);
        alertDelete.setVisibility(View.GONE);

        if(this.userId.equals(userId)){
            btnDelete.setVisibility(View.VISIBLE);
            btnReport.setVisibility(View.GONE);
        }else {
            btnDelete.setVisibility(View.GONE);
            btnReport.setVisibility(View.VISIBLE);
        }

        dialogPost.show();
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("dialog","btnReport "+currentPostUserId);
                actionBtns.setVisibility(View.GONE);
                alertReport.setVisibility(View.VISIBLE);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Log.d("dialog","btnReport");
                dialogPost.dismiss();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("dialog","btn delete click");
                actionBtns.setVisibility(View.GONE);
                alertDelete.setVisibility(View.VISIBLE);
            }
        });
        btnReportAlertNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("btnClick", "reportAlertNo");
                dialogPost.dismiss();
            }
        });
        btnReportAlertYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("btnClick", "reportAlertYes");
                dialogPost.dismiss();
                try {
                    postReportApi.postReport(sessionId ,currentPostId, "report");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        btnDeleteAlertNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("btnClick","DeleteAlertNo");
                dialogPost.dismiss();
            }
        });
        btnDeleteAlertYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPost.dismiss();
                try {
                    postReportApi.postReport(sessionId ,currentPostId, "delete");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("btnClick", "DeleteAlertYes");
            }
        });
    }

    @Override
    public void postDelete() {
        Log.d("postDelete","update recyclerview");
        try {
            homeFragment.getPostFeed("refresh");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}