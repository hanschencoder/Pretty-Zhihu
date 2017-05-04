package site.hanschen.pretty.zhihu;

import java.io.IOException;
import java.util.List;

import site.hanschen.pretty.zhihu.bean.AnswerList;

/**
 * @author HansChen
 */
public interface ZhiHuApi {

    boolean isUrlValid(String url);

    int parseQuestionId(String url);

    String parseQuestionTitle(String html);

    int parseAnswerCount(String html);

    List<String> parsePictureList(String answer);

    String getHtml(int questionId) throws IOException;

    AnswerList getAnswerList(int questionId, int pageSize, int offset) throws IOException;
}
