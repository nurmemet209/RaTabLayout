package com.nurmemet.ratablayout;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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

        mAdapter=new Adapter(getSupportFragmentManager(),this);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(mAdapter.getCount());
        mTabLayout.setViewPager(mViewPager, null, null);


    }
}
