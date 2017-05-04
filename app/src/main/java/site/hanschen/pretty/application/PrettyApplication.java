package site.hanschen.pretty.application;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.async.AsyncSession;

import site.hanschen.pretty.db.gen.DaoMaster;
import site.hanschen.pretty.db.gen.DaoSession;
import site.hanschen.pretty.db.repository.PrettyRepository;
import site.hanschen.pretty.db.repository.PrettyRepositoryImpl;

/**
 * @author HansChen
 */
public class PrettyApplication extends Application {

    private static PrettyApplication sInstance;

    public static PrettyApplication getInstance() {
        return sInstance;
    }

    private PrettyRepository mPrettyRepository;

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
}
