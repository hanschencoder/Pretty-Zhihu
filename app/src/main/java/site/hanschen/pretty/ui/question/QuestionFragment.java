package site.hanschen.pretty.ui.question;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import site.hanschen.pretty.eventbus.EditModeChangedEvent;
import site.hanschen.pretty.eventbus.NewPictureEvent;
import site.hanschen.pretty.eventbus.NewQuestionEvent;
import site.hanschen.pretty.ui.picture.PictureListActivity;
import site.hanschen.pretty.widget.FragmentBackHandler;
import site.hanschen.pretty.zhihu.ZhiHuApi;

/**
 * @author HansChen
 */
public class QuestionFragment extends Fragment implements FragmentBackHandler {

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

    private   ZhiHuApi         mApi;
    private   PrettyRepository mPrettyRepository;
    protected Set<Integer>     mSelections;

    private Menu mMenu;

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
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_question_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mPrettyRepository = PrettyApplication.getInstance().getPrettyRepository();
        mApi = PrettyApplication.getInstance().getApi();
        mSelections = new HashSet<>();
        initViews();
        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onBackPressed() {
        if (mAdapter.isEditMode()) {
            exitEditMode();
            return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_question_fragment, menu);
        mMenu = menu;
        mMenu.findItem(R.id.select_all).setVisible(false);
        mMenu.findItem(R.id.un_select_all).setVisible(false);
        mMenu.findItem(R.id.delete).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.select_all:
                selectAll();
                break;
            case R.id.un_select_all:
                clearAll();
                break;
            case R.id.delete:
                showDeleteDialog();
                break;
        }
        return true;
    }

    private void initData() {
        switch (mCategory) {
            case HISTORY:
                Observable.create(new ObservableOnSubscribe<List<Question>>() {
                    @Override
                    public void subscribe(ObservableEmitter<List<Question>> e) throws Exception {
                        e.onNext(mPrettyRepository.getAllQuestion());
                        e.onComplete();
                    }
                })
                          .subscribeOn(Schedulers.io())
                          .observeOn(AndroidSchedulers.mainThread())
                          .subscribe(new Observer<List<Question>>() {
                              @Override
                              public void onSubscribe(Disposable d) {

                              }

                              @Override
                              public void onNext(List<Question> questions) {
                                  mQuestion = questions;
                                  mAdapter.setData(mQuestion);
                              }

                              @Override
                              public void onError(Throwable e) {

                              }

                              @Override
                              public void onComplete() {

                              }
                          });
                break;
        }

    }

    private void initViews() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new QuestionListAdapter(getActivity());
        mAdapter.setItemClickListener(mOnItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
    }

    private QuestionListAdapter.OnItemClickListener mOnItemClickListener = new QuestionListAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(final Question question) {
            if (mAdapter.isEditMode()) {
                if (mSelections.contains(question.hashCode())) {
                    unSelectQuestion(question);
                } else {
                    selectQuestion(question);
                }
                mAdapter.notifyDataSetChanged();
            } else {
                PictureListActivity.open(getActivity(), question.getQuestionId(), question.getTitle());
            }
        }

        @Override
        public void onItemLongClick(Question question) {
            if (!mAdapter.isEditMode()) {
                selectQuestion(question);
                enterEditMode(mSelections);
            }
        }
    };

    private void selectQuestion(Question question) {
        mSelections.add(question.hashCode());
        mAdapter.notifyDataSetChanged();
    }

    private void selectAll() {
        for (Question q : mQuestion) {
            mSelections.add(q.hashCode());
        }
        mAdapter.notifyDataSetChanged();
    }

    private void clearAll() {
        mSelections.clear();
        mAdapter.notifyDataSetChanged();
    }

    private void unSelectQuestion(Question question) {
        mSelections.remove(question.hashCode());
        mAdapter.notifyDataSetChanged();
    }

    private void enterEditMode(Set<Integer> selections) {
        mAdapter.enterEditMode(selections);
        mMenu.findItem(R.id.select_all).setVisible(true);
        mMenu.findItem(R.id.un_select_all).setVisible(true);
        mMenu.findItem(R.id.delete).setVisible(true);
        EventBus.getDefault().post(new EditModeChangedEvent(true));
    }

    private void exitEditMode() {
        clearAll();
        mAdapter.exitEditMode();
        mMenu.findItem(R.id.select_all).setVisible(false);
        mMenu.findItem(R.id.un_select_all).setVisible(false);
        mMenu.findItem(R.id.delete).setVisible(false);
        EventBus.getDefault().post(new EditModeChangedEvent(false));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewQuestionEvent event) {
        if (mCategory == QuestionCategory.HISTORY) {
            showNewQuestionDialog();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewPictureEvent event) {
        if (mCategory == QuestionCategory.HISTORY) {
            if (event.pictures.size() > 0) {
                for (Question q : mQuestion) {
                    if (q.getQuestionId() == event.questionId) {
                        q.getPictures().addAll(event.pictures);
                        break;
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private void showNewQuestionDialog() {
        new MaterialDialog.Builder(getActivity()).title("添加话题")
                                                 .content("请输入话题地址")
                                                 .input("地址",
                                                        "https://www.zhihu.com/question/37787176",
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
                                  String title = mApi.parseQuestionTitle(html);
                                  if (count == 0 && TextUtils.isEmpty(title)) {
                                      emitter.onError(new Exception());
                                  } else {
                                      Question question = new Question(null, questionId, title, count);
                                      mPrettyRepository.insertOrUpdate(question);
                                      emitter.onNext(question);
                                      emitter.onComplete();
                                  }
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
                          mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                          Toast.makeText(getActivity().getApplicationContext(), "话题已添加", Toast.LENGTH_SHORT).show();
                      }

                      @Override
                      public void onError(@NonNull Throwable e) {
                          Toast.makeText(getActivity().getApplicationContext(), "话题添加失败", Toast.LENGTH_SHORT).show();
                          dismissDialog();
                      }

                      @Override
                      public void onComplete() {

                      }
                  });
    }

    private void showDeleteDialog() {
        new MaterialDialog.Builder(getActivity()).title("删除话题")
                                                 .content("是否删除选中话题？")
                                                 .positiveText("删除")
                                                 .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                     @Override
                                                     public void onClick(@NonNull MaterialDialog dialog,
                                                                         @NonNull DialogAction which) {
                                                         Iterator<Question> iterator = mQuestion.iterator();
                                                         while (iterator.hasNext()) {
                                                             Question q = iterator.next();
                                                             if (mSelections.contains(q.hashCode())) {
                                                                 iterator.remove();
                                                                 mPrettyRepository.deleteQuestion(q.getQuestionId());
                                                             }
                                                         }
                                                         mAdapter.notifyDataSetChanged();
                                                         exitEditMode();
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
