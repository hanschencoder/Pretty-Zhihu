package site.hanschen.pretty.ui.question;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import site.hanschen.pretty.R;
import site.hanschen.pretty.db.bean.Question;
import site.hanschen.pretty.utils.ColorUtils;

/**
 * @author HansChen
 */
public class QuestionListAdapter extends RecyclerView.Adapter<QuestionListAdapter.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    private   List<Question>        mQuestions;
    private   Context               mContext;
    private   LayoutInflater        mInflater;
    private   OnItemClickListener   mOnItemClickListener;
    private   TextDrawable.IBuilder mBuilder;
    private   boolean               mEditMode;
    protected Set<Integer>          mSelections;

    public QuestionListAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        mBuilder = TextDrawable.builder().round();
    }

    public void setData(List<Question> questions) {
        this.mQuestions = questions;
        notifyDataSetChanged();
    }

    public void setItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void enterEditMode(Set<Integer> selections) {
        mEditMode = true;
        mSelections = selections;
        notifyDataSetChanged();
    }

    public void exitEditMode() {
        mEditMode = false;
        if (mSelections != null) {
            mSelections.clear();
            mSelections = null;
        }
        notifyDataSetChanged();
    }

    public boolean isEditMode() {
        return mEditMode;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = mInflater.inflate(R.layout.list_item_two_line_with_icon_and_check, parent, false);
        root.setOnClickListener(QuestionListAdapter.this);
        root.setOnLongClickListener(QuestionListAdapter.this);
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(position);

        Question question = mQuestions.get(position);
        String firstChar = question.getTitle().substring(0, 1);
        int color = ColorUtils.getColor(question.getTitle());
        TextDrawable drawable = mBuilder.build(firstChar, color);
        holder.icon.setImageDrawable(drawable);

        holder.title.setText(question.getTitle());
        holder.detail.setText(String.format(Locale.getDefault(),
                                            "%d个回答, 已抓取%d张照片",
                                            question.getAnswerCount(),
                                            question.getPictures().size()));

        if (mEditMode) {
            holder.checkBox.setVisibility(View.VISIBLE);
            if (mSelections.contains(question.hashCode())) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mQuestions == null ? 0 : mQuestions.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            Question question = mQuestions.get((Integer) v.getTag());
            mOnItemClickListener.onItemClick(question);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mOnItemClickListener != null) {
            Question question = mQuestions.get((Integer) v.getTag());
            mOnItemClickListener.onItemLongClick(question);
            return true;
        }
        return false;
    }

    public interface OnItemClickListener {

        void onItemClick(Question question);

        void onItemLongClick(Question question);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_icon)
        ImageView icon;
        @BindView(R.id.item_primary_text)
        TextView  title;
        @BindView(R.id.item_secondary_text)
        TextView  detail;
        @BindView(R.id.item_check)
        CheckBox  checkBox;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
