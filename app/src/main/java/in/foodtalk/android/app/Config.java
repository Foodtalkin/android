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

    public static String URL_POST_BOOKMARK = "http://"+IP+"/index.php/service/like/delete";
}
