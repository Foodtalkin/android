package in.foodtalk.android.object;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RetailAdmin on 21-06-2016.
 */
public class CommentObj {
    public String viewType;
    public String id;
    public String comment;
    public String createDate;
    public String currentDate;
    public String userId;
    public String userName;
    public String fullName;
    public String userImage;
    public String userThumb;
    public String timeElapsed;
    public List<UserMention> userMentionsList = new ArrayList<>();
}
