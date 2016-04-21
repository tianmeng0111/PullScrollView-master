package com.example.tm.pullscrollview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    @InjectView(R.id.iv_scroll_header)
    ImageView ivHeader;
    @InjectView(R.id.pullScrollView)
    PullScrollView pullScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        initView();

    }

    private void initView() {
        pullScrollView.setHeaderView(ivHeader);
    }

    public void changeHeaderType(View view) {
        if (view.getTag() == null || (int)(view.getTag()) == 1) {
            pullScrollView.setHeaderType(2);
            ((Button) view).setText("scale");
            view.setTag(2);
        } else if ((int)(view.getTag()) == 2) {
            pullScrollView.setHeaderType(1);
            ((Button) view).setText("parallax");
            view.setTag(1);
        }
    }

}
