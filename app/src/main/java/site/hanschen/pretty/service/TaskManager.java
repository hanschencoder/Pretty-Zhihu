package site.hanschen.pretty.service;

/**
 * @author HansChen
 */
public interface TaskManager {

    boolean isFetching(final int questionId);

    void startFetchPicture(final int questionId);

    void stopFetchPicture(final int questionId);

    void registerObserver(TaskObserver observer);

    void unregisterObserver(TaskObserver observer);
}
