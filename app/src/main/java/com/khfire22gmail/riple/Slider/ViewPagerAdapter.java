package com.khfire22gmail.riple.slider;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.khfire22gmail.riple.tabs.FriendsTab;
import com.khfire22gmail.riple.tabs.DropsTab;
import com.khfire22gmail.riple.tabs.RipleTab;
import com.khfire22gmail.riple.tabs.TrickleTab;

/**
 * Created by Kevin on 9/8/2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter{

    // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    CharSequence Titles[];

    // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
    int NumbOfTabs;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {

        super(fm);
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        // if the position is 0 we are returning the First tab

        if (position == 0) {
            RipleTab ripleTab = new RipleTab();
            return ripleTab;
        }

        if (position == 1) {
            DropsTab dropsTab = new DropsTab();
            return dropsTab;
        }

        if (position == 2) {
            TrickleTab trickleTab = new TrickleTab();
            return trickleTab;
        }
        if (position == 3) {
            FriendsTab friendsTab = new FriendsTab();
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
