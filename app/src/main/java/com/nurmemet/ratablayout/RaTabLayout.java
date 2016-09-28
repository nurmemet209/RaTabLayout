package com.nurmemet.ratablayout;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by nurmemet on 2016/5/20.
 */
public class RaTabLayout extends LinearLayout {

    private PagerAdapter adapter;
    private ViewPager viewPager;
    private BindView bindView;
    private CustomOnItemClick itemClick;
    private int drawablePadding;
    private int mCurrentPosition = 0;

    private int mX1;
    private int mX2;
    private boolean isDecorateMode = true;
    private int mLineHeight = 2;
    private int mArcRadius = 15;
    private int mInset = 35;
    private Path mPath;

    private Paint mPaint;

    public void setDecorateMode(boolean isDecorateMode) {
        this.isDecorateMode = isDecorateMode;
    }

    public RaTabLayout(Context context) {
        super(context);
    }

    public RaTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RaTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        this.setOrientation(HORIZONTAL);
        setWillNotDraw(false
        );
        //setBackgroundColor(ContextCompat.getColor(getContext(), R.color.main_bg_gray));
    }

    public void setDrawablePadding(int padding) {
        this.drawablePadding = padding;
    }

    public void setViewPager(ViewPager viewPager, BindView bindView, CustomOnItemClick onItemClick) {
        if (viewPager == null) {
            throw new IllegalStateException("viewpager=null");
        }
        this.viewPager = viewPager;
        this.adapter = viewPager.getAdapter();
        this.bindView = bindView;
        this.itemClick = onItemClick;


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getChildAt(mCurrentPosition).findViewWithTag("title").setSelected(false);
                getChildAt(position).findViewWithTag("title").setSelected(true);
                decorateAnimate(position, mCurrentPosition);
                mCurrentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        for (int i = 0; i < adapter.getCount(); i++) {

            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            params.weight = 1;
            RelativeLayout container = new RelativeLayout(getContext());
            container.setLayoutParams(params);
            container.setClickable(true);
            container.setGravity(Gravity.CENTER);
            View v = getTab(i, adapter.getPageTitle(i).toString(), container);
            container.addView(v);
            container.setBackgroundColor(Color.TRANSPARENT);
            addView(container);

        }

    }


    private View getTab(final int position, String title, View container) {
        LinearLayout tab = new LinearLayout(getContext());
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        param.addRule(RelativeLayout.CENTER_IN_PARENT);
        tab.setLayoutParams(param);
        final TextView text = new TextView(getContext());
        text.setText(title);
        text.setTag("title");
        text.setGravity(Gravity.CENTER);
        if (mCurrentPosition == position) {
            text.setSelected(true);
        }
        text.setBackgroundColor(Color.TRANSPARENT);
        tab.addView(text);
        tab.setBackgroundColor(Color.TRANSPARENT);
        final ImageView img = new ImageView(getContext());

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.leftMargin = drawablePadding;
        params.gravity = Gravity.CENTER;
        img.setLayoutParams(params);
        tab.addView(img);
        if (bindView != null) {
            bindView.OnBindView(text, img, position);
        }
        container.setBackgroundColor(Color.TRANSPARENT);
        container.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int oldPosition = viewPager.getCurrentItem();
                if (viewPager.getCurrentItem() != position) {
                    viewPager.setCurrentItem(position, false);
                    if (itemClick != null) {
                        itemClick.OnItemClicked(text, img, position, oldPosition, true);
                    }

                } else {
                    if (itemClick != null) {
                        itemClick.OnItemClicked(text, img, position, oldPosition, false);
                    }
                }
            }
        });
        return tab;
    }

    private void decorateAnimate(int newSelPos, int oldSelPos) {
        clearAnimation();
        final int width = getChildAt(newSelPos).getWidth();
        View view = getChildAt(oldSelPos);
        final int x1 = view.getLeft();
        int delta = 0;
        for (int i = Math.min(newSelPos, oldSelPos); i < Math.min(newSelPos, oldSelPos) + Math.abs(newSelPos - oldSelPos); i++) {
            delta += getChildAt(i).getWidth();
        }
        ValueAnimator animator = ValueAnimator.ofInt(x1, newSelPos > oldSelPos ? x1 + delta : x1 - delta);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final int value = (Integer) animation.getAnimatedValue();
                mX1 = value + mInset;
                mX2 = mX1 + width - 2 * mInset;
                invalidate();

            }
        });
        animator.start();
    }

    public interface BindView {
        void OnBindView(TextView tv, ImageView img, int position);
    }

    public interface CustomOnItemClick {
        void OnItemClicked(TextView tv, ImageView img, int newPosition, int oldPosition, boolean state);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isDecorateMode) {
            if (mPaint == null) {
                mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                mPaint.setColor(Color.RED);
                mPaint.setStrokeWidth(mLineHeight);
                mPaint.setStyle(Paint.Style.STROKE);
                mPath = new Path();

            }
            mPath.reset();
            mPath.moveTo(0, getHeight() - mLineHeight);
            mPath.lineTo(mX1 - mArcRadius, getHeight() - mLineHeight);
            mPath.moveTo(mX1 - mArcRadius, getHeight() - mLineHeight);

            mPath.addArc(new RectF(mX1 - 2 * mArcRadius, getHeight() - mLineHeight - 2 * mArcRadius, mX1, getHeight() - mLineHeight), 0, 90);
            mPath.moveTo(mX1, getHeight() - mLineHeight - mArcRadius);
            mPath.lineTo(mX1, mLineHeight + mArcRadius + mInset);
            mPath.addArc(new RectF(mX1, mLineHeight + mInset, mX1 + 2 * mArcRadius, mLineHeight + 2 * mArcRadius + mInset), 180, 90);

            mPath.moveTo(mX1 + mArcRadius, mLineHeight + mInset);
            mPath.lineTo(mX2 - mArcRadius, mLineHeight + mInset);
            mPath.addArc(new RectF(mX2 - 2 * mArcRadius, mLineHeight + mInset, mX2, mLineHeight + 2 * mArcRadius + mInset), 270, 90);

            mPath.moveTo(mX2, mLineHeight + mArcRadius + mInset);
            mPath.lineTo(mX2, getHeight() - mLineHeight - mArcRadius);
            mPath.addArc(new RectF(mX2, getHeight() - 2 * mArcRadius - mLineHeight, mX2 + 2 * mArcRadius, getHeight() - mLineHeight), 90, 90);

            mPath.moveTo(mX2 + mArcRadius, getHeight() - mLineHeight);
            mPath.lineTo(getWidth() - mLineHeight, getHeight() - mLineHeight);

            canvas.drawPath(mPath, mPaint);


        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mX1 == mX2) {
            if (getChildCount() > mCurrentPosition) {
                final View view = getChildAt(mCurrentPosition);
                mX1 = view.getLeft() + mLineHeight + mInset;
                mX2 = view.getWidth() + mX1 - 2 * mInset;

            }
        }
    }
}
