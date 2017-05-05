package site.hanschen.pretty.eventbus;

/**
 * @author HansChen
 */
public class EditModeChangedEvent {

    public boolean isEditMode;

    public EditModeChangedEvent(boolean isEditMode) {
        this.isEditMode = isEditMode;
    }
}
