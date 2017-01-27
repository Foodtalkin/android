package in.foodtalk.android.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;

import java.util.List;

import in.foodtalk.android.BuildConfig;
import in.foodtalk.android.FbLogin;
import in.foodtalk.android.R;
import in.foodtalk.android.module.DatabaseHandler;

/**
 * Created by RetailAdmin on 12-05-2016.
 */
public class OptionsFragment extends Fragment implements View.OnTouchListener {
    View layout;
    LinearLayout btnContact;
    LinearLayout btnLogout;

    DatabaseHandler db;
    String versionName;
    TextView txtVersion;

    String[] email = {"contact@foodtalkindia.com"};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.options_fragment, container, false);
        btnContact = (LinearLayout) layout.findViewById(R.id.btn_contact);
        btnLogout = (LinearLayout) layout.findViewById(R.id.btn_logout_options);
        txtVersion = (TextView) layout.findViewById(R.id.txt_version);
        btnContact.setOnTouchListener(this);
        btnLogout.setOnTouchListener(this);

        db = new DatabaseHandler(getActivity());

        PackageManager manager = getActivity().getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(
                    getActivity().getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        Log.d("OptionFragment BT", BuildConfig.BUILD_TYPE);


        if (versionName != null){
            if (BuildConfig.BUILD_TYPE.equals("debug")){
                txtVersion.setText("Version "+versionName+" "+BuildConfig.BUILD_TYPE);
            }
            else {
                txtVersion.setText("Version "+versionName);
            }
        }
        return layout;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()){
            case R.id.btn_contact:
                Log.d("btnclicked","");
               // mailTo();
                shareToGMail(email, "", "");
                break;
            case R.id.btn_logout_options:
                logOut();
                break;
        }
        return false;
    }

    private void mailTo(){
        /*Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, "info@foodtalkindia.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        intent.putExtra(Intent.EXTRA_TEXT, "I'm email body.");

        startActivity(Intent.createChooser(intent, "Send Email"));*/


        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_EMAIL, "contact@foodtalkindia.com");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT,"Email subject");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Body of Email");
        startActivity(Intent.createChooser(sendIntent, "Email:"));
    }
    public void shareToGMail(String[] email, String subject, String content) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, email);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
        final PackageManager pm = getActivity().getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
        ResolveInfo best = null;
        for(final ResolveInfo info : matches)
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                best = info;
        if (best != null)
            emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        getActivity().startActivity(emailIntent);
    }
    private void logOut(){
        db.resetTables();
        LoginManager.getInstance().logOut();
        Intent i = new Intent(getActivity(), FbLogin.class);
        startActivity(i);
        getActivity().finish();
    }
}
