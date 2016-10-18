package com.nurmemet.ratablayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.HORIZONTAL;

/**
 * Created by nurmemet on 2016/5/20.
 */
public class RaTabLayout extends FrameLayout {


    private PagerAdapter mAdapter;
    private ViewPager mViewPager;
    private BindView mBindView;
    private TabItemClickListener mOnItemClick;
    private int mDrawablePadding;
    private int mCurrentPosition = 0;
    private RaCanvas mRaCanvas;
    private LinearLayout mContainer;
    private List<ViewGroup> mList;


    public RaTabLayout(Context context) {
        this(context,null);
    }

    public RaTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);

    }

    public RaTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mList = new ArrayList<>();
        setWillNotDraw(false);
        mContainer = new LinearLayout(getContext());
        mContainer.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mContainer.setOrientation(HORIZONTAL);
        addView(mContainer);
        mRaCanvas = new RaCanvas(getContext(), new RaCanvas.ChildTest() {
            @Override
            public View getChildAt(int pos) {
                return mContainer.getChildAt(pos);
            }

            @Override
            public int getChildCount() {
                return mContainer.getChildCount();
            }
        });
        mRaCanvas.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(mRaCanvas);

        //setBackgroundColor(ContextCompat.getColor(getContext(), R.color.main_bg_gray));
    }

    public void setDrawablePadding(int padding) {
        this.mDrawablePadding = padding;
    }

    public void setViewPager(ViewPager viewPager, BindView bindView, TabItemClickListener onItemClick) {
        if (viewPager == null) {
            throw new IllegalStateException("viewpager=null");
        }
        this.mViewPager = viewPager;
        this.mAdapter = viewPager.getAdapter();
        this.mBindView = bindView;
        this.mOnItemClick = onItemClick;


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mList.get(mCurrentPosition).findViewWithTag("title").setSelected(false);
                mList.get(mCurrentPosition).findViewWithTag("title").setSelected(true);
                mRaCanvas.decorateAnimate(position, mCurrentPosition);
                mCurrentPosition = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        for (int i = 0; i < mAdapter.getCount(); i++) {

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            params.weight = 1;
            RelativeLayout container = new RelativeLayout(getContext());
            container.setLayoutParams(params);
            container.setClickable(true);
            container.setGravity(Gravity.CENTER);
            View v = getTab(i, mAdapter.getPageTitle(i).toString(), container);
            container.addView(v);
            //container.setBackgroundColor(Color.TRANSPARENT);
            mList.add(container);
            mContainer.addView(container);

        }

    }


    private View getTab(final int position, String title, RelativeLayout container) {
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
        params.leftMargin = mDrawablePadding;
        params.gravity = Gravity.CENTER;
        img.setLayoutParams(params);
        tab.addView(img);
        if (mBindView != null) {
            mBindView.OnBindView(container,text, img, position);
        }
        //
        container.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int oldPosition = mViewPager.getCurrentItem();
                if (mViewPager.getCurrentItem() != position) {
                    mViewPager.setCurrentItem(position, false);
                    if (mOnItemClick != null) {
                        mOnItemClick.OnItemClicked(text, img, position, oldPosition, true);
                    }

                } else {
                    if (mOnItemClick != null) {
                        mOnItemClick.OnItemClicked(text, img, position, oldPosition, false);
                    }
                }
            }
        });
        return tab;
    }


    public interface BindView {
        void OnBindView(RelativeLayout tabContainer,TextView tv, ImageView img, int position);
    }

    public interface TabItemClickListener {
        void OnItemClicked(TextView tv, ImageView img, int newPosition, int oldPosition, boolean state);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    /**
     * 三角形外接圆半径
     * @param radius
     */
    public void setTriangleRadius(float radius) {
        mRaCanvas.setTriangleRadius(radius);
    }

    /**
     * 装饰颜色
     * @param color
     */

    public void setDecorateColor(int color) {
        mRaCanvas.setDecorateColor(color);
    }


    public void setIsDecorateMode(boolean isDecorateMode) {
        mRaCanvas.setIsDecorateMode(isDecorateMode);
    }
    /**
     * 装饰类型
     *
     * @param mode
     */
    public void setDecorateMode(RaCanvas.DecorateMode mode) {
        mRaCanvas.setDecorateMode(mode);
    }


}
