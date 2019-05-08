package com.qxtx.idea.ideadialogdemo;

import android.media.Image;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.qxtx.idea.ideadialog.dialog.IdeaDialog;
import com.qxtx.idea.ideadialog.listener.DefaultBtnClickListener;

public class MainActivity extends AppCompatActivity {
    private IdeaDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(() -> {
            dialog = new IdeaDialog.Builder(MainActivity.this)
                    .layoutId(R.layout.ideadialog_default)
                    .title("提示")
                    .message("我是对话框，来自IdeaDialog")
                    .positiveBtn("升级", new DefaultBtnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.replaceRootView(R.layout.ideadialog_img);
                            ((ImageView)dialog.getRootView()).setImageResource(R.mipmap.ic_launcher);
                        }
                        @Override
                        public boolean autoCancel() {
                            return false;
                        }
                    })
                    .negativeBtn("取消",null)
                    .create();

            dialog.show();
        }, 1000);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }
}
