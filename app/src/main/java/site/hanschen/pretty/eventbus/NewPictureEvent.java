package site.hanschen.pretty.eventbus;

import site.hanschen.pretty.db.bean.Picture;

/**
 * @author HansChen
 */
public class NewPictureEvent {

    public Picture picture;

    public NewPictureEvent(Picture picture) {
        this.picture = picture;
    }
}
