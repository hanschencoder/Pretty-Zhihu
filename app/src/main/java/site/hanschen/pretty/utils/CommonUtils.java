package site.hanschen.pretty.utils;

/**
 * @author HansChen
 */
public class CommonUtils {

    private CommonUtils() {
    }

    public static String getSmallPicture(String url) {
        return url.replace("_b", "_100w");
    }
}
