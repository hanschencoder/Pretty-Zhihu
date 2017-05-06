package site.hanschen.pretty.application;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import com.squareup.leakcanary.LeakCanary;

import site.hanschen.pretty.db.gen.DaoMaster;
import site.hanschen.pretty.db.gen.DaoSession;
import site.hanschen.pretty.db.repository.PrettyRepository;
import site.hanschen.pretty.db.repository.PrettyRepositoryImpl;
import site.hanschen.pretty.service.PrettyManager;
import site.hanschen.pretty.service.PrettyService;
import site.hanschen.pretty.zhihu.ZhiHuApi;
import site.hanschen.pretty.zhihu.ZhiHuApiApiImpl;

/**
 * @author HansChen
 */
public class PrettyApplication extends Application {

    private static PrettyApplication sInstance;

    public static PrettyApplication getInstance() {
        return sInstance;
    }

    private PrettyRepository mPrettyRepository;
    private ZhiHuApi         mZhiHuApi;
    private PrettyManager    mPrettyManager;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        sInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
        bindPrettyService();
    }

    public PrettyRepository getPrettyRepository() {
        if (mPrettyRepository == null) {
            synchronized (this) {
                if (mPrettyRepository == null) {
                    DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(this, "pretty-db", null);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    DaoSession daoSession = new DaoMaster(db).newSession();
                    mPrettyRepository = new PrettyRepositoryImpl(daoSession.getPictureDao(), daoSession.getQuestionDao());
                }
            }
        }
        return mPrettyRepository;
    }

    public ZhiHuApi getApi() {
        if (mZhiHuApi == null) {
            synchronized (this) {
                if (mZhiHuApi == null) {
                    mZhiHuApi = new ZhiHuApiApiImpl();
                }
            }
        }
        return mZhiHuApi;
    }

    private final ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof PrettyService.PrettyBinder) {
                mPrettyManager = ((PrettyService.PrettyBinder) service).getPrettyManager();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPrettyManager = null;
        }
    };

    private void bindPrettyService() {
        PrettyService.bind(getApplicationContext(), mConn);
    }

    private void unbindPrettyService() {
        PrettyService.unbind(getApplicationContext(), mConn);
        mPrettyManager = null;
    }

    public PrettyManager getPrettyManager() {
        if (mPrettyManager == null) {
            throw new IllegalStateException("mPrettyManager is null now ");
        }
        return mPrettyManager;
    }
}
