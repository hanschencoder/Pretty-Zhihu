package site.hanschen.pretty.ui.picture;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import site.hanschen.pretty.R;
import site.hanschen.pretty.db.bean.Picture;
import site.hanschen.pretty.utils.ColorUtils;
import site.hanschen.pretty.utils.CommonUtils;

/**
 * @author HansChen
 */
public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder> implements View.OnClickListener {

    private Context             mContext;
    private List<Picture>       mPictures;
    private int                 mPhotoSize;
    private OnItemClickListener mOnItemClickListener;

    public PictureAdapter(Context context, int photoSize) {
        this.mContext = context;
        this.mPhotoSize = photoSize;
    }

    public PictureAdapter(Context context, List<Picture> pictures, int photoSize) {
        this.mContext = context;
        this.mPictures = pictures;
        this.mPhotoSize = photoSize;
    }

    public void setItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setData(List<Picture> pictures) {
        this.mPictures = pictures;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            Picture picture = mPictures.get((Integer) v.getTag());
            mOnItemClickListener.onItemClick((Integer) v.getTag(), picture);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_picture, parent, false);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = mPhotoSize;
        params.height = mPhotoSize;
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(position);

        Picture picture = mPictures.get(position);
        Glide.with(mContext)
             .load(CommonUtils.getSmallPicture(picture.getUrl()))
             .centerCrop()
             .placeholder(new ColorDrawable(ColorUtils.getColor(picture)))
             .crossFade()
             .diskCacheStrategy(DiskCacheStrategy.ALL)
             .into(holder.picture);
    }

    @Override
    public int getItemCount() {
        return mPictures == null ? 0 : mPictures.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_picture)
        ImageView picture;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnItemClickListener {

        void onItemClick(int position, Picture picture);
    }
}
