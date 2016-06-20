package com.example.tm.pullscrollview;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class Main2Activity extends AppCompatActivity {

    private static final String TAG = Main2Activity.class.getSimpleName();

    @InjectView(R.id.iv_scroll_header)
    ImageView ivScrollHeader;
    @InjectView(R.id.iv_avatar)
    ImageView ivAvatar;
    @InjectView(R.id.pullScrollView)
    PullScrollView pullScrollView;
    //向上滑动和scrollview一起滚动的
    @InjectView(R.id.iv_scroll_header_real)
    ImageView ivScrollHeaderReal;

    private int scrollY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.inject(this);
        initView();
    }

    private void initView() {
        pullScrollView.setHeaderView(ivScrollHeader);
        pullScrollView.setHeaderType(2);
        pullScrollView.setScrollRatio(0.25f);
        final DisplayMetrics metrics = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);

        Rect rect = new Rect();
        ivScrollHeader.getWindowVisibleDisplayFrame(rect);

        pullScrollView.setOnScrollListener(new PullScrollView.OnScrollListener() {
            @Override
            public void onScroll(int l, int t, int oldl, int oldt) {
                scrollY = pullScrollView.getScrollY();
                Log.e(TAG, "scrollY-------------->>" + scrollY);
                if (scrollY > 0) {
//                    ivScrollHeader.setTop(-scrollY);
                    ivScrollHeaderReal.setVisibility(View.VISIBLE);
                } else if (scrollY <= 0) {
//                    ivScrollHeader.setTop(-scrollY);
                    ivScrollHeaderReal.setVisibility(View.GONE);
                }
            }
        });
    }
}
