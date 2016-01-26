package com.khfire22.riple.ViewPagers;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.khfire22.riple.fragments.CommentFragment;
import com.khfire22.riple.fragments.CompletedFragment;

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
        Fragment fragment = null;
        if (position == 0) {
            fragment = CommentFragment.newInstance(position);
        } else if (position == 1) {
            fragment = CompletedFragment.newInstance(position);
        }

        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
