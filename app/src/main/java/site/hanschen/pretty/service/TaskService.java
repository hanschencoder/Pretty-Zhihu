package site.hanschen.pretty.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.util.SparseArray;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import site.hanschen.pretty.application.PrettyApplication;
import site.hanschen.pretty.db.bean.Picture;
import site.hanschen.pretty.db.repository.PrettyRepository;
import site.hanschen.pretty.eventbus.NewPictureEvent;
import site.hanschen.pretty.zhihu.ZhiHuApi;

/**
 * @author HansChen
 */
public class TaskService extends Service implements TaskManager {

    public static void bind(Context context, ServiceConnection conn) {
        Intent intent = new Intent(context, TaskService.class);
        context.bindService(intent, conn, BIND_AUTO_CREATE);
    }

    public static void unbind(Context context, ServiceConnection conn) {
        context.unbindService(conn);
    }

    public static final int MSG_HUNT_COMPLETE = 0;

    private Context            mContext;
    private PrettyRepository   mPrettyRepository;
    private ZhiHuApi           mApi;
    private ThreadPoolExecutor mExecutor;
    private Dispatcher         mDispatcher;
    private Handler            mMainHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mPrettyRepository = PrettyApplication.getInstance().getPrettyRepository();
        mApi = PrettyApplication.getInstance().getApi();
        mExecutor = new ThreadPoolExecutor(getThreadPoolSize(),
                                           getThreadPoolSize(),
                                           60,
                                           TimeUnit.MINUTES,
                                           new LinkedBlockingQueue<Runnable>());
        HandlerThread handlerThread = new HandlerThread("work");
        handlerThread.start();
        mMainHandler = new Handler(handlerThread.getLooper(), mMainCallback);
        mDispatcher = new Dispatcher(mPrettyRepository, mApi, mExecutor, mMainHandler);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new TaskBinder();
    }

    @Override
    public void startFetchPicture(final int questionId) {
        mDispatcher.dispatchAddTask(questionId);
    }

    private Handler.Callback mMainCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_HUNT_COMPLETE:
                    @SuppressWarnings("unchecked") SparseArray<List<String>> urls = (SparseArray<List<String>>) msg.obj;
                    for (int i = 0; i < urls.size(); i++) {
                        List<Picture> pictures = new ArrayList<>();
                        int questionId = urls.keyAt(i);
                        for (String url : urls.valueAt(i)) {
                            if (mPrettyRepository.getPicture(url, questionId) == null) {
                                Picture picture = new Picture(null, questionId, url);
                                mPrettyRepository.insertOrUpdate(picture);
                                pictures.add(picture);
                            }
                        }
                        if (pictures.size() > 0) {
                            EventBus.getDefault().post(new NewPictureEvent(questionId, pictures));
                        }
                    }
                    break;
            }
            return false;
        }
    };

    public class TaskBinder extends Binder {

        public TaskManager getPrettyManager() {
            return TaskService.this;
        }
    }

    private int getThreadPoolSize() {
        int cpu = Runtime.getRuntime().availableProcessors();
        return 2 * cpu + 1;
    }
}
