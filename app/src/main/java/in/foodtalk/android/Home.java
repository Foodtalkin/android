package in.foodtalk.android;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import in.foodtalk.android.apicall.PostBookmarkApi;
import in.foodtalk.android.apicall.PostLikeApi;
import in.foodtalk.android.apicall.PostReportApi;
import in.foodtalk.android.communicator.AddRestaurantCallback;
import in.foodtalk.android.communicator.AddedRestaurantCallback;
import in.foodtalk.android.communicator.CamBitmapCallback;
import in.foodtalk.android.communicator.CheckInCallback;
import in.foodtalk.android.communicator.CloudinaryCallback;
import in.foodtalk.android.communicator.DishTaggingCallback;
import in.foodtalk.android.communicator.HeadSpannableCallback;
import in.foodtalk.android.communicator.MoreBtnCallback;
import in.foodtalk.android.communicator.PhoneCallback;
import in.foodtalk.android.communicator.PostBookmarkCallback;
import in.foodtalk.android.communicator.PostDeleteCallback;
import in.foodtalk.android.communicator.PostLikeCallback;
import in.foodtalk.android.communicator.PostOptionCallback;
import in.foodtalk.android.communicator.ProfilePostOpenCallback;
import in.foodtalk.android.communicator.ProfileRPostOpenCallback;
import in.foodtalk.android.communicator.RatingCallback;
import in.foodtalk.android.communicator.ReviewCallback;
import in.foodtalk.android.communicator.UserProfileCallback;
import in.foodtalk.android.communicator.UserThumbCallback;
import in.foodtalk.android.fragment.DiscoverFragment;
import in.foodtalk.android.fragment.FavouritesFragment;
import in.foodtalk.android.fragment.HomeFragment;
import in.foodtalk.android.fragment.MoreFragment;
import in.foodtalk.android.fragment.SearchFragment;
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
import in.foodtalk.android.module.CloudinaryUpload;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.NewPostUpload;
import in.foodtalk.android.object.CreatePostObj;
import in.foodtalk.android.object.RestaurantPostObj;
import in.foodtalk.android.object.UserPostObj;

public class Home extends AppCompatActivity implements View.OnClickListener,
        PostLikeCallback, PostBookmarkCallback, PostOptionCallback, PostDeleteCallback,
        MoreBtnCallback, UserProfileCallback, ProfilePostOpenCallback, FragmentManager.OnBackStackChangedListener,
        HeadSpannableCallback, UserThumbCallback, ProfileRPostOpenCallback, PhoneCallback,
        CheckInCallback, CamBitmapCallback, DishTaggingCallback , RatingCallback , ReviewCallback, AddRestaurantCallback,
        AddedRestaurantCallback {

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

    TextView titleHome;
    TextView subTitleHome;
    TextView titleHome1;

    RelativeLayout header;
    RelativeLayout header1;
    int pageNo;

    private final int USER_PROFILE = 1;
    private final int DISH = 2;
    private final int RESTAURANT_PROFILE = 3;


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


   // ImageCapture imageCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHandler(getApplicationContext());

       // imageCapture = new ImageCapture(this);


        //-------api init--------------------------
        postLikeApi = new PostLikeApi(this);
        postBookmarkApi = new PostBookmarkApi(this);
        postReportApi = new PostReportApi(this);
        //-----------------------------------------


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




        // Add the fragment to the 'fragment_container' FrameLayout
        getFragmentManager().beginTransaction()
                .add(R.id.container, homeFragment).commit();
        pageNo = 0;

        getFragmentManager().addOnBackStackChangedListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_home:
                //Log.d("onClick", "btn home");

                if (pageNo != 0) {
                    setFragmentView(homeFragment, R.id.container, 0, false);
                   // titleHome.setText("Home");
                    pageNo = 0;
                }
                break;
            case R.id.btn_discover:
                //Log.d("onClick", "btn discover");
                if (pageNo != 1) {
                    setFragmentView(discoverFragment, R.id.container, 1, false);
                    //titleHome.setText("Discover");
                    pageNo = 1;
                }
                break;
            case R.id.btn_newpost:
                if (pageNo != 2) {
                    newpostFragment = new CheckIn();
                    setFragmentView(newpostFragment, R.id.container1, 2, true);
                    //titleHome.setText("New Post");
                    //pageNo = 2;
                }
                //Log.d("onClick", "btn newpost");
                break;
            case R.id.btn_notification:
                if (pageNo != 3) {
                    setFragmentView(notiFragment, R.id.container, 3, false);
                   // titleHome.setText("Notification");
                    pageNo = 3;
                }

                //Log.d("onClick", "btn notification");
                break;
            case R.id.btn_more:
                if (pageNo != 4) {
                    setFragmentView(moreFragment, R.id.container, 4, false);
                    //titleHome.setText("More");
                    pageNo = 4;
                }
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
        }
    }



    private void setFragmentView(Fragment newFragment, int container, int pageN, boolean bStack) {
        String backStateName = newFragment.getClass().getName();

            setTitle(newFragment);


            //Log.d("setFragmentView","call function");

        //Temp disabled--
        /*
            if (newFragment == userProfile) {
                //Log.d("userProfile","header1 visible");
                header.setVisibility(View.GONE);
                header1.setVisibility(View.VISIBLE);
            } else if (newFragment == openPostFragment) {
                header.setVisibility(View.GONE);
                header1.setVisibility(View.VISIBLE);
            } else {
                header.setVisibility(View.VISIBLE);
                header1.setVisibility(View.GONE);
            }*/

        //Log.d("pageNo selected", pageN+"");

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
            android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
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
            transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            Fragment f = this.getFragmentManager().findFragmentById(R.id.container);
            String fName = f.getClass().getSimpleName();

        } else {
            super.onBackPressed();
        }
    }
    @Override
    public void onBackStackChanged() {
        Fragment f = this.getFragmentManager().findFragmentById(R.id.container);
        String fName = f.getClass().getSimpleName();

        setTitle(f);
        //Log.d("onBackStackChanged", f.getClass().getSimpleName());
        if (!fName.equals("UserProfile")) {
           // header.setVisibility(View.VISIBLE);
           // header1.setVisibility(View.GONE);
        }
        switch (fName) {
            case "UserProfile":
                //header.setVisibility(View.VISIBLE);
                //header1.setVisibility(View.GONE);
                break;
            case "MoreFragment":
                //titleHome.setText("More");
                break;
            case "FavouritesFragment":
                //titleHome.setText("Favourites");
                break;
        }
    }


    private void logOut() {
        db.resetTables();
        Intent i = new Intent(this, FbLogin.class);
        startActivity(i);
        finish();
    }

    @Override
    public void like(int position, String postId, Boolean like) {
        Log.d("likeResponse", position + " postid: " + postId);
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
                //startActivity(callIntent);
            }
        });
        txtPhone2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("call", phone2);
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+phone2));
               // startActivity(callIntent);
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
        final Dialog dialogImgFrom = new Dialog(this);
        dialogImgFrom.setContentView(R.layout.dialog_img_from);

        TextView btnCamera = (TextView) dialogImgFrom.findViewById(R.id.btn_camera_imgfrom);
        TextView btnGallery = (TextView) dialogImgFrom.findViewById(R.id.btn_gallery_imgfrom);

        dialogImgFrom.show();
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogImgFrom.dismiss();
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file1));
                startActivityForResult(intent, 1);
            }
        });
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogImgFrom.dismiss();
                Log.d("dialogImgFrom", "bt Gallery");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, FROM_GALLERY);
            }
        });
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
               userProfile = new UserProfile(userId);
               setFragmentView(userProfile, R.id.container, -1, true);
               break;
           case "options":
               setFragmentView(optionsFragment, R.id.container, -1, true);
               break;
           case "legal":
               Log.d("btn click","setFragment webview");
               webViewFragment = new WebViewFragment("http://www.foodtalkindia.com/document.html");
               setFragmentView (webViewFragment, R.id.container, -1, true);
               //titleHome.setText("Legal");


               setTitle(legalFragment);

               break;
           case "favourites":
               setFragmentView(favouritesFragment, R.id.container, -1, true);
               break;
       }

    }
    @Override
    public void getUserInfo(String points, String userName) {
        //subTitleHome.setText(points);
        subTitleHome.setText(points+" Points");
        titleHome1.setText(userName);
        //Log.d("points", points);
    }
    @Override
    public void postOpen(List<UserPostObj> postObj, String postId, String userId) {
        openPostFragment = new OpenPostFragment(postObj, postId, userId);
        setFragmentView (openPostFragment, R.id.container1, -1, true);
        Log.d("postOpen","userId: "+userId+" postId: "+postId);
    }

    @Override
    public void spannableTxt(String userId, String checkinRestaurantId, String dishName, int viewType, String requestFrom) {
        switch (viewType){
            case USER_PROFILE:
                if(!requestFrom.equals("UserProfile")){
                    userProfile = new UserProfile(userId);
                    setFragmentView(userProfile, R.id.container, -1, true);
                }

                break;
            case RESTAURANT_PROFILE:

                if (requestFrom.equals("UserProfile")){
                    getFragmentManager().beginTransaction().remove(openPostFragment).commit();
                }
                    restaurantProfileFragment = new RestaurantProfileFragment(checkinRestaurantId);
                    setFragmentView(restaurantProfileFragment, R.id.container, -1, true);


                Log.d("clicked","for restaurant");
                break;
            case DISH:
                Log.d("clicked","for Dish");
                break;
        }
    }

    @Override
    public void thumbClick(String userId) {
        userProfile = new UserProfile(userId);
        setFragmentView(userProfile, R.id.container, -1, true);
    }

    @Override
    public void rPostOpen(List<RestaurantPostObj> postObj, String postId, String restaurantId) {
        openRPostFragment = new OpenRPostFragment(postObj, postId, restaurantId);
        setFragmentView (openRPostFragment, R.id.container1, -1, true);
        Log.d("postOpen","userId: "+userId+" postId: "+postId);
    }

    @Override
    public void phoneBtn(String phone1, String phone2) {
        Log.d("phone numbers", phone1+" : "+phone2);
        callDialog(phone1, phone2);
    }
    @Override
    public void checkInRestaurant(String restaurantId, String restaurantName) {
        pickImage(restaurantId, restaurantName);
    }
    private void pickImage(String restaurantId, String restaurantName){
        this.restaurantIdNewPost = restaurantId;

        Log.d("checkInRestarant","rId"+restaurantId+" rName: "+restaurantName);
        cameraFragment = new CameraFragment();
        restaurantNameNewPost = restaurantName;
        dialogImgFrom();
        //setFragmentView (cameraFragment, R.id.container1, 4, true);
        hideSoftKeyboard();
    }

    @Override
    public void capturedBitmap(Bitmap photo , File file) {

        this.photo = photo;

        this.file = file;

        Log.d("capuredBitmap", "call");
        dishTagging = new DishTagging(photo);
        setFragmentView(dishTagging, R.id.container1, 0, true);

        //showSoftKeyboard(layout);
    }
    public void capturedBitmap1(Bitmap photo , File file) {

        this.photo = photo;

        this.file = file;

        Log.d("capuredBitmap", "call");
        dishTagging = new DishTagging(photo);
        setFragmentView(dishTagging, R.id.container1, 0, true);

        //showSoftKeyboard(layout);
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
        ratingFragment = new RatingFragment(photo);
        setFragmentView(ratingFragment, R.id.container1, 0, true);
    }
    @Override
    public void goforReview(String rate) {
        Log.d("goforReview", rate);
        rating = rate;
        reviewFragment = new ReviewFragment(photo);
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
    }

    @Override
    public void addNewRestaurant() {
        addRestaurant = new AddRestaurant();
        setFragmentView(addRestaurant, R.id.container1, 0, true);
    }

    @Override
    public void restaurantAdded(String rId) {
        Log.d("restaurant added", "Rid: "+rId);

        newpostFragment = new CheckIn();
        Bundle bundle = new Bundle();
        bundle.putString("rId", rId);
        newpostFragment.setArguments(bundle);
        setFragmentView(newpostFragment, R.id.container1, 0, true);
    }


    //---------camera and crop intent----------------------------
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("onAcrivitiresult", requestCode+"");
        //if request code is same we pass as argument in startActivityForResult
        if(requestCode==1 && resultCode == RESULT_OK){
            //create instance of File with same name we created before to get image from storage
            //File file = new File(Environment.getExternalStorageDirectory()+File.separator + "img.jpg");
            //Crop the captured image using an other intent

            // Bundle extras = data.getExtras();
            // Bitmap imageBitmap = (Bitmap) extras.get("data");

            Log.d("data", data+" ");

            try {
				/*the user's device may not support cropping*/
                //--cropCapturedImage(Uri.fromFile(file));
                Bitmap photo = decodeFile(file1);
                //Bitmap photo = setPic(file);
                //Bitmap photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(String.valueOf(file)));

//                Bitmap croppedBmp = Bitmap.createBitmap(photo, 0, 0,
                //     photo.getWidth() / 2, photo.getHeight());
                //imVCature_pic.setImageBitmap(photo);

                //-------
                startCropImageActivity(Uri.fromFile(file1));
                Log.d("onActivityResult","call cropimage activity");

                //--------
            }
            catch(ActivityNotFoundException aNFE){
                //display an error message if user device doesn't support
                String errorMessage = "Sorry - your device doesn't support the crop action!";
                Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                toast.show();
            }
            // Log.d("result code",CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE+" : "
            // + CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE+ ": "
            // + CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            Log.d("crop img","cropd");
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Log.d("Log","crop image activity request"+result.getUri());

                Bitmap photo = decodeFile(new File(result.getUri().getPath()));

               //-- imVCature_pic.setImageBitmap(photo);

               //-- camBitmapCallback.capturedBitmap(photo , new File(result.getUri().getPath()));
                capturedBitmap1(photo , new File(result.getUri().getPath()));
                // cropedImg.setImageUriAsync(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        if(requestCode==2){
            //Create an instance of bundle and get the returned data
            //Bundle extras = data.getExtras();
            //get the cropped bitmap from extras
            //--Bitmap thePic = extras.getParcelable("data");
            //set image bitmap to image view
            //--imVCature_pic.setImageBitmap(thePic);
            //Log.d("get extras", extras.getParcelable("data")+"");
            try {
                if(file.exists()){

                    Log.d("onActivityResult", "try to load image");
                    Bitmap photo = decodeFile(file1);
                    //Bitmap photo = setPic(file);
                    //Bitmap photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(String.valueOf(file)));
                    Bitmap croppedBmp = Bitmap.createBitmap(photo, 0, 0,
                            photo.getWidth() / 2, photo.getHeight());
                    // imVCature_pic.setImageBitmap(croppedBmp);
                }
                else {
                    Toast.makeText(this, "Error while save image", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (requestCode == FROM_GALLERY && resultCode == RESULT_OK && null != data){
            Uri selectedImage = data.getData();
            startCropImageActivity(selectedImage);
            Log.d("get result","from gallery"+ selectedImage);
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        // CropImage.activity(imageUri)
        //  .setGuidelines(CropImageView.Guidelines.ON)
        //  .setFixAspectRatio(true)
        //     .start(getActivity());
        //
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
        if (fragment == homeFragment){
            header.setVisibility(View.VISIBLE);
            header1.setVisibility(View.GONE);
            titleHome.setText("Home");
        }
        if (fragment == discoverFragment){
            header.setVisibility(View.VISIBLE);
            header1.setVisibility(View.GONE);
            titleHome.setText("Discover");
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
            titleHome.setText("Favourites");
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
            header.setVisibility(View.GONE);
            header1.setVisibility(View.VISIBLE);
        }

        if (fragment == restaurantProfileFragment){
            header.setVisibility(View.VISIBLE);
            header1.setVisibility(View.GONE);
            titleHome.setText("Restaurant");
        }

        if(fragment == homeFragment) {
            searchHeader.setVisibility(View.VISIBLE);
            header.setVisibility(View.GONE);
        }else if(fragment != searchFragment && fragment != userProfile) {
            header.setVisibility(View.VISIBLE);
            searchHeader.setVisibility(View.GONE);
        }
       /* case "UserProfile":
        header.setVisibility(View.VISIBLE);
        header1.setVisibility(View.GONE);
        break;
        case "MoreFragment":
        //titleHome.setText("More");
        break;
        case "FavouritesFragment":*/
        //titleHome.setText("Favourites");
    }
    //-------------------------------------------------------------
}