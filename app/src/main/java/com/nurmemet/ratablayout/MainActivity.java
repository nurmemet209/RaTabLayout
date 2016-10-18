package com.nurmemet.ratablayout;

import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Adapter mAdapter;
    private ViewPager mViewPager;
    private RaTabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mTabLayout = (RaTabLayout) findViewById(R.id.tab_layout);
        mTabLayout.setDecorateMode(RaCanvas.DecorateMode.MODE_2);
        mTabLayout.setIsDecorateMode(true);
        mTabLayout.setDecorateColor(Color.WHITE);
        mAdapter = new Adapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(mAdapter.getCount());
        mTabLayout.setViewPager(mViewPager, new RaTabLayout.BindView() {
            @Override
            public void OnBindView(RelativeLayout tabContainer, TextView tv, ImageView img, int position) {
                if (0 == position) {
                    tabContainer.setBackgroundColor(Color.RED);
                } else if (position == 1) {
                    tabContainer.setBackgroundColor(Color.BLACK);
                }else if (position==2){
                    tabContainer.setBackgroundColor(0xFF3300FF);
                }
                tv.setTextColor(0xFFFFFFFF);
            }
        }, null);


    }
}
