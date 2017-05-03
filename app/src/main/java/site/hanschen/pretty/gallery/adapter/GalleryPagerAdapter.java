package site.hanschen.pretty.gallery.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.List;

import site.hanschen.pretty.R;


/**
 * @author HansChen
 */
public class GalleryPagerAdapter extends PagerAdapter {

    private List<String>   mData;
    private LayoutInflater mInflater;
    private Context        mContext;

    public GalleryPagerAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<String> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View layout = mInflater.inflate(R.layout.item_gallery_pager, view, false);
        assert layout != null;
        PhotoView imageView = (PhotoView) layout.findViewById(R.id.gallery_pager_image);
        final ProgressWheel progress = (ProgressWheel) layout.findViewById(R.id.gallery_pager_progress);

        progress.setVisibility(View.VISIBLE);
        Glide.with(mContext)
             .load(mData.get(position))
             .centerCrop()
             .placeholder(new ColorDrawable(Color.GRAY))
             .crossFade()
             .diskCacheStrategy(DiskCacheStrategy.ALL)
             .listener(new RequestListener<String, GlideDrawable>() {
                 @Override
                 public boolean onException(Exception e,
                                            String model,
                                            Target<GlideDrawable> target,
                                            boolean isFirstResource) {
                     progress.setVisibility(View.GONE);
                     return false;
                 }

                 @Override
                 public boolean onResourceReady(GlideDrawable resource,
                                                String model,
                                                Target<GlideDrawable> target,
                                                boolean isFromMemoryCache,
                                                boolean isFirstResource) {
                     progress.setVisibility(View.GONE);
                     return false;
                 }
             })
             .into(imageView);
        view.addView(layout, 0);
        return layout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
