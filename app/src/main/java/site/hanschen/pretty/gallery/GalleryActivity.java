package site.hanschen.pretty.gallery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import site.hanschen.pretty.R;
import site.hanschen.pretty.gallery.adapter.GalleryAdapter;
import site.hanschen.pretty.gallery.adapter.GalleryPagerAdapter;
import site.hanschen.pretty.widget.DepthPageTransformer;
import site.hanschen.pretty.widget.ViewPagerCatchException;

/**
 * @author HansChen
 */
public class GalleryActivity extends AppCompatActivity {

    private static final String KEY_SELECT = "KEY_SELECT";
    private static final String KEY_IMAGES = "KEY_IMAGES";

    @BindView(R.id.gallery_content)
    ViewPagerCatchException mPager;

    @BindView(R.id.gallery_horizontal_list)
    GalleryRecyclerView mRecyclerView;

    private List<String>        mImages;
    private GalleryAdapter      mListAdapter;
    private GalleryPagerAdapter mPagerAdapter;
    private int                 mSelected;


    public static void startup(Context context, ArrayList<String> list, int selected) {
        if (list == null) {
            throw new IllegalArgumentException("list can't be null");
        }

        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra(KEY_SELECT, selected);
        intent.putStringArrayListExtra(KEY_IMAGES, list);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.bind(this);
        initViews();
        initData();
    }


    private void initViews() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mListAdapter = new GalleryAdapter(this);
        mRecyclerView.setAdapter(mListAdapter);

        mListAdapter.setOnItemClickListener(new GalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                setSelected(position);
            }
        });

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
                setSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initData() {
        mImages = getIntent().getStringArrayListExtra(KEY_IMAGES);
        mListAdapter.setData(mImages);
        mPagerAdapter.setData(mImages);

        setSelected(getIntent().getIntExtra(KEY_SELECT, 0));
    }

    private void setSelected(int selected) {
        mSelected = selected;
        mListAdapter.setSelected(selected);
        mRecyclerView.smoothScrollToPosition(selected);

        mPagerAdapter.notifyDataSetChanged();
        mPager.setCurrentItem(selected);
    }

    @OnClick({R.id.gallery_delete_btn, R.id.gallery_share_btn, R.id.gallery_back_btn})
    public void onBtnClick(View v) {
        switch (v.getId()) {
            case R.id.gallery_back_btn:
                onBackPressed();
                break;
            case R.id.gallery_delete_btn:
//                showDeleteConfirmDialog();
                break;
            case R.id.gallery_share_btn:
                shareCurrentImage();
                break;
        }
    }

    private void shareCurrentImage() {
//        ShareUtils.shareSingleFile(this, mImages.get(mSelected), getResources().getString(R.string.share));
    }
}
