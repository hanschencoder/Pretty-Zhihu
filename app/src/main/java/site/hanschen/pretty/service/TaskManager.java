package site.hanschen.pretty.service;

/**
 * @author HansChen
 */
public interface TaskManager {

    void startFetchPicture(final int questionId);

    void stopFetchPicture(final int questionId);
}
