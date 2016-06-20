package com.example.tm.pullscrollview;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    @InjectView(R.id.iv_scroll_header)
    ImageView ivHeader;
    @InjectView(R.id.pullScrollView)
    PullScrollView pullScrollView;
    //actionbar
    @InjectView(R.id.layout_ab)
    RelativeLayout layoutAb;
    @InjectView(R.id.text_title)
    TextView textTitle;
    @InjectView(R.id.img_menu)
    ImageView imgMenu;

    private int abTopSpace;

    private int defaultAbColor = R.color.ab_item_back;
    private int abColor = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        initView();
        initAb();
        getVibrantSwatch();
    }

    private void initView() {
        pullScrollView.setHeaderView(ivHeader);
        pullScrollView.setOnScrollListener(new PullScrollView.OnScrollListener() {
            @Override
            public void onScroll(int l, int t, int oldl, int oldt) {
                int scrollY = pullScrollView.getScrollY();
                Log.e(TAG, "--------------->>" + scrollY);
                if (scrollY >= (300 - 80) && scrollY <= 420) {
                    float function = ((float) scrollY - 220) / 200;
                    if (abColor != 0)
                        layoutAb.setBackgroundColor(ColorUtil.getNewColorByStartEndColor(MainActivity.this,
                                function, R.color.transparent, abColor));
                    else
                        layoutAb.setBackgroundColor(ColorUtil.getNewColorByStartEndColor(MainActivity.this,
                                function, R.color.transparent, defaultAbColor));

                } else if (scrollY < (300 - 80)){
                    layoutAb.setBackgroundColor(Color.TRANSPARENT);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        textTitle.setBackground(getResources().getDrawable(R.drawable.shape_ab_item_back));
                        imgMenu.setBackground(getResources().getDrawable(R.drawable.shape_ab_item_back));
                    }
                } else {
                    if (abColor != 0)
                        layoutAb.setBackgroundColor(abColor);
                    else
                        layoutAb.setBackgroundColor(getColor(defaultAbColor));
                    textTitle.setBackgroundColor(Color.TRANSPARENT);
                    imgMenu.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });
    }

    public void changeHeaderType(View view) {
        if (view.getTag() == null || (int) (view.getTag()) == 1) {
            pullScrollView.setHeaderType(2);
            ((Button) view).setText("scale");
            view.setTag(2);
        } else if ((int) (view.getTag()) == 2) {
            pullScrollView.setHeaderType(1);
            ((Button) view).setText("parallax");
            view.setTag(1);
        }
    }

    private void initAb() {
        textTitle.setText("我的");
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);
            }
        });
    }

    public void getVibrantSwatch() {
        Drawable drawable = ivHeader.getDrawable();
        BitmapDrawable bd = (BitmapDrawable) drawable;
        Palette palette = Palette.generate(bd.getBitmap());
        Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
        abColor = vibrantSwatch.getRgb();
    }
}
