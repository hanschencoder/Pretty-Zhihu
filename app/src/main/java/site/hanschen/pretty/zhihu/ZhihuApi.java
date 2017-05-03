package site.hanschen.pretty.zhihu;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import site.hanschen.pretty.utils.JsonUtils;
import site.hanschen.pretty.zhihu.bean.AnswerList;
import site.hanschen.pretty.zhihu.bean.RequestAnswerParams;

/**
 * @author HansChen
 */
public class ZhihuApi {

    private HttpClient httpClient = new HttpClient();

    public String getHtml(int questionId) throws IOException {
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

    public AnswerList getAnswerList(int questionId, int pageSize, int offset) throws IOException {

        Log.d("Hans", String.format("requestId:%d, pageSize:%d, offset:%d", questionId, pageSize, offset));
        RequestAnswerParams params = new RequestAnswerParams(questionId, pageSize, offset);
        FormBody body = new FormBody.Builder().add("method", "next").add("params", JsonUtils.toJson(params)).build();
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/x-www-form-urlencoded");

        String answer = httpClient.httpPost("https://www.zhihu.com/node/QuestionAnswerListV2", header, body);
        return JsonUtils.fromJsonObject(answer, AnswerList.class);
    }

    public List<String> getPictureList(String answer) {
        List<String> pictures = new ArrayList<>();
        String regex = "data-actualsrc=\"(https://pic[0-9]+\\.zhimg\\.com/[0-9a-zA-Z\\-]+_b\\.(jpg|png))\"";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(answer);
        while (matcher.find()) {
            pictures.add(matcher.group(1));
        }
        return pictures;
    }
}
