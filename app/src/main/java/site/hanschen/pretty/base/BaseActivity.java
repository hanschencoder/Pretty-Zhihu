package site.hanschen.pretty.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * @author HansChen
 */
public class BaseActivity extends AppCompatActivity {

    private MaterialDialog mWaitingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void showWaitingDialog(String title, String message) {
        dismissDialog();
        mWaitingDialog = new MaterialDialog.Builder(this).title(title)
                                                         .cancelable(false)
                                                         .canceledOnTouchOutside(false)
                                                         .progress(true, 0)
                                                         .progressIndeterminateStyle(true)
                                                         .content(message)
                                                         .build();
        mWaitingDialog.show();
    }

    protected void dismissDialog() {
        if (mWaitingDialog != null && mWaitingDialog.isShowing()) {
            mWaitingDialog.dismiss();
            mWaitingDialog = null;
        }
    }
}
