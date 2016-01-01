package com.khfire22gmail.riple.MainViewPager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.khfire22gmail.riple.fragments.CommentFragment;

/**
 * Created by Kevin on 12/31/2015.
 */
public class DropPagerAdapter extends FragmentPagerAdapter{

    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Comments", "Riples"};
    private Context context;

    public DropPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        return CommentFragment.newInstance(position + 1);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
