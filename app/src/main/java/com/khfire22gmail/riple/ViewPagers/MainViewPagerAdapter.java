package com.khfire22gmail.riple.ViewPagers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.khfire22gmail.riple.fragments.FriendsTabFragment;
import com.khfire22gmail.riple.fragments.DropsTabFragment;
import com.khfire22gmail.riple.fragments.RipleTabFragment;
import com.khfire22gmail.riple.fragments.TrickleTabFragment;

/**
 * Created by Kevin on 9/8/2015.
 */
public class MainViewPagerAdapter extends FragmentStatePagerAdapter{

    // This will Store the Titles of the Tabs which are Going to be passed when MainViewPagerAdapter is created
    CharSequence Titles[];

    // Store the number of tabs, this will also be passed when the MainViewPagerAdapter is created
    int NumbOfTabs;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public MainViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {

        super(fm);
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        // if the position is 0 we are returning the first tab

        if (position == 0) {
            RipleTabFragment ripleTab = new RipleTabFragment();
            return ripleTab;
        }

        if (position == 1) {
            DropsTabFragment dropsTab = new DropsTabFragment();
            return dropsTab;
        }

        if (position == 2) {
            TrickleTabFragment trickleTab = new TrickleTabFragment();
            return trickleTab;
        }
        if (position == 3) {
            FriendsTabFragment friendsTab = new FriendsTabFragment();
            return friendsTab;
        }
        return null;
    }

    // This method return the titles for the Tabs in the Tab Strip
    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}
