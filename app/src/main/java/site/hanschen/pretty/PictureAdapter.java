package site.hanschen.pretty;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author HansChen
 */
public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder> {

    private Context      mContext;
    private List<String> mPictures;
    private int          mPhotoSize;

    public PictureAdapter(Context context, List<String> pictures, int photoSize) {
        this.mContext = context;
        this.mPictures = pictures;
        this.mPhotoSize = photoSize;
    }

    public void addPicture(String picture) {
        mPictures.add(picture);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_picture, parent, false);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = mPhotoSize;
        params.height = mPhotoSize;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(mContext)
             .load(mPictures.get(position))
             .centerCrop()
             .placeholder(new ColorDrawable(Color.GRAY))
             .crossFade()
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
}
