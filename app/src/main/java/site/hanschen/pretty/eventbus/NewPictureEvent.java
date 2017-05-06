package site.hanschen.pretty.eventbus;

import java.util.List;

import site.hanschen.pretty.db.bean.Picture;

/**
 * @author HansChen
 */
public class NewPictureEvent {

    public List<Picture> pictures;

    public NewPictureEvent(List<Picture> pictures) {
        this.pictures = pictures;
    }
}
