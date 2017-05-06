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
import site.hanschen.pretty.base.HttpClient;
import site.hanschen.pretty.utils.JsonUtils;
import site.hanschen.pretty.zhihu.bean.AnswerList;
import site.hanschen.pretty.zhihu.bean.RequestAnswerParams;

/**
 * @author HansChen
 */
public class ZhiHuApiApiImpl implements ZhiHuApi {

    private HttpClient httpClient = new HttpClient();

    @Override
    public boolean isUrlValid(String url) {
        String regex = "https://www\\.zhihu\\.com/question/\\d+";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(url);
        return matcher.matches();
    }

    @Override
    public int parseQuestionId(String url) {
        int index = url.lastIndexOf('/');
        return Integer.parseInt(url.substring(index + 1));
    }

    @Override
    public String parseQuestionTitle(String html) {
        String regex = "<h1 class=\"QuestionHeader-title\">([\\S ]+)</h1>";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(html);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    @Override
    public int parseAnswerCount(String html) {
        String regex = "<h4 class=\"List-headerText\"><span>(\\d+) 个回答</span></h4>";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(html);
        if (matcher.find()) {
            String count = matcher.group(1);
            return Integer.valueOf(count);
        }

        return 0;
    }

    @Override
    public List<String> parsePictureList(String answer) {
        List<String> pictures = new ArrayList<>();
        String regex = "data-actualsrc=\"(https://pic[0-9]+\\.zhimg\\.com/[0-9a-zA-Z\\-]+_b\\.(jpg|png))\"";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(answer);
        while (matcher.find()) {
            pictures.add(matcher.group(1));
        }
        return pictures;
    }

    @Override
    public String getHtml(int questionId) throws IOException {
        Log.d("Hans", "getHtml: " + "https://www.zhihu.com/question/" + questionId);
        return httpClient.httpGet("https://www.zhihu.com/question/" + questionId);
    }

    @Override
    public AnswerList getAnswerList(int questionId, int pageSize, int offset) throws IOException {
        RequestAnswerParams params = new RequestAnswerParams(questionId, pageSize, offset);
        Log.d("Hans", "getAnswerList: " + JsonUtils.toJson(params));
        FormBody body = new FormBody.Builder().add("method", "next").add("params", JsonUtils.toJson(params)).build();
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/x-www-form-urlencoded");

        String answer = httpClient.httpPost("https://www.zhihu.com/node/QuestionAnswerListV2", header, body);
        AnswerList answerList = JsonUtils.fromJsonObject(answer, AnswerList.class);
        Log.d("Hans", "answerList: " + answerList.msg.size());
        return answerList;
    }
}
