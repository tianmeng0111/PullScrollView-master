package com.example.tm.pullscrollview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2016/4/19.
 */
public class PullScrollView extends ScrollView {

    private static final String TAG = PullScrollView.class.getSimpleName();

    private Context context;
    //可以下拉的最大距离
    private float mPullDownMaxDest = 0;
    //下拉刷新时 header的效果(0:normal, 1:parallax, 2:scale)
    private int mHeaderType = 0;

    private float touchDownY;
    private float touchY;

    public enum State {
        Normal, Up, Down, Refresh;
    }

    private State state = State.Normal;

    private boolean isTop = true;

    private int scaledTouchSlop;

    //this的根布局，即getChildAt0
    private View contentView;
    //跟随下拉变化的控件（图片）
    private View mHeader;
    //根布局的Rect
    private Rect rectContent = new Rect();
    //mHeader 的Rect
    private Rect rectHeader = new Rect();
    //是否可以下拉滑动
    private boolean isMoving = true;
    //向下拉动阻尼系数
    private float SCROLL_RATIO = 0.4f;
    //放大距离的比例因数；经验证，放大比例为1.1~1.2
    private static final float SCALE_RATIO = 0.2f;
    //视差效果的移动距离
    private float mParallaxDest;
    //放大效果的左右上下放大距离
    private float mScaleDest;

    public PullScrollView(Context context) {
        super(context);
        init(context, null);
    }

    public PullScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.context = context;
        if (attrs != null) {
            TypedArray typedArray = this.context.obtainStyledAttributes(attrs, R.styleable.PullScrollView);
            mPullDownMaxDest = typedArray.getDimension(R.styleable.PullScrollView_PullDownMaxDest, 0);
            mHeaderType = typedArray.getInt(R.styleable.PullScrollView_HeaderType, 0);
            Log.e(TAG, "--------->>" + mHeaderType);
            typedArray.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        Log.e(TAG, "==========================onFinishInflate");
        if (getChildCount() > 0) {
            contentView = getChildAt(0);
        } else {
            contentView = new LinearLayout(context);
            contentView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        super.onFinishInflate();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (this.onScrollListener != null) {
            onScrollListener.onScroll(l, t, oldl, oldt);
        }
        if (getScrollY() == 0) {
//            Log.e(TAG, "------滑动到顶部");
            isTop = true;
        }else {
//            Log.e(TAG, "------不在顶部");
//            isTop = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                Log.e(TAG, "------------down");
                touchDownY = ev.getY();
                state = State.Normal;
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.e(TAG, "------------ move");

                touchY = ev.getY();
                float destY = touchY - touchDownY;

                if (destY > 0 ) {
                    state = State.Down;
                } else if (destY < 0 ) {
                    state = State.Up;
                }

                if (state == State.Up) {
                    destY = destY < 0 ? destY : 0;

                    isMoving = false;

                } else if (state == State.Down) {
                    isMoving = true;
                    destY = destY < 0 ? 0 : destY;
                }

                /*if (destY >= scaledTouchSlop) {
                    isMoving = true;
                } else {
                    isMoving = false;
                }*/

                if (isMoving) {
                    if (rectContent.isEmpty()) {
                        rectContent.set(contentView.getLeft(), contentView.getTop(),
                                contentView.getRight(), contentView.getBottom());
                    }
                    if (mHeader != null && rectHeader.isEmpty()){
                        rectHeader.set(mHeader.getLeft(), mHeader.getTop(),
                                mHeader.getRight(), mHeader.getBottom());
                    }
                    if (mHeader == null) {
                        contentView.layout(rectContent.left, (int) (rectContent.top + destY * SCROLL_RATIO),
                                rectContent.right, (int) (rectContent.bottom + destY * SCROLL_RATIO));
                    } else {
                        if (rectHeader.bottom >= mPullDownMaxDest) {
                            contentView.layout(rectContent.left, (int) (rectContent.top + destY * SCROLL_RATIO),
                                    rectContent.right, (int) (rectContent.bottom + destY * SCROLL_RATIO));
                        }
                    }
                    if (mHeader != null) {
                        switch (mHeaderType) {
                            case 1:
                                mParallaxDest = destY * SCROLL_RATIO * 0.5f;
                                mHeader.layout(rectHeader.left, rectHeader.top + (int)(destY * SCROLL_RATIO * 0.5f),
                                        rectHeader.right, (int)(rectHeader.bottom + destY * SCROLL_RATIO * 0.5f));
                                break;
                            case 2:
                                mScaleDest = destY * SCROLL_RATIO * SCALE_RATIO;
//                                float mScaleDestX = (SCALE_RATIO * (rectHeader.right - rectHeader.left) - (rectHeader.right - rectHeader.left))/2;
//                                float mScaleDestY = (SCALE_RATIO * (rectHeader.bottom - rectHeader.top) - (rectHeader.bottom - rectHeader.top))/2;
                                mHeader.layout( (int) (rectHeader.left - mScaleDest),
                                        (int) (rectHeader.top - mScaleDest),
                                        (int) (rectHeader.right + mScaleDest),
                                        (int) (rectHeader.bottom + mScaleDest) );
                                break;
                        }

                    }
                }

                break;
            case MotionEvent.ACTION_UP:
//                Log.e(TAG, "------------up");
                if (!rectContent.isEmpty() && isMoving) {
                    rollBackAnim();
                }
                state = State.Normal;
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void rollBackAnim() {
        //contentView 回弹
        TranslateAnimation ta = new TranslateAnimation(0, 0, contentView.getTop(), rectContent.top);
        ta.setDuration(200);
        contentView.startAnimation(ta);
        contentView.layout(rectContent.left, rectContent.top, rectContent.right, rectContent.bottom);
        rectContent.setEmpty();
        //回弹  header的动画
        TranslateAnimation taHeader = new TranslateAnimation(0, 0, mParallaxDest, 0);
        taHeader.setDuration(200);

        final float scaleFactorX = (mScaleDest * 2 + rectHeader.right - rectHeader.left) / (rectHeader.right - rectHeader.left);
        final float scaleFactorY = (mScaleDest * 2 + rectHeader.bottom - rectHeader.top) / (rectHeader.bottom - rectHeader.top);
        ScaleAnimation saHeader = new ScaleAnimation(scaleFactorX, 1, scaleFactorY, 1,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        saHeader.setDuration(200);

        if (mHeader != null) {
            switch (mHeaderType) {
                case 1:
                    mHeader.startAnimation(taHeader);
                    break;
                case 2:
                    mHeader.startAnimation(saHeader);
                    break;
            }
            mHeader.layout(rectHeader.left, rectHeader.top, rectHeader.right, rectHeader.bottom);
            rectHeader.setEmpty();
        }

    }


    public void setHeaderView(View view) {
        mHeader = view;
    }

    public void setPullDownMaxDest(float dest) {
        mPullDownMaxDest = dest;
    }

    public void setHeaderType(int headerType) {
        mHeaderType = headerType;
    }

    /**
     * 更改向下拖动的阻尼系数，阻尼系数越小，拉动越难。
     * @param ratio
     */
    public void setScrollRatio(float ratio) {
        SCROLL_RATIO = ratio;
    }

    /**
     * 外部调用 scroll变化 监听
     */
    public interface OnScrollListener {
        public void onScroll(int l, int t, int oldl, int oldt);
    }
    private OnScrollListener onScrollListener;

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }
}
