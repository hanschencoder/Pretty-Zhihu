package site.hanschen.pretty.ui.home;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
import site.hanschen.pretty.db.bean.Question;
import site.hanschen.pretty.db.repository.PrettyRepository;
import site.hanschen.pretty.eventbus.NewQuestionEvent;
import site.hanschen.pretty.zhihu.ZhiHuApi;

/**
 * @author HansChen
 */
public class QuestionFragment extends Fragment {

    private static String KEY_CATEGORY = "KEY_CATEGORY";

    public static QuestionFragment newInstance(QuestionCategory category) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.question_list)
    RecyclerView mRecyclerView;

    private QuestionListAdapter mAdapter;
    private QuestionCategory    mCategory;
    private List<Question>      mQuestion;
    private MaterialDialog      mWaitingDialog;

    private ZhiHuApi         mApi;
    private PrettyRepository mPrettyRepository;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null || !getArguments().containsKey(KEY_CATEGORY)) {
            throw new IllegalStateException("bundle must contain category info");
        }
        mCategory = (QuestionCategory) getArguments().getSerializable(KEY_CATEGORY);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_question_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mPrettyRepository = PrettyApplication.getInstance().getPrettyRepository();
        mApi = PrettyApplication.getInstance().getApi();
        initViews();
        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initData() {
        switch (mCategory) {
            case HISTORY:
                mQuestion = mPrettyRepository.getAllQuestion();
                break;
        }
        mAdapter.setData(mQuestion);
    }

    private void initViews() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new QuestionListAdapter(getActivity());
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
    }

    private QuestionListAdapter.OnItemClickListener mOnItemClickListener = new QuestionListAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(final Question question) {
            new MaterialDialog.Builder(getActivity()).title("打开话题")
                                                     .content(String.format("抓取该话题下所有图片？请尽量使用Wi-Fi，土豪随意",
                                                                            question.getTitle()))
                                                     .positiveText("抓取")
                                                     .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                         @Override
                                                         public void onClick(@NonNull MaterialDialog dialog,
                                                                             @NonNull DialogAction which) {
                                                         }
                                                     })
                                                     .negativeText("取消")
                                                     .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                         @Override
                                                         public void onClick(@NonNull MaterialDialog dialog,
                                                                             @NonNull DialogAction which) {
                                                             dialog.dismiss();
                                                         }
                                                     })
                                                     .build()
                                                     .show();
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewQuestionEvent event) {
        if (mCategory == QuestionCategory.HISTORY) {
            showNewQuestionDialog();
        }
    }

    private void showNewQuestionDialog() {
        new MaterialDialog.Builder(getActivity()).title("添加话题")
                                                 .content("请输入话题地址")
                                                 .input("地址",
                                                        "https://www.zhihu.com/question/",
                                                        false,
                                                        new MaterialDialog.InputCallback() {
                                                            @Override
                                                            public void onInput(@NonNull MaterialDialog dialog,
                                                                                CharSequence input) {
                                                                if (mApi.isUrlValid(input.toString())) {
                                                                    dialog.dismiss();
                                                                    fetchQuestionDetail(input.toString());
                                                                } else {
                                                                    //noinspection ConstantConditions
                                                                    dialog.getContentView().setText("请输入正确的地址");
                                                                    dialog.getContentView()
                                                                          .setTextColor(getResources().getColor(R.color.error));
                                                                }
                                                            }
                                                        })
                                                 .inputType(InputType.TYPE_CLASS_NUMBER)
                                                 .negativeText("取消")
                                                 .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                     @Override
                                                     public void onClick(@NonNull MaterialDialog dialog,
                                                                         @NonNull DialogAction which) {
                                                         dialog.dismiss();

                                                     }
                                                 })
                                                 .autoDismiss(false)
                                                 .build()
                                                 .show();
    }

    private void fetchQuestionDetail(final String url) {
        final int questionId = mApi.parseQuestionId(url);
        for (Question q : mQuestion) {
            if (q.getQuestionId() == questionId) {
                Toast.makeText(getActivity().getApplicationContext(), "话题已存在", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext(mApi.getHtml(questionId));
                emitter.onComplete();
            }
        })
                  .subscribeOn(Schedulers.io())
                  .observeOn(Schedulers.io())
                  .flatMap(new Function<String, ObservableSource<Question>>() {
                      @Override
                      public ObservableSource<Question> apply(final @NonNull String html) throws Exception {
                          return Observable.create(new ObservableOnSubscribe<Question>() {
                              @Override
                              public void subscribe(@NonNull ObservableEmitter<Question> emitter) throws Exception {
                                  int count = mApi.parseAnswerCount(html);
                                  Question question = new Question(null, questionId, mApi.parseQuestionTitle(html), count);
                                  mPrettyRepository.insertOrReplace(question);
                                  emitter.onNext(question);
                                  emitter.onComplete();
                              }
                          });
                      }
                  })
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new Observer<Question>() {
                      @Override
                      public void onSubscribe(@NonNull Disposable d) {
                          showWaitingDialog("请稍等", "正在获取话题...");
                      }

                      @Override
                      public void onNext(@NonNull Question question) {
                          dismissDialog();
                          mQuestion.add(question);
                          mAdapter.notifyDataSetChanged();
                          Toast.makeText(getActivity().getApplicationContext(), "话题已添加", Toast.LENGTH_SHORT).show();
                      }

                      @Override
                      public void onError(@NonNull Throwable e) {
                          dismissDialog();
                      }

                      @Override
                      public void onComplete() {

                      }
                  });
    }

    protected void showWaitingDialog(String title, String message) {
        dismissDialog();
        mWaitingDialog = new MaterialDialog.Builder(getActivity()).title(title)
                                                                  .cancelable(false)
                                                                  .canceledOnTouchOutside(false)
                                                                  .progress(true, 0)
                                                                  .progressIndeterminateStyle(true)
                                                                  .content(message)
                                                                  .build();
        mWaitingDialog.show();
    }

    protected void dismissDialog() {
        if (mWaitingDialog != null && mWaitingDialog.isShowing()) {
            mWaitingDialog.dismiss();
            mWaitingDialog = null;
        }
    }
}
