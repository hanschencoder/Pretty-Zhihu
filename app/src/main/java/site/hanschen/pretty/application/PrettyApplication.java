package site.hanschen.pretty.application;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import site.hanschen.pretty.db.gen.DaoMaster;
import site.hanschen.pretty.db.gen.DaoSession;
import site.hanschen.pretty.db.repository.PrettyRepository;
import site.hanschen.pretty.db.repository.PrettyRepositoryImpl;
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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        sInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
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
}
