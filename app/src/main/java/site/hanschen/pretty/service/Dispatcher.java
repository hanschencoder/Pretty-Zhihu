package site.hanschen.pretty.service;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.WorkerThread;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import site.hanschen.pretty.db.bean.Question;
import site.hanschen.pretty.db.repository.PrettyRepository;
import site.hanschen.pretty.zhihu.ZhiHuApi;

/**
 * @author HansChen
 */
public class Dispatcher {

    private static final int MSG_ENQUEUE_TASK    = 0;
    private static final int MSG_DEQUEUE_TASK    = 1;
    private static final int MSG_HUNTER_COMPLETE = 2;
    private static final int MSG_HUNTER_FAILED   = 3;
    private static final int MSG_NEXT_BATCH      = 4;

    private Handler            mWorkerHandler;
    private Handler            mMainHandler;
    private HandlerThread      mWorkerThread;
    private PrettyRepository   mPrettyRepository;
    private ZhiHuApi           mApi;
    private ThreadPoolExecutor mExecutor;

    private List<UrlHunter>           mTaskArray;
    private SparseArray<List<String>> mBatch;

    public Dispatcher(PrettyRepository repository, ZhiHuApi api, ThreadPoolExecutor executor, Handler mainHandler) {
        this.mPrettyRepository = repository;
        this.mApi = api;
        this.mExecutor = executor;
        this.mMainHandler = mainHandler;

        mWorkerThread = new HandlerThread("worker thread");
        mWorkerThread.start();
        mWorkerHandler = new Handler(mWorkerThread.getLooper(), mWorkerCallback);
        mTaskArray = new ArrayList<>();
        mBatch = new SparseArray<>();
    }

    void shutdown() {
        mWorkerHandler.removeCallbacksAndMessages(null);
        mWorkerThread.quit();
        mTaskArray.clear();
    }

    public void dispatchAddTask(int questionId) {
        Message message = mWorkerHandler.obtainMessage(MSG_ENQUEUE_TASK);
        message.arg1 = questionId;
        mWorkerHandler.sendMessage(message);
    }

    public void dispatchRemoveTask(int questionId) {
        Message message = mWorkerHandler.obtainMessage(MSG_DEQUEUE_TASK);
        message.arg1 = questionId;
        mWorkerHandler.sendMessage(message);
    }

    public void dispatchHuntComplete(UrlHunter hunter) {
        Message message = mWorkerHandler.obtainMessage(MSG_HUNTER_COMPLETE);
        message.obj = hunter;
        mWorkerHandler.sendMessage(message);
    }

    public void dispatchHuntFailed(UrlHunter hunter) {
        Message message = mWorkerHandler.obtainMessage(MSG_HUNTER_FAILED);
        message.obj = hunter;
        mWorkerHandler.sendMessage(message);
    }

    @WorkerThread
    private void performAddTask(int questionId) {
        Question question = mPrettyRepository.getQuestion(questionId);
        if (question != null) {
            for (int offset = 0; offset < question.getAnswerCount(); offset += 10) {
                UrlHunter hunter = new UrlHunter(questionId, 10, offset, mApi, this);
                mTaskArray.add(hunter);
                hunter.setFuture(mExecutor.submit(hunter));
            }
        }
    }

    @WorkerThread
    private void performRemoveTask(int questionId) {
        Iterator<UrlHunter> iterator = mTaskArray.iterator();
        while (iterator.hasNext()) {
            UrlHunter h = iterator.next();
            if (h.getQuestionId() == questionId && h.cancel()) {
                iterator.remove();
            }
        }
    }

    @WorkerThread
    private void performHuntComplete(UrlHunter hunter) {
        mTaskArray.remove(hunter);
        batch(hunter);
    }

    @WorkerThread
    private void batch(UrlHunter hunter) {
        if (hunter.isCancelled()) {
            return;
        }
        List<String> urls = mBatch.get(hunter.getQuestionId());
        if (urls == null) {
            mBatch.put(hunter.getQuestionId(), hunter.getResult());
        } else {
            urls.addAll(hunter.getResult());
        }
        if (!mWorkerHandler.hasMessages(MSG_NEXT_BATCH)) {
            mWorkerHandler.sendEmptyMessageDelayed(MSG_NEXT_BATCH, 1000);
        }
    }

    @WorkerThread
    private void performHuntFailed(UrlHunter hunter) {
        mTaskArray.remove(hunter);
    }

    @WorkerThread
    private void performBatch() {
        SparseArray copy = mBatch.clone();
        mBatch.clear();
        mMainHandler.sendMessage(mMainHandler.obtainMessage(TaskService.MSG_HUNT_COMPLETE, copy));
    }

    @SuppressWarnings("FieldCanBeLocal")
    private Handler.Callback mWorkerCallback = new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ENQUEUE_TASK:
                    performAddTask(msg.arg1);
                    break;
                case MSG_DEQUEUE_TASK:
                    performRemoveTask(msg.arg1);
                    break;
                case MSG_HUNTER_COMPLETE:
                    performHuntComplete((UrlHunter) msg.obj);
                    break;
                case MSG_HUNTER_FAILED:
                    performHuntFailed((UrlHunter) msg.obj);
                    break;
                case MSG_NEXT_BATCH:
                    performBatch();
                    break;
            }
            return true;
        }
    };
}
