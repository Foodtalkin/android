package in.foodtalk.android.app;

/**
 * Created by RetailAdmin on 19-04-2016.
 */
public class Config {
    private static  String IP = "52.74.13.4";
    public static String URL_LOGIN = "http://"+IP+"/index.php/service/auth/signin"; // ----testing url
   // public static String URL_LOGIN = "http://52.74.136.146/index.php/service/auth/signin";

    public static String URL_POST_LIST = "http://"+IP+"/index.php/service/post/list";

    public static String URL_POST_LIKE = "http://"+IP+"/index.php/service/like/add";
    public static String URL_POST_UNLIKE = "http://"+IP+"/index.php/service/like/delete";

    public static String URL_POST_BOOKMARK = "http://"+IP+"/index.php/service/bookmark/add";
    public static String URL_POST_REMOVE_BOOKMARK = "http://"+IP+"/index.php/service/bookmark/delete";

    public static String URL_POST_REPORT = "http://"+IP+"/index.php/service/flag/add";
    public static String URL_POST_DELETE = "http://"+IP+"/index.php/service/post/delete";

    public static String URL_POST_DISCOVER = "http://"+IP+"/index.php/service/post/getImageCheckInPosts";

    public static String URL_USER_PROFILE = "http://"+IP+"/index.php/service/user/getProfile";
    public static String URL_USER_POST_IMAGE = "http://"+IP+"/index.php/service/user/getImagePosts";

    public static String URL_FAVOURITES = "http://"+IP+"/index.php/service/bookmark/list";

    public static String URL_FOLLOW = "http://"+IP+"/index.php/service/follower/follow";
    public static String URL_UNFOLLOW = "http://"+IP+"/index.php/service/follower/unfollow";

    public static String URL_RESTAURANT_PROFILE = "http://"+IP+"/index.php/service/restaurant/getProfile";

    public static String URL_NEAR_BY_RESTAURANT = "http://"+IP+"/index.php/service/restaurant/list";

    public static String URL_DISH_NAME = "http://"+IP+"/index.php/service/dish/list";

    public static String URL_POST_CREATE = "http://"+IP+"/index.php/service/post/create";

    public static String URL_ADD_RESTAURANT = "http://"+IP+"/index.php/service/restaurant/add";
    public static String URL_REGION_LIST = "http://"+IP+"/index.php/service/region/list";
}