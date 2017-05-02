package site.hanschen.pretty.zhihu;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;

/**
 * @author HansChen
 */
public class ZhihuApi {

    private HttpClient httpClient = new HttpClient();

    public String getHtml(String questionId) throws IOException {
        String url = "https://www.zhihu.com/question/" + questionId;
        return httpClient.httpGet(url);
    }

    public int getAnswerCount(String html) {
        String regex = "<h4 class=\"List-headerText\"><span>(\\d+) 个回答</span></h4>";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(html);
        if (matcher.find()) {
            String count = matcher.group(1);
            return Integer.valueOf(count);
        }

        return 0;
    }

    public String getAnswer(String questionId) throws IOException {

        String params = "{\"url_token\":" + questionId + ",\"pagesize\":50,\"offset\":0}";
        FormBody body = new FormBody.Builder().add("method", "next").add("params", params).build();

        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/x-www-form-urlencoded");

        return httpClient.httpPost("https://www.zhihu.com/node/QuestionAnswerListV2", header, body);
    }
}
