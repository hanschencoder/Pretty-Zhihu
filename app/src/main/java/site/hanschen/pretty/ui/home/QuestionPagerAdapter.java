package site.hanschen.pretty.ui.home;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @author HansChen
 */
public class QuestionPagerAdapter extends FragmentPagerAdapter {

    private List<QuestionCategory> mCategories;

    public QuestionPagerAdapter(FragmentManager fm, List<QuestionCategory> categories) {
        super(fm);
        this.mCategories = categories;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mCategories != null ? mCategories.size() : 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (mCategories.get(position)) {
            case HISTORY:
                return "历史";
            case FAVORITES:
                return "收藏";
            case HOT:
                return "热门";
            default:
                throw new IllegalStateException("unknown category: " + mCategories.get(position));
        }
    }

    @Override
    public Fragment getItem(int position) {
        return QuestionFragment.newInstance(mCategories.get(position));
    }
}
