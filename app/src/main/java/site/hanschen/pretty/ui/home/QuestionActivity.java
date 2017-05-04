package site.hanschen.pretty.ui.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import site.hanschen.pretty.R;

/**
 * @author HansChen
 */
public class QuestionActivity extends AppCompatActivity {

    @BindView(R.id.question_tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.question_pager)
    ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.question_toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        mViewPager.setOffscreenPageLimit(2);
        QuestionCategory[] categories = new QuestionCategory[]{
                QuestionCategory.HISTORY, QuestionCategory.FAVORITES, QuestionCategory.HOT};
        mViewPager.setAdapter(new QuestionPagerAdapter(getFragmentManager(), Arrays.asList(categories)));
        mTabLayout.setupWithViewPager(mViewPager);
    }
}
