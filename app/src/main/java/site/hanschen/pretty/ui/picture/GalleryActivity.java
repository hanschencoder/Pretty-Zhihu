package site.hanschen.pretty.ui.picture;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import site.hanschen.pretty.R;
import site.hanschen.pretty.application.PrettyApplication;
import site.hanschen.pretty.db.bean.Picture;
import site.hanschen.pretty.db.repository.PrettyRepository;
import site.hanschen.pretty.eventbus.NewPictureEvent;
import site.hanschen.pretty.widget.DepthPageTransformer;
import site.hanschen.pretty.widget.ViewPagerCatchException;

/**
 * @author HansChen
 */
public class GalleryActivity extends AppCompatActivity {

    private static final String KEY_SELECT      = "KEY_SELECT";
    private static final String KEY_QUESTION_ID = "KEY_QUESTION_ID";

    @BindView(R.id.gallery_content)
    ViewPagerCatchException mPager;

    @BindView(R.id.gallery_indicate)
    TextView mIndicate;

    private List<Picture>       mPictures;
    private GalleryPagerAdapter mPagerAdapter;
    private int                 mSelected;
    private int                 mQuestionId;
    private PrettyRepository    mPrettyRepository;


    public static void open(Context context, int questionId, int selected) {
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra(KEY_SELECT, selected);
        intent.putExtra(KEY_QUESTION_ID, questionId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.bind(this);
        mPrettyRepository = PrettyApplication.getInstance().getPrettyRepository();
        parseData();
        initViews();
        initData();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void parseData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null || (mQuestionId = bundle.getInt(KEY_QUESTION_ID)) == 0) {
            throw new IllegalArgumentException("bundle must contain QuestionId");
        }
        mSelected = bundle.getInt(KEY_SELECT);
    }


    private void initViews() {

        mPagerAdapter = new GalleryPagerAdapter(this) {
            @Override
            public int getItemPosition(Object object) {
                return PagerAdapter.POSITION_NONE;
            }
        };
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageTransformer(true, new DepthPageTransformer());
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mSelected = position;
                updateIndicate();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

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
                mPagerAdapter.setData(mPictures);
                mPager.setCurrentItem(mSelected);
                updateIndicate();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void updateIndicate() {
        mIndicate.setText(String.format(Locale.getDefault(), "%d/%d", mSelected, mPictures.size()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewPictureEvent event) {
        if (event.questionId != mQuestionId || event.pictures.size() <= 0) {
            return;
        }
        mPagerAdapter.notifyDataSetChanged();
        updateIndicate();
    }
}
