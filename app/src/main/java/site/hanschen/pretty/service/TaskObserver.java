package site.hanschen.pretty.service;

import java.util.List;

import site.hanschen.pretty.db.bean.Picture;

/**
 * @author HansChen
 */
public interface TaskObserver {

    void onFetchStart(final int questionId);

    void onFetchProgress(final int questionId, final int progress);

    void onFetch(final int questionId, final List<Picture> pictures);
}
