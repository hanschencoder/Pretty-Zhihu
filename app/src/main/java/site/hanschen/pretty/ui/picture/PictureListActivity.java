package site.hanschen.pretty.ui.picture;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import site.hanschen.pretty.R;
import site.hanschen.pretty.application.PrettyApplication;
import site.hanschen.pretty.base.BaseActivity;
import site.hanschen.pretty.db.bean.Picture;
import site.hanschen.pretty.db.bean.Question;
import site.hanschen.pretty.db.repository.PrettyRepository;
import site.hanschen.pretty.eventbus.NewPictureEvent;
import site.hanschen.pretty.zhihu.ZhiHuApi;
import site.hanschen.pretty.zhihu.bean.AnswerList;

/**
 * @author HansChen
 */
public class PictureListActivity extends BaseActivity {

    private static final String KEY_QUESTION_ID = "KEY_QUESTION_ID";
    private static final String KEY_TITLE       = "KEY_TITLE";

    public static void open(Context context, int questionId, String title) {
        Intent intent = new Intent(context, PictureListActivity.class);
        intent.putExtra(KEY_QUESTION_ID, questionId);
        intent.putExtra(KEY_TITLE, title);
        context.startActivity(intent);
    }

    @BindView(R.id.picture_list_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.picture_list_pictures)
    RecyclerView mPictureView;

    private PictureAdapter   mAdapter;
    private List<Picture>    mPictures;
    private PrettyRepository mPrettyRepository;
    private ZhiHuApi         mApi;
    private int              mQuestionId;
    private String           mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_list);
        ButterKnife.bind(PictureListActivity.this);
        mPrettyRepository = PrettyApplication.getInstance().getPrettyRepository();
        mApi = PrettyApplication.getInstance().getApi();
        parseData();
        initViews();
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_picture_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                showFetchDialog();
                break;
        }
        return true;
    }

    private void parseData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null
            || (mQuestionId = bundle.getInt(KEY_QUESTION_ID)) == 0
            || (mTitle = bundle.getString(KEY_TITLE)) == null) {
            throw new IllegalArgumentException("bundle must contain QuestionId");
        }
    }

    private int getPhotoSize(int column) {
        int margin = getResources().getDimensionPixelOffset(R.dimen.grid_margin);
        return getResources().getDisplayMetrics().widthPixels / column - 2 * margin;
    }

    private void initViews() {
        mToolbar.setTitle(mTitle);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mPictureView.setLayoutManager(new GridLayoutManager(this, 3));
        mPictureView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int margin = getResources().getDimensionPixelOffset(R.dimen.grid_margin);
                outRect.set(margin, margin, margin, margin);
            }
        });
        mAdapter = new PictureAdapter(this, getPhotoSize(3));
        mAdapter.setItemClickListener(mOnItemClickListener);
        mPictureView.setAdapter(mAdapter);
    }

    private PictureAdapter.OnItemClickListener mOnItemClickListener = new PictureAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position, Picture picture) {
            GalleryActivity.open(PictureListActivity.this, mQuestionId, position);
        }
    };

    private void initData() {
        Observable.create(new ObservableOnSubscribe<List<Picture>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Picture>> e) throws Exception {
                e.onNext(mPrettyRepository.getPictures(mQuestionId));
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<Picture>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<Picture> pictures) {
                mPictures = pictures;
                mAdapter.setData(mPictures);
                if (mPictures.size() <= 0) {
                    showFetchDialog();
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void showFetchDialog() {
        new MaterialDialog.Builder(this).title("打开话题")
                                        .content("抓取该话题下所有图片？请尽量使用Wi-Fi，土豪随意")
                                        .positiveText("抓取")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                fetchPictureList();
                                            }
                                        })
                                        .negativeText("取消")
                                        .build()
                                        .show();
    }

    private void fetchPictureList() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                Question question = mPrettyRepository.getQuestion(mQuestionId);
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
                return mApi.getAnswerList(mQuestionId, 10, offset);
            }
        }).observeOn(Schedulers.computation()).flatMap(new Function<AnswerList, ObservableSource<String>>() {
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
        }).flatMap(new Function<List<String>, ObservableSource<Picture>>() {
            @Override
            public ObservableSource<Picture> apply(final @NonNull List<String> urls) throws Exception {
                return Observable.create(new ObservableOnSubscribe<Picture>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<Picture> emitter) throws Exception {
                        for (String url : urls) {
                            if (mPrettyRepository.getPicture(url, mQuestionId) == null) {
                                Picture picture = new Picture(null, mQuestionId, url);
                                mPrettyRepository.insertOrUpdate(picture);
                                emitter.onNext(picture);
                            }
                        }
                        emitter.onComplete();
                    }
                });
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Picture>() {
            @Override
            public void onSubscribe(@NonNull Disposable disposable) {

            }

            @Override
            public void onNext(@NonNull Picture picture) {
                mPictures.add(picture);
                mAdapter.notifyDataSetChanged();
                EventBus.getDefault().post(new NewPictureEvent(picture));
            }

            @Override
            public void onError(@NonNull Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
}
