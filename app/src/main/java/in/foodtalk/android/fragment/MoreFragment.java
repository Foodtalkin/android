package in.foodtalk.android.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import in.foodtalk.android.R;
import in.foodtalk.android.adapter.MoreAdapter;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.object.FavoritesObj;
import in.foodtalk.android.object.UserProfileObj;

/**
 * Created by RetailAdmin on 21-04-2016.
 */
public class MoreFragment extends Fragment {


    View layout;
    DatabaseHandler db;
    MoreAdapter moreAdapter;
    List<FavoritesObj> favorites;
    UserProfileObj userProfile;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;

    String userName, fullName, sId, fId, uId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.more_fragment, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.more_recycler_view);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        favorites = new ArrayList<>();
        userProfile = new UserProfileObj();

        recyclerView.setLayoutManager(linearLayoutManager);

        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        db = new DatabaseHandler(getActivity());

        userName = db.getUserDetails().get("userName");
        fullName = db.getUserDetails().get("fullName");
        uId = db.getUserDetails().get("userId");
        fId = db.getUserDetails().get("facebooId");
        sId = db.getUserDetails().get("sessionId");

        userProfile.fullName = fullName;
        userProfile.userName = userName;
        userProfile.image = "https://graph.facebook.com/"+fId+"/picture?type=large";

        moreAdapter = new MoreAdapter(getActivity(), userProfile, favorites);
        recyclerView.setAdapter(moreAdapter);
        Log.d("facebookId", fId);
    }
}
