package site.hanschen.pretty.ui.gallery.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import site.hanschen.pretty.R;
import site.hanschen.pretty.utils.CommonUtils;


/**
 * @author HansChen
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private int                 mSelected;
    private Context             mContext;
    private List<String>        mData;
    private OnItemClickListener mOnItemClickListener;

    public GalleryAdapter(Context context) {
        this.mContext = context;
    }

    public void setSelected(int selected) {
        mSelected = selected;
        notifyDataSetChanged();
    }

    public void setData(List<String> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_gallery_recycle_view, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Glide.with(mContext)
             .load(CommonUtils.getSmallPicture(mData.get(position)))
             .centerCrop()
             .placeholder(new ColorDrawable(Color.GRAY))
             .crossFade()
             .diskCacheStrategy(DiskCacheStrategy.ALL)
             .into(holder.mImg);
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, holder.getAdapterPosition());
                }
            });
        }
        if (mSelected == position) {
            holder.mSelected.setVisibility(View.VISIBLE);
        } else {
            holder.mSelected.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mImg;
        View      mSelected;

        ViewHolder(View view) {
            super(view);
            mImg = (ImageView) view.findViewById(R.id.item_gallery_recycle_view_icon);
            mSelected = view.findViewById(R.id.item_gallery_recycle_view_select_mark);
        }
    }

    public interface OnItemClickListener {

        void onItemClick(View view, int position);
    }
}
