package in.foodtalk.android.module;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

import in.foodtalk.android.R;
import in.foodtalk.android.communicator.HeadSpannableCallback;
import in.foodtalk.android.object.UserMention;

/**
 * Created by RetailAdmin on 16-05-2016.
 */
public class HeadSpannable {
    Context context;

    private final int USER_PROFILE = 1;
    private final int DISH = 2;
    private final int RESTAURANT_PROFILE = 3;

    private String requestFrom;

    HeadSpannableCallback headSpannableCallback;

    public HeadSpannable(Context context){
        this.context = context;
        headSpannableCallback = (HeadSpannableCallback) context;

    }
    public void code(TextView txt, String userName, String dishName, String restaurantName, String userId, String checkinRestaurantId, Boolean rLink, String requestFrom){
        //String mystring = userName;

        this.requestFrom = requestFrom;
        SpannableString uName= new SpannableString(userName);
        uName.setSpan(new MyClickableSpan(userName, userId , checkinRestaurantId, USER_PROFILE), 0, uName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        uName.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.card_head_highlight)), 0, uName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //String mystring1 ="Pizza Hut";
        SpannableString dName= new SpannableString(dishName);
        dName.setSpan(new MyClickableSpan(dishName, userId , checkinRestaurantId, DISH), 0, dName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        dName.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.card_head_highlight)), 0, dName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString rName= new SpannableString(restaurantName);
        rName.setSpan(new MyClickableSpan(restaurantName, userId , checkinRestaurantId, RESTAURANT_PROFILE), 0, rName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        rName.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.card_head_highlight)), 0, rName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        txt.setText(uName);
        txt.append(" is having ");
        txt.append(dName);
        if (!restaurantName.equals("")){
            txt.append(" at ");
        }

        if (rLink){
            txt.append(rName);
        }else {
            txt.append(restaurantName);
        }
        makeLinksFocusable(txt);
    }
    class MyClickableSpan extends ClickableSpan {// extend ClickableSpan

        String clicked;
        String userId;
        String checkinRestaurantId;
        int viewType;

        public MyClickableSpan(String string, String uId, String rId, int clickType) {
            super();
            clicked = string;
            userId = uId;
            checkinRestaurantId = rId;
            viewType = clickType;
        }
        public void onClick(View tv) {
            //Toast.makeText(,clicked , Toast.LENGTH_SHORT).show();
            headSpannableCallback.spannableTxt(userId, checkinRestaurantId, clicked, viewType, requestFrom);
            Log.d("onTextClick", clicked+" userId: "+userId+" checkinRestaurantId: " +checkinRestaurantId);
        }
        public void updateDrawState(TextPaint ds) {// override updateDrawState
            ds.setUnderlineText(false); // set to false to remove underline
            //ds.bgColor = Integer.parseInt(null);
            ds.bgColor = Color.WHITE;
        }
    }


    public SpannableStringBuilder commentSpannable(String userName, String userId, String comment, final List<UserMention> userMentionList, int textWidth){

        Log.d("userMention", userMentionList.size()+"");

        String commentTxt;
        String blankSpace = "";

        Log.d("textView width", textWidth+"");

        int spaceLength = (int) Math.floor(textWidth/11);

        for (int i = 0; i< spaceLength; i++){
            blankSpace = blankSpace +" ";
        }

        commentTxt = blankSpace + comment;

        SpannableStringBuilder ssb = new SpannableStringBuilder(commentTxt);


//        userMentionList.size();
        //Log.d("userMentionList spann", userMentionList.size()+"");
        int idx1 = commentTxt.indexOf("@");
        int idx2 = 0;
        while (idx1 != -1){
            idx2 = commentTxt.indexOf(" ", idx1) + 1;

            Log.d("idx2", idx2+"");
            if (idx2 != 0){
                final String clickString = commentTxt.substring(idx1, idx2);
                ssb.setSpan(new CommentClickable(clickString, userMentionList), idx1, idx2, 0);
                idx1 = commentTxt.indexOf("@", idx2);
            }else {
                idx1 = -1;
            }
        }
        return ssb;
    }

    class CommentClickable extends ClickableSpan{

        String clickString;
        List<UserMention> userMentionList;

        public CommentClickable (String clickString , List<UserMention> userMentionList){
            this.clickString = clickString;
            this.userMentionList = userMentionList;
        }
        @Override
        public void onClick(View widget) {
            Log.d("userMentionList", userMentionList.size()+"");
            for (int i =0; i< userMentionList.size(); i++ ){
                String userMention = "@"+userMentionList.get(i).userName;
                if (userMention.equals(clickString.trim())){
                    Log.d("userId", userMentionList.get(i).userId);
                    headSpannableCallback.spannableTxt(userMentionList.get(i).userId, null, null, USER_PROFILE, "commentFragment");
                }
            }
            Log.d("comment click", "|"+clickString.trim()+"|");
        }
    }
    private void makeLinksFocusable(TextView tv) {
        MovementMethod m = tv.getMovementMethod();
        if ((m == null) || !(m instanceof LinkMovementMethod)) {
            if (tv.getLinksClickable()) {
                tv.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }
}
