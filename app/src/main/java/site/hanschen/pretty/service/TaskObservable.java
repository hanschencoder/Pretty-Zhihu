package site.hanschen.pretty.service;


import android.database.Observable;

import java.util.List;

import site.hanschen.pretty.db.bean.Picture;

/**
 * @author HansChen
 */
class TaskObservable extends Observable<TaskObserver> {

    public void notifyFetchStart(final int questionId) {
        synchronized (mObservers) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onFetchStart(questionId);
            }
        }
    }

    public void notifyFetchProgress(final int questionId, final int progress) {
        synchronized (mObservers) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onFetchProgress(questionId, progress);
            }
        }
    }

    public void notifyFetch(final int questionId, final List<Picture> pictures) {
        synchronized (mObservers) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onFetch(questionId, pictures);
            }
        }
    }
}
