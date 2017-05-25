package site.hanschen.pretty.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import site.hanschen.pretty.base.BaseActivity;
import site.hanschen.pretty.ui.question.QuestionActivity;

/**
 * @author HansChen
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(SplashActivity.this, QuestionActivity.class));
        finish();
    }
}
