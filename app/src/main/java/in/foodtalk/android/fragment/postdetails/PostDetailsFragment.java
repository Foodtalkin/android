package in.foodtalk.android.fragment.postdetails;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.app.Fragment;
//import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import in.foodtalk.android.R;
import in.foodtalk.android.module.DatabaseHandler;


/**
 * Created by RetailAdmin on 21-10-2016.
 */

public class PostDetailsFragment extends Fragment implements TabLayout.OnTabSelectedListener, View.OnTouchListener {
    View layout;
    FragmentManager fm;
    private FragmentActivity myContext;

    PagerAdapterPd adapter;

    ViewPager viewPager;

    ImageView iconHeart, iconComment, iconBookmark;

    LinearLayout tabLike, tabComment, tabBookmark;

    ViewPager pager;
    public String postId;
    public int setCurrentPage = -1;

    DatabaseHandler db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.post_details_fragment, container, false);
        viewPager = (ViewPager) layout.findViewById(R.id.viewpager);

        iconHeart = (ImageView) layout.findViewById(R.id.icon_heart_img);
        iconComment = (ImageView) layout.findViewById(R.id.icon_comment);
        iconBookmark = (ImageView) layout.findViewById(R.id.img_icon_bookmark);
        pager = (ViewPager) layout.findViewById(R.id.viewpager);
        pager.setOffscreenPageLimit(2);


        tabLike = (LinearLayout) layout.findViewById(R.id.icon_like_holder);
        tabComment = (LinearLayout) layout.findViewById(R.id.icon_comment_holder);
        tabBookmark = (LinearLayout) layout.findViewById(R.id.icon_bookmark_holder);

        tabLike.setOnTouchListener(this);
        tabComment.setOnTouchListener(this);
        tabBookmark.setOnTouchListener(this);



        db = new DatabaseHandler(getActivity());
        initialisePaging();
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        fm = myContext.getSupportFragmentManager();
        super.onAttach(activity);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
    List<android.support.v4.app.Fragment> fragments;
    private void initialisePaging(){

        Log.d("PostDetailsFragment","initialisePagin");
       //final List<Fragment> fragments = new Vector<Fragment>();
        fragments = new Vector<android.support.v4.app.Fragment>();
        fragments.add(android.support.v4.app.Fragment.instantiate(getActivity(),LikeListFragment.class.getName()));
        fragments.add(android.support.v4.app.Fragment.instantiate(getActivity(),CommentsPostFragment.class.getName()));
        fragments.add(android.support.v4.app.Fragment.instantiate(getActivity(),BookmarkListFragment.class.getName()));
        //fragments.add(Fragment.instantiate())


        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("postId", postId);
        hashMap.put("sessionId", db.getUserDetails().get("sessionId"));

        adapter = new PagerAdapterPd(fm, fragments, hashMap);

        pager.setAdapter(adapter);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                Log.d("onPageSelected",position+"");
                hideSoftKeyboard();
                switch (position){
                    case 0:
                        iconHeart.setImageResource(R.drawable.ic_heart_filled);
                        iconComment.setImageResource(R.drawable.ic_comment_card_24);
                        iconBookmark.setImageResource(R.drawable.ic_bookmark_card_24);
                        break;
                    case 1:
                        iconHeart.setImageResource(R.drawable.ic_like_card_24);
                        iconComment.setImageResource(R.drawable.ic_comment_filled);
                        iconBookmark.setImageResource(R.drawable.ic_bookmark_card_24);
                        break;
                    case 2:
                        iconHeart.setImageResource(R.drawable.ic_like_card_24);
                        iconComment.setImageResource(R.drawable.ic_comment_card_24);
                        iconBookmark.setImageResource(R.drawable.ic_bookmark_filled);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (setCurrentPage != -1){
            pager.setCurrentItem(setCurrentPage);
        }

    }
    public void hideSoftKeyboard() {
        if(getActivity().getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()){
            case R.id.icon_like_holder:
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        pager.setCurrentItem(0);
                        break;
                }
                break;
            case R.id.icon_comment_holder:
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        pager.setCurrentItem(1);
                        break;
                }
                break;
            case R.id.icon_bookmark_holder:
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        pager.setCurrentItem(2);
                        break;
                }
                break;
        }
        return false;
    }
}
