package site.hanschen.pretty.zhihu.bean;

/**
 * @author HansChen
 */
public class RequestAnswerParams {

    public int url_token;
    public int pagesize;
    public int offset;

    public RequestAnswerParams() {
    }

    public RequestAnswerParams(int url_token, int pagesize, int offset) {
        this.url_token = url_token;
        this.pagesize = pagesize;
        this.offset = offset;
    }
}
