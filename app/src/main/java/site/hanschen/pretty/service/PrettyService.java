package site.hanschen.pretty.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import site.hanschen.pretty.application.PrettyApplication;
import site.hanschen.pretty.db.bean.Picture;
import site.hanschen.pretty.db.bean.Question;
import site.hanschen.pretty.db.repository.PrettyRepository;
import site.hanschen.pretty.eventbus.NewPictureEvent;
import site.hanschen.pretty.zhihu.ZhiHuApi;
import site.hanschen.pretty.zhihu.bean.AnswerList;

/**
 * @author HansChen
 */
public class PrettyService extends Service implements PrettyManager {

    public static void bind(Context context, ServiceConnection conn) {
        Intent intent = new Intent(context, PrettyService.class);
        context.bindService(intent, conn, BIND_AUTO_CREATE);
    }

    public static void unbind(Context context, ServiceConnection conn) {
        context.unbindService(conn);
    }

    private Context          mContext;
    private PrettyRepository mPrettyRepository;
    private ZhiHuApi         mApi;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mPrettyRepository = PrettyApplication.getInstance().getPrettyRepository();
        mApi = PrettyApplication.getInstance().getApi();
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
        return new PrettyBinder();
    }

    @Override
    public void startFetchPicture(final int questionId) {
        Log.d("Hans", "startFetchPicture: " + questionId);
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                Question question = mPrettyRepository.getQuestion(questionId);
                if (question != null) {
                    for (int offset = 0; offset < question.getAnswerCount(); offset += 10) {
                        emitter.onNext(offset);
                    }
                }
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).map(new Function<Integer, AnswerList>() {
            @Override
            public AnswerList apply(@NonNull Integer offset) throws Exception {
                return mApi.getAnswerList(questionId, 10, offset);
            }
        }).flatMap(new Function<AnswerList, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull final AnswerList answerList) throws Exception {
                return Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Exception {
                        for (String answer : answerList.msg) {
                            emitter.onNext(answer);
                        }
                        emitter.onComplete();
                    }
                });
            }
        }).map(new Function<String, List<String>>() {
            @Override
            public List<String> apply(@NonNull String answer) throws Exception {
                return mApi.parsePictureList(answer);
            }
        }).flatMap(new Function<List<String>, ObservableSource<List<Picture>>>() {
            @Override
            public ObservableSource<List<Picture>> apply(final @NonNull List<String> urls) throws Exception {
                return Observable.create(new ObservableOnSubscribe<List<Picture>>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<List<Picture>> emitter) throws Exception {
                        List<Picture> pictures = new ArrayList<>();
                        for (String url : urls) {
                            if (mPrettyRepository.getPicture(url, questionId) == null) {
                                Picture picture = new Picture(null, questionId, url);
                                mPrettyRepository.insertOrUpdate(picture);
                                pictures.add(picture);
                            }
                        }
                        emitter.onNext(pictures);
                        emitter.onComplete();
                    }
                });
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<Picture>>() {
            @Override
            public void onSubscribe(@NonNull Disposable disposable) {

            }

            @Override
            public void onNext(@NonNull List<Picture> pictures) {
                if (pictures.size() > 0) {
                    EventBus.getDefault().post(new NewPictureEvent(pictures));
                }
            }

            @Override
            public void onError(@NonNull Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    public class PrettyBinder extends Binder {

        public PrettyManager getPrettyManager() {
            return PrettyService.this;
        }
    }
}
