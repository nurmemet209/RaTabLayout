package com.nurmemet.ratablayout;


import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by nurmemet on 10/18/2016.
 */

 class RaCanvas extends View {

    public enum DecorateMode {
        MODE_1, MODE_2
    }

    private DecorateMode mDecorateMode = DecorateMode.MODE_1;
    private int mCurrentPosition = 0;
    private static final int DURATION = 400;
    private int mX1;
    private int mX2;
    private boolean isDecorateMode = true;
    private int mLineHeight = 2;
    private int mArcRadius = 15;
    private int mInset = 35;
    private Path mPath;

    private Paint mPaint;
    private TriangleDecorate mTriangleDecorate;
    private boolean mWithAnim = false;

    private int mDecorateColor = 0xFFFF0000;
    private ChildTest childTest;

    public RaCanvas(Context context,ChildTest childTest) {
        super(context);
        this.childTest=childTest;
        init();
    }

    public RaCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RaCanvas(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTriangleDecorate = new TriangleDecorate();
        setWillNotDraw(false);
        setBackgroundColor(Color.TRANSPARENT);
    }




    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isDecorateMode) {
            if (mDecorateMode == DecorateMode.MODE_1) {
                if (mPaint == null) {
                    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    mPaint.setColor(mDecorateColor);
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
            } else if (mDecorateMode == DecorateMode.MODE_2) {
                final float itemWidth = (1F) / childTest.getChildCount() * getMeasuredWidth();
                final float x = itemWidth / 2 + mCurrentPosition * itemWidth;
                final float y = getMeasuredHeight();
                mTriangleDecorate.draw(canvas, x, y);
            }


        }
    }


    public void decorateAnimate(int newSelPos, int oldSelPos) {
        clearAnimation();
        final int width = childTest.getChildAt(newSelPos).getWidth();
        View view = childTest.getChildAt(oldSelPos);
        final int x1 = view.getLeft();
        int delta = 0;
        for (int i = Math.min(newSelPos, oldSelPos); i < Math.min(newSelPos, oldSelPos) + Math.abs(newSelPos - oldSelPos); i++) {
            delta += childTest.getChildAt(i).getWidth();
        }
        if (mWithAnim) {
            ValueAnimator animator = ValueAnimator.ofInt(x1, newSelPos > oldSelPos ? x1 + delta : x1 - delta);
            animator.setDuration(DURATION);
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
        } else {
            mX1 = (newSelPos > oldSelPos ? x1 + delta : x1 - delta) + mInset;
            mX2 = mX1 + width - 2 * mInset;
            invalidate();
        }
        mCurrentPosition=newSelPos;

    }



    public interface ChildTest{
        View getChildAt(int pos);
        int getChildCount();
    }

    private static class TriangleDecorate {

        private Paint mPaint;
        private int mColor = 0xFFFFA500;
        private float mRadius = 20;
        private Path mPath;
        private PointF mFirstP = new PointF(), mSecondP = new PointF(), mThirdP = new PointF();

        void init() {
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(mColor);
            mPath = new Path();

        }

        public TriangleDecorate() {
            init();
        }

        void draw(Canvas canvas, float x, float y) {

            mPath.reset();

            mFirstP.x = x - mRadius;
            mFirstP.y = y;
            mSecondP.x = x;
            mSecondP.y = y - (float) (mRadius * Math.sqrt(3));
            mThirdP.x = x + mRadius;
            mThirdP.y = y;

            mPath.moveTo(mFirstP.x, mFirstP.y);
            mPath.lineTo(mSecondP.x, mSecondP.y);
            mPath.lineTo(mThirdP.x, mThirdP.y);

            canvas.drawPath(mPath, mPaint);

        }

        void setTriangleRadius(float radius) {
            mRadius = radius;
        }

        void setTriangleColor(int color) {
            mColor = color;
            mPaint.setColor(color);
        }

    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mX1 == mX2) {
            if (childTest.getChildCount() > mCurrentPosition) {
                final View view = childTest.getChildAt(mCurrentPosition);
                mX1 = view.getLeft() + mLineHeight + mInset;
                mX2 = view.getWidth() + mX1 - 2 * mInset;
            }
        }
    }

    /**
     * 三角形外接圆半径
     * @param radius
     */
    void setTriangleRadius(float radius) {
        mTriangleDecorate.setTriangleRadius(radius);
    }

    /**
     * 装饰颜色
     * @param color
     */
    void setDecorateColor(int color) {
        mDecorateColor=color;
        mTriangleDecorate.setTriangleColor(color);
    }

    /**
     * 有无装饰
     * @param isDecorateMode
     */
    public void setIsDecorateMode(boolean isDecorateMode){
        this.isDecorateMode=isDecorateMode;
    }

    /**
     * 装饰类型
     * @param mode
     */
    public void setDecorateMode(DecorateMode mode){
        mDecorateMode=mode;
    }

}
