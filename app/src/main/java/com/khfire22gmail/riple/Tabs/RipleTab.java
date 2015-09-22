package com.khfire22gmail.riple.Tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khfire22gmail.riple.R;

/**
 * Created by Kevin on 9/8/2015.
 */
public class RipleTab extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.tab_riple,container,false);
        return view;
    }
}
