package in.foodtalk.android;

import android.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.foodtalk.android.fragment.DiscoverFragment;
import in.foodtalk.android.fragment.HomeFragment;
import in.foodtalk.android.fragment.MoreFragment;
import in.foodtalk.android.fragment.NewpostFragment;
import in.foodtalk.android.fragment.NotiFragment;
import in.foodtalk.android.module.DatabaseHandler;

public class Home extends AppCompatActivity implements View.OnClickListener{

    DatabaseHandler db;
    LinearLayout btnHome, btnDiscover, btnNewPost, btnNotifications, btnMore;
    ImageView homeIcon, discoverIcon,newpostIcon,notiIcon,moreIcon;
    TextView txtHomeIcon, txtDiscoverIcon, txtNewpostIcon, txtNotiIcon, txtMoreIcon;

    private ImageView[] icons;
    private TextView[] txtIcons;
    private int[] imgR;
    private int[] imgRA;


    HomeFragment homeFragment;
    DiscoverFragment discoverFragment;
    NewpostFragment newpostFragment;
    NotiFragment notiFragment;
    MoreFragment moreFragment;

    int pageNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHandler(getApplicationContext());
        setContentView(R.layout.activity_home);

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
}