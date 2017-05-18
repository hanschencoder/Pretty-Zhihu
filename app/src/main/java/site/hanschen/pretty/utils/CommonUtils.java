package site.hanschen.pretty.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author HansChen
 */
public class CommonUtils {

    private CommonUtils() {
    }

    public static String getSmallPicture(String url) {
        return url.replace("_b", "_100w");
    }

    public static String getTitleFromShare(String shareText) {
        String regex = "([\\S ]+)(http[s]?://www\\.zhihu\\.com/question/[0-9]+)([\\S ]+)";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(shareText);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }

    public static String getUrlFromShare(String shareText) {
        String regex = "([\\S ]+)(http[s]?://www\\.zhihu\\.com/question/[0-9]+)([\\S ]+)";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(shareText);
        if (matcher.matches()) {
            return matcher.group(2);
        }
        return null;
    }
}
