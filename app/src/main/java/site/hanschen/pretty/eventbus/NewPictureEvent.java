package site.hanschen.pretty.eventbus;

import java.util.List;

import site.hanschen.pretty.db.bean.Picture;

/**
 * @author HansChen
 */
public class NewPictureEvent {

    public int           questionId;
    public List<Picture> pictures;

    public NewPictureEvent(int questionId, List<Picture> pictures) {
        this.questionId = questionId;
        this.pictures = pictures;
    }
}
