package com.nurmemet.ratablayout;


import android.content.Context;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by nurmemet on 9/27/2016.
 */

public class Adapter extends FragmentPagerAdapter {

    private Context mContext;

    public Adapter(FragmentManager fm, Context mContext) {
        super(fm);
        this.mContext = mContext;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        switch (position) {
            case 0:
                frag = new TestFrag1();
                break;
            case 1:
                frag = new TestFrag2();
                break;
            case 2:
                frag = new TestFrag3();
                break;
        }
        return frag;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "fragment1";
                break;
            case 1:
                title = "fragment2";
                break;
            case 2:
                title = "fragment3";
                break;
        }
        return title;
    }
}
