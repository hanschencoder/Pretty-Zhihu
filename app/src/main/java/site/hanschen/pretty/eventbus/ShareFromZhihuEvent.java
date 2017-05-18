package site.hanschen.pretty.eventbus;

/**
 * @author HansChen
 */
public class ShareFromZhihuEvent {

    public String title;
    public String url;

    public ShareFromZhihuEvent(String title, String url) {
        this.title = title;
        this.url = url;
    }
}
