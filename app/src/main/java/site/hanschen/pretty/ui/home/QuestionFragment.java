package site.hanschen.pretty.ui.home;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import site.hanschen.pretty.R;
import site.hanschen.pretty.application.PrettyApplication;
import site.hanschen.pretty.db.bean.Question;
import site.hanschen.pretty.db.repository.PrettyRepository;

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

    @BindView(R.id.device_list_devices)
    RecyclerView mRecyclerView;

    private QuestionListAdapter mAdapter;
    private QuestionCategory    mCategory;
    private PrettyRepository    mPrettyRepository;
    private List<Question>      mQuestion;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null || !getArguments().containsKey(KEY_CATEGORY)) {
            throw new IllegalStateException("bundle must contain category info");
        }
        mCategory = (QuestionCategory) getArguments().getSerializable(KEY_CATEGORY);
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
        initViews();
        initData();
    }

    private void initData() {
        mPrettyRepository = PrettyApplication.getInstance().getPrettyRepository();
        mQuestion = mPrettyRepository.getAllQuestion();
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
            new MaterialDialog.Builder(getActivity()).title("问题")
                                                     .content(String.format("抓取该问题[%s]下所有图片？?", question.getTitle()))
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
}
