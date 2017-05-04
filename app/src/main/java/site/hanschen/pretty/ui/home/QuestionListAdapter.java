package site.hanschen.pretty.ui.home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import site.hanschen.pretty.R;
import site.hanschen.pretty.db.bean.Question;

/**
 * @author HansChen
 */
public class QuestionListAdapter extends RecyclerView.Adapter<QuestionListAdapter.ViewHolder> implements View.OnClickListener {

    private List<Question>        mQuestions;
    private Context               mContext;
    private LayoutInflater        mInflater;
    private OnItemClickListener   mOnItemClickListener;
    private ColorGenerator        mGenerator;
    private TextDrawable.IBuilder mBuilder;

    public QuestionListAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        mGenerator = ColorGenerator.MATERIAL;
        mBuilder = TextDrawable.builder().round();
    }

    public void setData(List<Question> questions) {
        this.mQuestions = questions;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = mInflater.inflate(R.layout.list_item_two_line_with_icon, parent, false);
        root.setOnClickListener(QuestionListAdapter.this);
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(position);

        Question question = mQuestions.get(0);
        String firstChar = question.getTitle().substring(0, 1);
        int color = mGenerator.getColor(question.getTitle());
        TextDrawable drawable = mBuilder.build(firstChar, color);
        holder.icon.setImageDrawable(drawable);

        holder.title.setText(question.getTitle());
        holder.detail.setText(String.format(Locale.getDefault(),
                                            "%d个回答, %d张照片",
                                            question.getAnswerCount(),
                                            question.getPictures().size()));
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

    public interface OnItemClickListener {

        void onItemClick(Question question);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_icon)
        ImageView icon;
        @BindView(R.id.item_primary_text)
        TextView  title;
        @BindView(R.id.item_secondary_text)
        TextView  detail;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
