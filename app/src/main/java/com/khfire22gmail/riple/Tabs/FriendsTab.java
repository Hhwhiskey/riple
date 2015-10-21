package com.khfire22gmail.riple.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khfire22gmail.riple.R;

/**
 * Created by Kevin on 9/8/2015.
 */
public class FriendsTab extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_chat,container,false);
        return v;
    }
}
