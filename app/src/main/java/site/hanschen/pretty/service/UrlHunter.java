package site.hanschen.pretty.service;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import site.hanschen.pretty.zhihu.ZhiHuApi;
import site.hanschen.pretty.zhihu.bean.AnswerList;

/**
 * @author HansChen
 */
public class UrlHunter implements Runnable {

    private int          mQuestionId;
    private int          mPageSize;
    private int          mOffset;
    private ZhiHuApi     mApi;
    private Dispatcher   mDispatcher;
    private Future       mFuture;
    private List<String> mResult;

    public UrlHunter(int questionId, int pageSize, int offset, ZhiHuApi api, Dispatcher dispatcher) {
        this.mQuestionId = questionId;
        this.mPageSize = pageSize;
        this.mOffset = offset;
        this.mApi = api;
        this.mDispatcher = dispatcher;
    }

    public Future getFuture() {
        return mFuture;
    }

    public void setFuture(Future future) {
        this.mFuture = future;
    }

    public boolean cancel() {
        Log.d("Hans", "cancel: " + toString());
        return mFuture != null && mFuture.cancel(false);
    }

    public boolean isCancelled() {
        return mFuture != null && mFuture.isCancelled();
    }

    public int getQuestionId() {
        return mQuestionId;
    }

    public List<String> getResult() {
        return mResult;
    }

    public List<String> hunt() throws IOException {
        List<String> urls = new ArrayList<>();
        AnswerList answerList = mApi.getAnswerList(mQuestionId, mPageSize, mOffset);
        for (String answer : answerList.msg) {
            urls.addAll(mApi.parsePictureList(answer));
        }
        return urls;
    }

    @Override
    public void run() {
        try {
            mResult = hunt();
            mDispatcher.dispatchHuntComplete(this);
        } catch (IOException e) {
            mDispatcher.dispatchHuntFailed(this);
        }
    }

    @Override
    public String toString() {
        return "UrlHunter{" + "mQuestionId=" + mQuestionId + ", mPageSize=" + mPageSize + ", mOffset=" + mOffset + '}';
    }
}
