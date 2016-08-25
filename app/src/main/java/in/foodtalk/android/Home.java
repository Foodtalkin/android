package in.foodtalk.android;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.applinks.AppLinkData;
import com.facebook.login.LoginManager;
import com.flurry.android.FlurryAgent;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import in.foodtalk.android.apicall.ApiCall;
import in.foodtalk.android.apicall.PostBookmarkApi;
import in.foodtalk.android.apicall.PostLikeApi;
import in.foodtalk.android.apicall.PostReportApi;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.AddRestaurantCallback;
import in.foodtalk.android.communicator.AddedRestaurantCallback;
import in.foodtalk.android.communicator.ApiCallback;
import in.foodtalk.android.communicator.CamBitmapCallback;
import in.foodtalk.android.communicator.CheckInCallback;
import in.foodtalk.android.communicator.CloudinaryCallback;
import in.foodtalk.android.communicator.CommentCallback;
import in.foodtalk.android.communicator.DishTaggingCallback;
import in.foodtalk.android.communicator.HeadSpannableCallback;
import in.foodtalk.android.communicator.MoreBtnCallback;
import in.foodtalk.android.communicator.NotificationCallback;
import in.foodtalk.android.communicator.OpenRestaurantCallback;
import in.foodtalk.android.communicator.PhoneCallback;
import in.foodtalk.android.communicator.PostBookmarkCallback;
import in.foodtalk.android.communicator.PostDeleteCallback;
import in.foodtalk.android.communicator.PostLikeCallback;
import in.foodtalk.android.communicator.PostOptionCallback;
import in.foodtalk.android.communicator.ProfilePostOpenCallback;
import in.foodtalk.android.communicator.ProfileRPostOpenCallback;
import in.foodtalk.android.communicator.RatingCallback;
import in.foodtalk.android.communicator.ReviewCallback;
import in.foodtalk.android.communicator.SearchResultCallback;
import in.foodtalk.android.communicator.StoreCallback;
import in.foodtalk.android.communicator.UserProfileCallback;
import in.foodtalk.android.communicator.UserThumbCallback;
import in.foodtalk.android.fragment.CommentFragment;
import in.foodtalk.android.fragment.CuratedFragment;
import in.foodtalk.android.fragment.DiscoverFragment;
import in.foodtalk.android.fragment.FavouritesFragment;
import in.foodtalk.android.fragment.HomeFragment;
import in.foodtalk.android.fragment.MoreFragment;
import in.foodtalk.android.fragment.SearchFragment;
import in.foodtalk.android.fragment.StoreHistoryFragment;
import in.foodtalk.android.fragment.newpost.AddRestaurant;
import in.foodtalk.android.fragment.newpost.CameraFragment;
import in.foodtalk.android.fragment.newpost.CheckIn;
import in.foodtalk.android.fragment.NotiFragment;
import in.foodtalk.android.fragment.OpenPostFragment;
import in.foodtalk.android.fragment.OpenRPostFragment;
import in.foodtalk.android.fragment.OptionsFragment;
import in.foodtalk.android.fragment.RestaurantProfileFragment;
import in.foodtalk.android.fragment.UserProfile;
import in.foodtalk.android.fragment.WebViewFragment;
import in.foodtalk.android.fragment.newpost.DishTagging;
import in.foodtalk.android.fragment.newpost.RatingFragment;
import in.foodtalk.android.fragment.newpost.ReviewFragment;
import in.foodtalk.android.fragment.StoreFragment;
import in.foodtalk.android.module.CloudinaryUpload;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.NewPostUpload;
import in.foodtalk.android.module.StringCase;
import in.foodtalk.android.object.CreatePostObj;
import in.foodtalk.android.object.RestaurantPostObj;
import in.foodtalk.android.object.UserPostObj;

public class Home extends AppCompatActivity implements View.OnClickListener,
        PostLikeCallback, PostBookmarkCallback, PostOptionCallback, PostDeleteCallback,
        MoreBtnCallback, UserProfileCallback, ProfilePostOpenCallback, FragmentManager.OnBackStackChangedListener,
        HeadSpannableCallback, UserThumbCallback, ProfileRPostOpenCallback, PhoneCallback,
        CheckInCallback, CamBitmapCallback, DishTaggingCallback , RatingCallback , ReviewCallback, AddRestaurantCallback,
        AddedRestaurantCallback, SearchResultCallback, CommentCallback, NotificationCallback, OpenRestaurantCallback,
        ApiCallback, StoreCallback {

    DatabaseHandler db;
    LinearLayout btnHome, btnDiscover, btnNewPost, btnNotifications, btnMore;
    ImageView homeIcon, discoverIcon, newpostIcon, notiIcon, moreIcon;
    TextView txtHomeIcon, txtDiscoverIcon, txtNewpostIcon, txtNotiIcon, txtMoreIcon;

    private ImageView[] icons;
    private TextView[] txtIcons;
    private int[] imgR;
    private int[] imgRA;
    private LinearLayout btnLogout;
    LinearLayout progressBarUpload;

    HomeFragment homeFragment;
    DiscoverFragment discoverFragment;
    DiscoverFragment dishResultFragment;
    CheckIn newpostFragment;
    NotiFragment notiFragment;
    MoreFragment moreFragment;
    OpenPostFragment openPostFragment;
    OptionsFragment optionsFragment;
    WebViewFragment webViewFragment;
    FavouritesFragment favouritesFragment;
    RestaurantProfileFragment restaurantProfileFragment;
    OpenRPostFragment openRPostFragment;
    CameraFragment cameraFragment;
    DishTagging dishTagging;
    RatingFragment ratingFragment;
    ReviewFragment reviewFragment;
    AddRestaurant addRestaurant;
    SearchFragment searchFragment;
    CommentFragment commentFragment;
    CuratedFragment curatedFragment;
    StoreFragment storeFragment;
    StoreHistoryFragment storeHistoryFragment;



    //-------dummy fragment created for temporary use to set Legal screen title----
    Fragment legalFragment = new Fragment();

    UserProfile userProfile;

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

    String currentProfileUserId = "null";
    String currentProfileRestaurantId = "null";

    TextView titleHome;
    TextView subTitleHome;
    TextView titleHome1;

    RelativeLayout header;
    RelativeLayout header1;
    int pageNo;

    private final int USER_PROFILE = 1;
    private final int DISH = 2;
    private final int RESTAURANT_PROFILE = 3;

    private final int DISCOVER_SCREEN = 0;
    private final int DISH_PROFILE = 1;

    private String dishSearchedName;
    private StringCase stringCase;

    Bitmap photo;
    File file;
    String restaurantNameNewPost;
    String rating;
    String review;
    String restaurantIdNewPost;
    String dishName;

    CreatePostObj createPostObj;
    TextView txtUploadingDish;

    TextView searchBarHome;

    LinearLayout searchHeader;
    Fragment currentFragment;

    ImageView btnOption;
    ImageView btnStoreHistory;

    ApiCall apiCall;

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1, REQUEST_CROP = 2;
    //private File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
    private File destination = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), System.currentTimeMillis() + ".jpg");


   // ImageCapture imageCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHandler(getApplicationContext());

        apiCall = new ApiCall();

       // imageCapture = new ImageCapture(this);

       // requestWindowFeature(Window.FEATURE_NO_TITLE);

        //-------api init--------------------------
        postLikeApi = new PostLikeApi(this);
        postBookmarkApi = new PostBookmarkApi(this);
        postReportApi = new PostReportApi(this);
        //-----------------------------------------

        stringCase = new StringCase();



        //getWindow().requestFeature(Window.FEATURE_NO_TITLE);


        setContentView(R.layout.activity_home);

        createPostObj = new CreatePostObj();


       // ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //progressBar.setIndeterminate(false);

        progressBarUpload = (LinearLayout) findViewById(R.id.progress_bar_upload);
        txtUploadingDish = (TextView) findViewById(R.id.txt_uploading_dish);

        header = (RelativeLayout) findViewById(R.id.header);
        header1 = (RelativeLayout) findViewById(R.id.header1);


        subTitleHome = (TextView) findViewById(R.id.subtitle);
        titleHome1 = (TextView) findViewById(R.id.title_home1);

        btnLogout = (LinearLayout) findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(this);

        btnOption = (ImageView) findViewById(R.id.btn_option);
        btnStoreHistory = (ImageView) findViewById(R.id.btn_store_history);

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

        titleHome = (TextView) findViewById(R.id.title_home);

        searchBarHome = (TextView) findViewById(R.id.txt_search_home);

        searchBarHome.setOnClickListener(this);

        searchHeader = (LinearLayout) findViewById(R.id.search_header);

        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setDisplayUseLogoEnabled(false);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        //-----------
        icons = new ImageView[]{homeIcon, discoverIcon, newpostIcon, notiIcon, moreIcon};
        txtIcons = new TextView[]{txtHomeIcon, txtDiscoverIcon, txtNewpostIcon, txtNotiIcon, txtMoreIcon};
        imgR = new int[]{R.drawable.home, R.drawable.discover, R.drawable.newpost, R.drawable.notifications, R.drawable.more};
        imgRA = new int[]{R.drawable.home_active, R.drawable.discover_active, R.drawable.newpost_active, R.drawable.notifications_active, R.drawable.more_active};
        //----

        btnDiscover.setOnClickListener(this);
        btnNewPost.setOnClickListener(this);
        btnNotifications.setOnClickListener(this);
        btnMore.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        btnOption.setOnClickListener(this);
        btnStoreHistory.setOnClickListener(this);
        // Log.d("getInfo",db.getRowCount()+"");
        // Log.d("get user info", db.getUserDetails().get("userName")+"");

        // Log.d("get user info", "session id: "+db.getUserDetails().get("sessionId"));
        // Log.d("get user info", "user id: "+db.getUserDetails().get("userId"));
        //Log.d("get user info", "full name: "+db.getUserDetails().get("fullName"));
        //Log.d("get user info", "user name: "+db.getUserDetails().get("userName"));

        userId = db.getUserDetails().get("userId");
        sessionId = db.getUserDetails().get("sessionId");

        homeFragment = new HomeFragment();
        discoverFragment = new DiscoverFragment();

        notiFragment = new NotiFragment();
        moreFragment = new MoreFragment();

        optionsFragment = new OptionsFragment();
        favouritesFragment = new FavouritesFragment();
        curatedFragment = new CuratedFragment();
        storeFragment = new StoreFragment();
        storeFragment.title = titleHome;
        storeHistoryFragment = new StoreHistoryFragment();

        openHomeFirst();
        deepLinkfb();

        //----------get extra------
        String newString;
        if (savedInstanceState == null) {
            Log.e("SavedInstance", "Null");
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
                Log.e("Get extras", "Null");

                // Add the fragment to the 'fragment_container' FrameLayout
            } else {
                //newString= extras.getString("STRING_I_NEED");
                Log.e("Get extras", "is not Null");
                Log.d("Get extras", extras.getString("com.parse.Data")+"");
                String jsonData = extras.getString("com.parse.Data");
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    final String screenName = jsonObject.getString("class");
                    final String elementId = jsonObject.getString("elementId");
                    //openNotificationFragment(screenName, elementId);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //openNotificationFragment();
            }
        } else {
            Log.e("SavedInstance", "is not Null");
            newString= (String) savedInstanceState.getSerializable("STRING_I_NEED");
            Log.d("Get extras", "null--");
        }
        //----------------------------


    }

    private void openHomeFirst(){
        getFragmentManager().beginTransaction()
                .add(R.id.container, homeFragment).commit();
        pageNo = 0;

        getFragmentManager().addOnBackStackChangedListener(this);
    }

    private void deepLinkfb(){
        FacebookSdk.sdkInitialize(this);
        AppLinkData.fetchDeferredAppLinkData(this,
                new AppLinkData.CompletionHandler() {
                    @Override
                    public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                        if (appLinkData != null) {
                            Bundle bundle = appLinkData.getArgumentBundle();
                            Log.i("DEBUG_FACEBOOK_SDK", bundle.getString("target_url"));
                            String url = bundle.getString("target_url");
                            List<String> items = Arrays.asList(url.split("\\s*/\\s*"));

                            if (items.size() > 3){
                                Log.e("DebugFb urlvalue", items.get(2));
                                Log.e("DebugFb urlvalue", items.get(3));
                                //openNotificationFragment(items.get(2), items.get(3));
                            }else if (items.size() > 2){
                                //openNotificationFragment(items.get(2), "");
                                Log.e("DebugFb urlvalue", items.get(2));
                            }

                        } else {
                            Log.i("DEBUG_FACEBOOK_SDK", "AppLinkData is Null");
                        }
                    }
                });
    }
    private void openNotificationFragment(String fragmentName, String elementId){
        Log.d("Notification screen", fragmentName);
        switch (fragmentName){
            case "OpenPost":
                Bundle bundle = new Bundle();
                bundle.putString("postId", elementId);
                commentFragment = new CommentFragment();
                commentFragment.setArguments(bundle);
                setFragmentView(commentFragment, R.id.container1, 0, true);
                break;
            case "UserProfile":
                /*userProfile = new UserProfile();
                userProfile.userProfile1(elementId);
                setFragmentView(userProfile, R.id.container, 0, true);*/
                userProfileOpen(elementId);
                break;
            case "RestaurantProfile":
                /*Bundle bundle1 = new Bundle();
                bundle1.putString("restaurantId", elementId);
                restaurantProfileFragment = new RestaurantProfileFragment();
                restaurantProfileFragment.setArguments(bundle1);
                setFragmentView(restaurantProfileFragment, R.id.container, 0, true);*/
                openRProfile(elementId);
                break;
            case "Home":
                setFragmentView(homeFragment, R.id.container, 0, false);
                break;
            case "Discover":
                discoverFragment.pageType = DISCOVER_SCREEN;
                setFragmentView(discoverFragment, R.id.container, 1, false);
                break;
            case "Search":
                searchFragment = new SearchFragment();
                setFragmentView(searchFragment, R.id.container1, -1, true);
                break;
            case "CheckIn":
                startCheckIn(null);
                /*newpostFragment = new CheckIn();
                setFragmentView(newpostFragment, R.id.container1, 2, true);*/
                break;
            case "Notifications":
                setFragmentView(notiFragment, R.id.container, 3, false);
                break;
            case "More":
                setFragmentView(moreFragment, R.id.container, 4, false);
                break;
            case "AddRestaurant":
                addRestaurant = new AddRestaurant();
                setFragmentView(addRestaurant, R.id.container1, 0, true);
                break;
            case "Options":
                setFragmentView(optionsFragment, R.id.container, 0, true);
                break;
            case "Favorite":
                setFragmentView(favouritesFragment, R.id.container, 0, true);
                break;
            case "DishProfile":
                dishResultFragment = new DiscoverFragment();
                dishResultFragment.pageType = DISH_PROFILE;
                dishResultFragment.dishName = dishName;
                dishSearchedName = dishName;
                setFragmentView(dishResultFragment, R.id.container, -1, true);
                break;
            case "WebLink":
                openWebPage(elementId, "Web");
               /* webViewFragment = new WebViewFragment();
                webViewFragment.webViewFragment1(elementId);
                setFragmentView (webViewFragment, R.id.container, 0, true);
                //titleHome.setText("Legal");
                titleHome.setText("Web");*/
                //setTitle(legalFragment);
                break;
            case "FoodTalkSuggestions":
                setFragmentView(curatedFragment, R.id.container, -1, true);
                break;
            case "Store":
                setFragmentView(storeFragment, R.id.container, -1, true);
                break;
            case "BookStore":
                setFragmentView(storeHistoryFragment, R.id.container, -1, true);
                break;
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_home:
                //Log.d("onClick", "btn home");
                currentFragment = this.getFragmentManager().findFragmentById(R.id.container);
                if(currentFragment != homeFragment){
                    setFragmentView(homeFragment, R.id.container, 0, false);
                    // titleHome.setText("Home");
                    pageNo = 0;

                   // getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }else {
                    homeFragment.scrollToTop();
                }
               /* if (pageNo != 0) {
                    setFragmentView(homeFragment, R.id.container, 0, false);
                   // titleHome.setText("Home");
                    pageNo = 0;
                }*/
                break;
            case R.id.btn_discover:
                //Log.d("onClick", "btn discover");
                currentFragment = this.getFragmentManager().findFragmentById(R.id.container);
                if(currentFragment != discoverFragment){
                    discoverFragment.pageType = DISCOVER_SCREEN;
                    setFragmentView(discoverFragment, R.id.container, 1, false);
                    //titleHome.setText("Discover");
                    pageNo = 1;
                }
              /* if (pageNo != 1) {
                    discoverFragment.pageType = DISCOVER_SCREEN;
                    setFragmentView(discoverFragment, R.id.container, 1, false);
                    //titleHome.setText("Discover");
                    pageNo = 1;
                }*/
                break;
            case R.id.btn_newpost:
                if (pageNo != 2) {
                    photo = null;
                    startCheckIn(null);
                    /*newpostFragment = new CheckIn();
                    setFragmentView(newpostFragment, R.id.container1, 2, true);*/
                    //titleHome.setText("New Post");
                    //pageNo = 2;
                }
                //Log.d("onClick", "btn newpost");
                break;
            case R.id.btn_notification:
                currentFragment = this.getFragmentManager().findFragmentById(R.id.container);
                if(currentFragment != notiFragment){
                    setFragmentView(notiFragment, R.id.container, 3, false);
                    // titleHome.setText("Notification");
                    pageNo = 3;
                }
               /*if (pageNo != 3) {
                    setFragmentView(notiFragment, R.id.container, 3, false);
                   // titleHome.setText("Notification");
                    pageNo = 3;
                }*/

                //Log.d("onClick", "btn notification");
                break;
            case R.id.btn_more:
                currentFragment = this.getFragmentManager().findFragmentById(R.id.container);
                if(currentFragment != moreFragment){
                    setFragmentView(moreFragment, R.id.container, 4, false);
                    //titleHome.setText("More");
                    pageNo = 4;
                }
                /*if (pageNo != 4) {
                    setFragmentView(moreFragment, R.id.container, 4, false);
                    //titleHome.setText("More");
                    pageNo = 4;
                }*/
                //Log.d("onClick", "btn more");
                break;
            case R.id.btn_logout:
                //Log.d("btn clicked", "logout");
                logOut();
                break;
            case R.id.txt_search_home:
                Log.d("clicked","on search box");
                searchFragment = new SearchFragment();
                setFragmentView(searchFragment, R.id.container1, -1, true);
                break;
            case R.id.btn_option:
                Log.d("clicked","btnOption");
                currentFragment = this.getFragmentManager().findFragmentById(R.id.container);
                if (currentFragment == userProfile){
                    profileReport("user", currentProfileUserId);
                }else if (currentFragment == restaurantProfileFragment){
                    profileReport("restaurant", currentProfileRestaurantId);
                }

                break;
            case R.id.btn_store_history:
                setFragmentView(storeHistoryFragment, R.id.container, -1, true);
                break;
        }
    }
    private void setFragmentView(Fragment newFragment, int container, int pageN, boolean bStack) {
        String backStateName = newFragment.getClass().getName();

        FlurryAgent.logEvent(newFragment.getClass().getSimpleName());

        if (newFragment == userProfile){
            subTitleHome.setText("");
            titleHome1.setText("");
        }


        // Log.d("newFragment", backStateName);
        Log.i("setFragmentView",newFragment.getClass().getSimpleName());

        if(container == R.id.container){
            setTitle(newFragment);
        }


            //Log.d("New fragment", backStateName+"");
            if (newFragment != newpostFragment && pageN != -1){
                icons[pageNo].setImageResource(imgR[pageNo]);
                icons[pageN].setImageResource(imgRA[pageN]);
                txtIcons[pageN].setTextColor(getResources().getColor(R.color.icon_txt_active));
                txtIcons[pageNo].setTextColor(getResources().getColor(R.color.icon_txt));
            }
            else {
                //Log.d("fragment","newpostFragment");
            }

             FragmentManager manager = getFragmentManager();
            //boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);


            android.app.FragmentTransaction transaction = manager.beginTransaction();
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack if needed
            if(newFragment == newpostFragment){
                transaction.remove(newpostFragment);
            }



            transaction.replace(container, newFragment);
            if (bStack) {
                transaction.addToBackStack(backStateName);
                //Log.d("addtobackstack", backStateName);
            }
            // Commit the transaction
            Log.d("setFragmentview","going to commit");
            if(newFragment == homeFragment || newFragment == discoverFragment || newFragment == notiFragment || newFragment == moreFragment){
                clearBackStack();

            }
            transaction.commit();

        /*if(this.getFragmentManager().findFragmentById(R.id.container1) == null){
            setTitle(newFragment);
        }*/
    }
    Boolean backPressed = false;

    @Override
    public void onBackPressed() {
       // Fragment f = this.getFragmentManager().findFragmentById(R.id.container);
       // String fName = f.getClass().getSimpleName();
        backPressed = true;


        if (getFragmentManager().getBackStackEntryCount() > 0) {

            //getFragmentManager().getBackStackEntryAt(getFragmentManager().getBackStackEntryCount()).getName();
           // Log.d("back stack fn", getFragmentManager().getBackStackEntryAt(getFragmentManager().getBackStackEntryCount()-1).getName());
            Log.i("onBackPressed","going to popBackStack");
            getFragmentManager().popBackStack();
            Log.i("onBackPressed","going to popBackStack done");
           // Fragment f = this.getFragmentManager().findFragmentById(R.id.container);
            //setTitle(f);
           // String fName = f.getClass().getSimpleName();
        } else {
            super.onBackPressed();
        }
        Fragment f = this.getFragmentManager().findFragmentById(R.id.container);
        setTitle(f);
    }
    @Override
    public void onBackStackChanged() {

       // String fName = f.getClass().getSimpleName();

             //Log.i("onBackStackChanged", f.getClass().getSimpleName());
            //setTitle(f);
        if(backPressed){
            Fragment f = this.getFragmentManager().findFragmentById(R.id.container);

            setTitle(f);


            backPressed = false;
        }
    }


    private void logOut() {
        db.resetTables();
        LoginManager.getInstance().logOut();
        Intent i = new Intent(this, FbLogin.class);
        startActivity(i);
        finish();
    }

    @Override
    public void like(int position, String postId, Boolean like) {
        Log.d("likeResponse", position + " postid: " + postId);
        FlurryAgent.logEvent("Like Tabbed");
        try {
            postLikeApi.postLike(postId, like);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // discoverFragment.recyclerView.smoothScrollToPosition(5);
    }

    @Override
    public void bookmark(int position, String postId, Boolean bookmark) {
        //Log.d("bookmark", "position"+ position);
        FlurryAgent.logEvent("Bookmark Tabbed");
        try {
            postBookmarkApi.postBookmark(postId, bookmark);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void option(int position, String postId, String userId) {
        Log.d("option callback", "post id: " + postId);
        showDialog(postId, userId);
    }

    //String phone1;
    // String phone2;
    private void callDialog(final String phone1, final String phone2) {
        final Dialog dialogCall = new Dialog(this);
        dialogCall.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCall.setContentView(R.layout.dialog_call);

        //this.phone1 = phone1;
        // this.phone2 = phone2;

        final TextView txtPhone1 = (TextView) dialogCall.findViewById(R.id.txt_phone1);
        TextView txtPhone2 = (TextView) dialogCall.findViewById(R.id.txt_phone2);

        TextView btnClose = (TextView) dialogCall.findViewById(R.id.btn_call_dialog_close);


        if (!phone1.equals("")) {
            txtPhone1.setText(phone1);
        } else {
            txtPhone1.setVisibility(View.GONE);
        }
        if (!phone2.equals("")) {
            txtPhone2.setText(phone2);
        } else {
            txtPhone2.setVisibility(View.GONE);
        }

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCall.dismiss();
            }
        });

        txtPhone1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("call", phone1);
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+phone1));
                startActivity(callIntent);
            }
        });
        txtPhone2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("call", phone2);
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+phone2));
                startActivity(callIntent);
            }
        });
        dialogCall.show();
    }
    File file1 = new File(Environment.getExternalStorageDirectory()+File.separator + "img.jpg");

    String mCurrentPhotoPath;

    CamBitmapCallback camBitmapCallback;

    CropImageView cropedImg;
    final static int FROM_GALLERY = 55;

    private void dialogImgFrom(){
        Log.d("home","dialogImgFrom comes");

        final Dialog dialogImgFrom = new Dialog(this);
        dialogImgFrom.requestWindowFeature(Window.FEATURE_NO_TITLE);

       // dialogImgFrom.setCancelable(false);
       // dialogImgFrom.setCanceledOnTouchOutside(false);

        dialogImgFrom.setContentView(R.layout.dialog_img_from);

        TextView btnCamera = (TextView) dialogImgFrom.findViewById(R.id.btn_camera_imgfrom);
        TextView btnGallery = (TextView) dialogImgFrom.findViewById(R.id.btn_gallery_imgfrom);

        dialogImgFrom.show();
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogImgFrom.dismiss();
                cameraIntent();
                /*Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file1));
                startActivityForResult(intent, 1);*/
            }
        });
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogImgFrom.dismiss();
                Log.d("dialogImgFrom", "bt Gallery");
                galleryIntent();
                /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                //startActivityForResult(intent, FROM_GALLERY);
                startActivityForResult(Intent.createChooser(intent, "Select File"), FROM_GALLERY);*/
            }
        });
    }
    private void showDialog(String postId, final String userId){

        currentPostUserId = userId;
        currentPostId = postId;
        dialogPost = new Dialog(this);
        dialogPost.requestWindowFeature(Window.FEATURE_NO_TITLE);
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

    private void profileReport(final String profileType, final String id){

        final Dialog dialogReport;
        dialogReport = new Dialog(this);
        dialogReport.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogReport.setContentView(R.layout.dialog_report);
        TextView btnCancel = (TextView) dialogReport.findViewById(R.id.btn_cancel);
        TextView btnYes = (TextView) dialogReport.findViewById(R.id.btn_yes);
        TextView txtAlert = (TextView) dialogReport.findViewById(R.id.txt_report_alert);
        LinearLayout alertRestaurant = (LinearLayout) dialogReport.findViewById(R.id.alert_restaurant_report);
        LinearLayout alertUser = (LinearLayout) dialogReport.findViewById(R.id.alert_user_report);

        TextView btnNumber = (TextView) dialogReport.findViewById(R.id.btn_number);
        TextView btnAddress = (TextView) dialogReport.findViewById(R.id.btn_address);
        TextView btnShutdown = (TextView) dialogReport.findViewById(R.id.btn_shutdown);
        TextView btnCancel1 = (TextView) dialogReport.findViewById(R.id.btn_cancel1);

        if (profileType.equals("user")){
            txtAlert.setText("Report user ?");
            alertRestaurant.setVisibility(View.GONE);
            alertUser.setVisibility(View.VISIBLE);
        }else if (profileType.equals("restaurant")){
            alertRestaurant.setVisibility(View.VISIBLE);
            alertUser.setVisibility(View.GONE);
            //txtAlert.setText("Report restaurant");
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogReport.dismiss();
            }
        });
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profileType.equals("user")){
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("sessionId", sessionId);
                        jsonObject.put("userId", currentProfileUserId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Log.d("apicall",getApplicationContext()+" "+ jsonObject+" "+Config.URL_REPORT_USER+ " userReport");
                    apiCall.apiRequestPost(getApplicationContext(), jsonObject, Config.URL_REPORT_USER, "userReport", null);
                }
                dialogReport.dismiss();
            }
        });
        btnNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("sessionId", sessionId);
                    jsonObject.put("restaurantId", id);
                    jsonObject.put("reportType","1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                apiCall.apiRequestPost(getApplicationContext(), jsonObject, Config.URL_REPORT_RESTAURANT, "restaurantReport", null);
                dialogReport.dismiss();
            }
        });
        btnAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("sessionId", sessionId);
                    jsonObject.put("restaurantId", id);
                    jsonObject.put("reportType","2");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                apiCall.apiRequestPost(getApplicationContext(), jsonObject, Config.URL_REPORT_RESTAURANT, "restaurantReport", null);
                dialogReport.dismiss();
            }
        });
        btnShutdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("sessionId", sessionId);
                    jsonObject.put("restaurantId", id);
                    jsonObject.put("reportType","3");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                apiCall.apiRequestPost(getApplicationContext(), jsonObject, Config.URL_REPORT_RESTAURANT, "restaurantReport", null);
                dialogReport.dismiss();
            }
        });
        btnCancel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              dialogReport.dismiss();
            }
        });
        dialogReport.show();
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
    @Override
    public void btnClick(String type, int position) {
        Log.d("more btn clicked", type+" position: "+ position);
        /*if(type.equals("profile")){
            setFragmentView(userProfile, R.id.container, 4, true);
        }
        if(type.equals("options")){
            setFragmentView(optionsFragment, R.id.container, 4, true);
        }*/
       switch (type){
           case "profile":
               /*userProfile = new UserProfile();
               userProfile.userProfile1(userId);
               setFragmentView(userProfile, R.id.container, -1, true);*/
               userProfileOpen(userId);
               break;
           case "options":
               setFragmentView(optionsFragment, R.id.container, -1, true);
               break;
           case "legal":
               Log.d("btn click","setFragment webview");
               openWebPage("http://www.foodtalkindia.com/document.html", null);
               /*webViewFragment = new WebViewFragment();
               webViewFragment.webViewFragment1("http://www.foodtalkindia.com/document.html");
               setFragmentView (webViewFragment, R.id.container, -1, true);
               //titleHome.setText("Legal");*/
               setTitle(legalFragment);
               break;
           case "favourites":
               setFragmentView(favouritesFragment, R.id.container, -1, true);
               break;
           case "curated":
               setFragmentView(curatedFragment, R.id.container, -1, true);
               break;
           case "store":
               setFragmentView(storeFragment, R.id.container, -1, true);
               break;
       }
    }
    @Override
    public void getUserInfo(String points, String userName) {

        Double v2 = Double.parseDouble(points);
        int v3 = (int) Math.floor(v2);
        //Double pointValue = Double.parseDouble(points);
        subTitleHome.setText(Integer.toString(v3)+" Points");
        titleHome.setText(userName);
        //Log.d("points", points);
    }
    @Override
    public void postOpen(List<UserPostObj> postObj, String postId, String userId) {
        openPostFragment = new OpenPostFragment();
        openPostFragment.openPostFragment1(postObj, postId, userId);
        setFragmentView (openPostFragment, R.id.container, -1, true);
        Log.d("postOpen","userId: "+userId+" postId: "+postId);
    }

    @Override
    public void spannableTxt(String userId, String checkinRestaurantId, String dishName, int viewType, String requestFrom) {
        switch (viewType){
            case USER_PROFILE:
                if(!requestFrom.equals("UserProfile")){
                    /*userProfile = new UserProfile();
                    userProfile.userProfile1(userId);
                    setFragmentView(userProfile, R.id.container, -1, true);*/
                    userProfileOpen(userId);
                    if(requestFrom.equals("commentFragment")){
                        getFragmentManager().beginTransaction().remove(commentFragment).commit();
                    }
                }

                break;
            case RESTAURANT_PROFILE:

                if (requestFrom.equals("UserProfile")){
                   // getFragmentManager().beginTransaction().remove(openPostFragment).commit();
                }
                if (requestFrom.equals("commentFragment")){
                    getFragmentManager().beginTransaction().remove(commentFragment).commit();
                }
                openRProfile(checkinRestaurantId);
                Log.d("clicked","for restaurant");
                break;
            case DISH:
                if (requestFrom.equals("commentFragment")){
                    getFragmentManager().beginTransaction().remove(commentFragment).commit();
                }
                Log.d("clicked","for Dish"+ dishName);
                dishSearchByName(dishName, false);
                break;
        }
    }
    private void openRProfile(String restaurantId){
        currentProfileRestaurantId = restaurantId;
        Bundle bundle1 = new Bundle();
        bundle1.putString("restaurantId", restaurantId);
        restaurantProfileFragment = new RestaurantProfileFragment();
        restaurantProfileFragment.setArguments(bundle1);
        setFragmentView(restaurantProfileFragment, R.id.container, -1, true);
    }

    @Override
    public void thumbClick(String userId) {
        /*userProfile = new UserProfile();
        userProfile.userProfile1(userId);
        setFragmentView(userProfile, R.id.container, -1, true);*/
        userProfileOpen(userId);
    }

    private void userProfileOpen(String userId){
        currentProfileUserId = userId;
        userProfile = new UserProfile();
        userProfile.userProfile1(userId);
        setFragmentView(userProfile, R.id.container, -1, true);
    }

    @Override
    public void rPostOpen(List<RestaurantPostObj> postObj, String postId, String restaurantId) {
        openRPostFragment = new OpenRPostFragment();
        openRPostFragment.openRPostFragment1(postObj, postId, restaurantId);
        setFragmentView (openRPostFragment, R.id.container, -1, true);
        Log.d("postOpen","userId: "+userId+" postId: "+postId);
    }
    @Override
    public void phoneBtn(String phone1, String phone2) {
        Log.d("phone numbers", phone1+" : "+phone2);
        callDialog(phone1, phone2);
    }
    @Override
    public void checkInRestaurant(String restaurantId, String restaurantName) {
        currentFragment = this.getFragmentManager().findFragmentById(R.id.container1);
        //if (currentFragment == newpostFragment){
            pickImage(restaurantId, restaurantName);
        //}

    }
    private void pickImage(String restaurantId, String restaurantName){
        Log.d("home", "photo "+ photo);
        this.restaurantIdNewPost = restaurantId;
        Log.d("checkInRestarant","rId"+restaurantId+" rName: "+restaurantName);
        cameraFragment = new CameraFragment();
        restaurantNameNewPost = restaurantName;

        if (photo == null){
            dialogImgFrom();
        }else {
            startDishTagging();
        }
        //setFragmentView (cameraFragment, R.id.container1, 4, true);
        hideSoftKeyboard();
    }

    @Override
    public void capturedBitmap(Bitmap photo , File file) {

        this.photo = photo;

        this.file = file;

        Log.d("capuredBitmap", "call");
        dishTagging = new DishTagging();
        dishTagging.dishTagging1(photo);
        setFragmentView(dishTagging, R.id.container1, 0, true);
        //showSoftKeyboard(layout);
    }
    public void capturedBitmap1(Bitmap photo , File file) {

        this.photo = photo;

        this.file = file;

        Log.d("capuredBitmap", "call");
        //showSoftKeyboard(layout);
    }
    private void startDishTagging(){
        dishTagging = new DishTagging();
        dishTagging.dishTagging1(photo);
        setFragmentView(dishTagging, R.id.container1, 0, true);
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void showSoftKeyboard(EditText txt) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        txt.requestFocus();
        inputMethodManager.showSoftInput(txt, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideSoftKeyboard();
        Log.d("onDestroy", "distroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftKeyboard();
        Log.d("onPause", "activity pause");
    }

    @Override
    public void dishNameSelected(String dishName) {

    }
    @Override
    public void startRating(String dishName) {
        Log.d("startRating", dishName);
        this.dishName = dishName;
        hideSoftKeyboard();
        ratingFragment = new RatingFragment();
        ratingFragment.ratingFragment1(photo);
        setFragmentView(ratingFragment, R.id.container1, 0, true);
    }
    @Override
    public void goforReview(String rate) {
        Log.d("goforReview", rate);
        rating = rate;
        reviewFragment = new ReviewFragment();
        reviewFragment.reviewFragment1(photo);
        setFragmentView(reviewFragment, R.id.container1, 0, true);
    }
    CloudinaryCallback cloudinaryCallback = new CloudinaryCallback() {
        @Override
        public void uploaded(Map result) {
            Log.d("uploaded", result+"");
            //progressBarUpload.setVisibility(View.GONE);
            Log.d("result img url", result.get("url").toString());

            createPostObj.sessionId= sessionId;
            createPostObj.checkedInRestaurantId = restaurantIdNewPost;
            createPostObj.image = result.get("url").toString();
            createPostObj.tip = review;
            createPostObj.rating = rating;
            createPostObj.dishName = dishName;
            createPostObj.sendPushNotification = "1";
            createPostObj.shareOnFacebook = "1";
            createPostObj.shareOnTwitter = "1";
            createPostObj.shareOnInstagram = "1";
            NewPostUpload newPostUpload = new NewPostUpload(createPostObj , homeFragment.newPostCallback, progressBarUpload);
            try {
                newPostUpload.uploadNewPost();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    @Override
    public void postData(String review) {
        Log.d("review call back", review);
        this.review = review;
        getFragmentManager().beginTransaction().remove(reviewFragment).commit();
        hideSoftKeyboard();
        //new GetApiContent(getContext(), apiAsyncCallback).execute("http://www.circuitmagic.com/api/get_posts/");
        new CloudinaryUpload(this, cloudinaryCallback).execute(file);
       //progressBarUpload.setIndeterminate(true);
        progressBarUpload.setVisibility(View.VISIBLE);
        if (restaurantNameNewPost.equals("")){
            txtUploadingDish.setText("Posting "+dishName);
        }else {
            txtUploadingDish.setText("Posting "+dishName+" at "+restaurantNameNewPost);
        }
        clearBackStack();
    }

    @Override
    public void addNewRestaurant() {
        addRestaurant = new AddRestaurant();
        setFragmentView(addRestaurant, R.id.container1, -1, true);
    }

    @Override
    public void restaurantAdded(String rId) {
        Log.d("restaurant added", "Rid: "+rId);
        startCheckIn(rId);
    }

    private void startCheckIn(String rId){
        newpostFragment = new CheckIn();
        if (rId != null){
            Bundle bundle = new Bundle();
            bundle.putString("rId", rId);
            newpostFragment.setArguments(bundle);
        }
        setFragmentView(newpostFragment, R.id.container1, 2, true);
        pickImage("","");
        //dialogImgFrom();
    }

    //----------new camera and gallery and crop code----------------
    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
        startActivityForResult(intent, REQUEST_CAMERA);
    }
    private void cropIntent(Uri imageUri) {
        Log.d("startCropImage", imageUri+"");
        Intent intent = CropImage.activity(imageUri).setFixAspectRatio(true).getIntent(this);
        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK){
            if (requestCode == SELECT_FILE){
                Log.d("requeestCode", "SELECT_FILE");

                cropIntent(data.getData());
                /*try {
                    Bitmap  mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    imgV.setImageBitmap(mBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
            if (requestCode == REQUEST_CAMERA){
                Log.d("requeestCode", "REQUEST_CAMERA");
                cropIntent(Uri.fromFile(destination));
                //onCaptureImageResult(data);
            }
            if (requestCode == REQUEST_CROP){
                Log.d("requeestCode", "REQUEST_CROP");
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                if (data != null){
                    // Uri selectedImage = data.getData();
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);

                    Bitmap photo = decodeFile(new File(result.getUri().getPath()));

                    //-- imVCature_pic.setImageBitmap(photo);

                    //-- camBitmapCallback.capturedBitmap(photo , new File(result.getUri().getPath()));
                    capturedBitmap1(photo , new File(result.getUri().getPath()));
                    /*try {

                        Bitmap  mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getUri());
                        imgV.setImageURI(result.getUri());
                        //imgV.setImageBitmap(mBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                }else {
                    Log.e("onActivityResult", "data null");
                }

            }
        }
    }
    //-----------------end camera gallery and crop

    //---------camera and crop intent----------------------------


    private void startCropImageActivity(Uri imageUri) {
        // CropImage.activity(imageUri)
        //  .setGuidelines(CropImageView.Guidelines.ON)
        //  .setFixAspectRatio(true)
        //     .start(getActivity());
        //

        Log.d("startCropImage", imageUri+"");
        Intent intent = CropImage.activity(imageUri).setFixAspectRatio(true).getIntent(this);
        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);

    }

    private Bitmap decodeFile(File f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 512;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    private void setTitle(Fragment fragment){
        /*if (fragment == homeFragment){
            header.setVisibility(View.VISIBLE);
            header1.setVisibility(View.GONE);
            titleHome.setText("Home");
        }*/

        Log.e("setTitle",fragment.getClass().getName());
        if (fragment == discoverFragment){
            header.setVisibility(View.VISIBLE);
            header1.setVisibility(View.GONE);
            titleHome.setText("Nearby");
        }
        if (fragment == notiFragment){
            header.setVisibility(View.VISIBLE);
            header1.setVisibility(View.GONE);
            titleHome.setText("Notification");
        }
        if (fragment == moreFragment){
            header.setVisibility(View.VISIBLE);
            header1.setVisibility(View.GONE);
            titleHome.setText("More");
        }

        if(fragment == favouritesFragment){
            header.setVisibility(View.VISIBLE);
            header1.setVisibility(View.GONE);
            titleHome.setText("My Bucket List");
        }
        if(fragment == optionsFragment){
            header.setVisibility(View.VISIBLE);
            header1.setVisibility(View.GONE);
            titleHome.setText("Options");
        }
        if(fragment == legalFragment){
            header.setVisibility(View.VISIBLE);
            header1.setVisibility(View.GONE);
            titleHome.setText("Legal");
        }

        if (fragment == userProfile){
            header.setVisibility(View.VISIBLE);
            header1.setVisibility(View.GONE);
            searchHeader.setVisibility(View.GONE);
        }
        if (fragment == restaurantProfileFragment){
            header.setVisibility(View.VISIBLE);
            header1.setVisibility(View.GONE);
            searchHeader.setVisibility(View.GONE);
            titleHome.setText("Restaurant");
        }

        if(fragment == homeFragment) {
            searchHeader.setVisibility(View.VISIBLE);
            header.setVisibility(View.GONE);
            header1.setVisibility(View.GONE);
        }else if(fragment != searchFragment && fragment != userProfile) {
            header.setVisibility(View.VISIBLE);
            searchHeader.setVisibility(View.GONE);
        }

        if(fragment == dishResultFragment){
            searchHeader.setVisibility(View.GONE);
            header.setVisibility(View.VISIBLE);
            header1.setVisibility(View.GONE);
            titleHome.setText(stringCase.caseSensitive(dishSearchedName));
        }
        if (fragment == curatedFragment){
            titleHome.setText("Food Talk Curated list");
        }

        /*if(this.getFragmentManager().findFragmentById(R.id.container1) != null){
            header.setVisibility(View.GONE);
            Log.d("setTitle", "header gone");
        }*/
       /* case "UserProfile":
        header.setVisibility(View.VISIBLE);
        header1.setVisibility(View.GONE);
        break;
        case "MoreFragment":
        //titleHome.setText("More");
        break;
        case "FavouritesFragment":*/
        //titleHome.setText("Favourites");
        if(fragment == openPostFragment){
            header.setVisibility(View.GONE);
            Log.d("setTitle", "header gone");
        }

        if (fragment == restaurantProfileFragment){
            btnOption.setVisibility(View.VISIBLE);
        }else if (fragment == userProfile && userId != currentProfileUserId){
            btnOption.setVisibility(View.VISIBLE);
        }else {
            btnOption.setVisibility(View.GONE);
        }
        if (fragment == storeFragment){
            searchHeader.setVisibility(View.GONE);
            header.setVisibility(View.VISIBLE);
            header1.setVisibility(View.GONE);
            titleHome.setText("Store");
            btnStoreHistory.setVisibility(View.VISIBLE);
        }else {
            btnStoreHistory.setVisibility(View.GONE);
        }

        if (fragment == storeHistoryFragment){
            searchHeader.setVisibility(View.GONE);
            header.setVisibility(View.VISIBLE);
            header1.setVisibility(View.GONE);
            titleHome.setText("Purchases");
        }
       // Log.d("check ids","myId:"+userId+" userId:"+currentProfileUserId+" f: "+fragment.getClass().getSimpleName());
       /* if (fragment == userProfile && currentProfileUserId.equals(userId)){
            btnOption.setVisibility(View.VISIBLE);
            Log.d("opton btn", "show");
        }else if(fragment != restaurantProfileFragment && fragment != userProfile) {
            btnOption.setVisibility(View.GONE);
            Log.d("opton btn", "hide");
        }*/
    }
    static final int DISH_SEARCH = 0;
    static final int USER_SEARCH = 1;
    static final int RESTAURANT_SEARCH = 2;

    @Override
    public void resultClick(int resultType, String id, String dishName) {
        switch (resultType){
            case USER_SEARCH:
                /*userProfile = new UserProfile();
                userProfile.userProfile1(id);
                setFragmentView(userProfile, R.id.container, -1, true);*/
                userProfileOpen(id);
                getFragmentManager().beginTransaction().remove(searchFragment).commit();
                break;
            case RESTAURANT_SEARCH:

                Log.d("restaurantId", id+"");
                Bundle bundle = new Bundle();
                bundle.putString("restaurantId", id);

                /*RestaurantProfileFragment restaurantProfileFragment = new RestaurantProfileFragment();
                restaurantProfileFragment.setArguments(bundle);
                setFragmentView(restaurantProfileFragment, R.id.container, -1, true);*/
                openRProfile(id);
                getFragmentManager().beginTransaction().remove(searchFragment).commit();
                break;
            case DISH_SEARCH:
                /*dishResultFragment = new DiscoverFragment();
                dishResultFragment.pageType = 1;
                dishResultFragment.dishName = dishName;
                setFragmentView(dishResultFragment, R.id.container, -1, true);
                getFragmentManager().beginTransaction().remove(searchFragment).commit();*/
                dishSearchByName(dishName, true);
                break;
        }
        hideSoftKeyboard();

        Log.d("result click", "resutlType: "+ resultType+" id:"+id);
    }

    private void dishSearchByName(String dishName, boolean fromSearch){
        dishResultFragment = new DiscoverFragment();
        dishResultFragment.pageType = 1;
        dishResultFragment.dishName = dishName;
        dishSearchedName = dishName;
        setFragmentView(dishResultFragment, R.id.container, -1, true);
        if (fromSearch){
            getFragmentManager().beginTransaction().remove(searchFragment).commit();
        }

    }

    @Override
    public void openComment(String postId) {
        Log.d("callback open comment", postId);

        /*if(this.getFragmentManager().findFragmentById(R.id.container1) != null){
            currentFragment = this.getFragmentManager().findFragmentById(R.id.container1);
        }*/
        if(this.getFragmentManager().findFragmentById(R.id.container1) != null){
            Log.d("fragment c1", this.getFragmentManager().findFragmentById(R.id.container1)+"");
            getFragmentManager().beginTransaction().remove(this.getFragmentManager().findFragmentById(R.id.container1)).commit();
        }
        Bundle bundle = new Bundle();
        bundle.putString("postId", postId);
        commentFragment = new CommentFragment();
        commentFragment.setArguments(bundle);
        setFragmentView(commentFragment, R.id.container1, -1, true);

       /* if(currentFragment == openRPostFragment){
            getFragmentManager().beginTransaction().remove(openRPostFragment).commit();
            Bundle bundle = new Bundle();
            bundle.putString("postId", postId);
            commentFragment = new CommentFragment();
            commentFragment.setArguments(bundle);
            setFragmentView(commentFragment, R.id.container1, -1, true);
        }else if(currentFragment == openPostFragment) {
            getFragmentManager().beginTransaction().remove(openPostFragment).commit();
            Bundle bundle = new Bundle();
            bundle.putString("postId", postId);
            commentFragment = new CommentFragment();
            commentFragment.setArguments(bundle);
            setFragmentView(commentFragment, R.id.container1, -1, true);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("postId", postId);
            commentFragment = new CommentFragment();
            commentFragment.setArguments(bundle);
            setFragmentView(commentFragment, R.id.container1, -1, true);
        }*/
    }

    @Override
    public void notiClicked(String eventType, String raiserId, String raiserThumb, String eventDate, String elementId) {
        if(eventType.equals("2") || eventType.equals("4") || eventType.equals("9") || eventType.equals("12") || eventType.equals("11")){
           // commentFragment = new CommentFragment(elementId);
            Bundle bundle = new Bundle();
            bundle.putString("postId", elementId);
            commentFragment = new CommentFragment();
            commentFragment.setArguments(bundle);
            setFragmentView(commentFragment, R.id.container1, -1, true);
        }else if (eventType.equals("5")){
            /*userProfile = new UserProfile();
            userProfile.userProfile1(elementId);
            setFragmentView(userProfile, R.id.container, -1, true);*/
            userProfileOpen(userId);
        }
    }
    //-------------------------------------------------------------

    private void clearBackStack() {
        FragmentManager manager = getFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        photo = null;
    }
    @Override
    public void restaurantOpen(String rId) {
        openRProfile(rId);
    }
    @Override
    public void apiResponse(JSONObject response, String tag) {
        if (tag.equals("bookSlot")){
            setFragmentView(storeHistoryFragment, R.id.container, -1, true);
        }
        Log.d("api response home", tag);
    }
    public void openWebPage(String url, String title){
        webViewFragment = new WebViewFragment();
        webViewFragment.webViewFragment1(url);
        setFragmentView (webViewFragment, R.id.container, 0, true);
        //titleHome.setText("Legal");
        if (title != null){
            titleHome.setText(title);
        }
    }
    @Override
    public void storeHistory(String type, String value) {
        openWebPage(value, null);
        titleHome.setText("");
    }
}
