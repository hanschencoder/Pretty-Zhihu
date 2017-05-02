package site.hanschen.pretty;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import site.hanschen.pretty.zhihu.ZhihuApi;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(MainActivity.this);
        initViews();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ZhihuApi api = new ZhihuApi();
                try {
                    String html = api.getHtml("58565859");
                    int count = api.getAnswerCount(html);
                    Log.d("Hans", "count: " + count);

                    String answer = api.getAnswer("58565859");
                    Log.d("Hans", "answer: " + answer);


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initViews() {
        mToolbar.setTitle(R.string.app_name);
        setSupportActionBar(mToolbar);
    }
}
