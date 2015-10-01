package com.khfire22gmail.riple.Tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.model.TrickleAdapter;
import com.khfire22gmail.riple.model.TrickleItem;

import java.util.ArrayList;
import java.util.List;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;


/**
 * Created by Kevin on 9/8/2015.
 */
public class TrickleTab extends Fragment /*implements WaveSwipeRefreshLayout.OnRefreshListener*/ {

    private ListView mListview;
    private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    List<TrickleItem> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        View view = inflater.inflate(R.layout.tab_trickle, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.trickle_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        /*initView();
        getData2();*/

        TrickleAdapter trickleAdapter;

//        list = getData();
        list = getData2();

        trickleAdapter = new TrickleAdapter(getActivity(), list);

        mRecyclerView.setAdapter(trickleAdapter);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return view;
    }

    /*private void initView() {
        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) mListview.findViewById(R.id.main_swipe);
        mWaveSwipeRefreshLayout.setColorSchemeColors(Color.WHITE, Color.WHITE);
        mWaveSwipeRefreshLayout.setOnRefreshListener(this);
        mWaveSwipeRefreshLayout.setWaveColor(0x00000000);
        mWaveSwipeRefreshLayout.setMaxDropHeight(1500);

        mListview = (ListView) mListview.findViewById(R.id.trickle_recycler_view);
    }*/

    /*private void refresh(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                mWaveSwipeRefreshLayout.setRefreshing(false);
            }
        }, 3000);
    }*/

    @Override
    public void onResume() {
        super.onResume();
    }

    /*@Override
    public void onRefresh() {
        refresh();
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static List<TrickleItem> getData() {
        List<TrickleItem> data = new ArrayList<>();

        return data;
    }

    public static List<TrickleItem> getData2() {

//        Idea Hardcode
        List<TrickleItem> data = new ArrayList<>();
        String[] trickleitem = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
        //String[] trickleprofilepic = {}
        String[] trickleusername= {"Kevin Hodges", "Arnold Swarzenegger", "Payton Manning","Kevin Hodges", "Arnold Swarzenegger", "Payton Manning","Kevin Hodges", "Arnold Swarzenegger", "Payton Manning"};
        String[] trickletime = {"Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m."};
        String[] trickletitle = {"Wash your neighbors car", "Volunteer at a local church", "Buy a strangers groceries", "Wash your neighbors car", "Volunteer at a local church", "Buy a strangers groceries", "Wash your neighbors car", "Volunteer at a local church", "Buy a strangers groceries"};
        String[] trickledescription = {"Surprise an elderly neighbor and wash their car", "Find a local church and volunteer", "While in the store, find someone in need.", "Surprise an elderly neighbor and wash their car", "Find a local church and volunteer", "While in the store, find someone in need.", "Surprise an elderly neighbor and wash their car", "Find a local church and volunteer", "While in the store, find someone in need."};
        Integer[] trickleriplecount = {34, 77, 18, 34, 77, 18, 34, 77, 18};
        Integer[] tricklecommentcount = {50, 15, 6, 50, 15, 6, 50, 15, 6};

        for (int i = 0; i < trickleitem.length; i++) {
            TrickleItem currentTrickleItem = new TrickleItem();

            //currentTrickleItem.setTrickleProfilePic(trickleprofilepic[i]);
            currentTrickleItem.setTrickleUserName(trickleusername[i]);
            currentTrickleItem.setTrickleTime(trickletime[i]);
            currentTrickleItem.setTrickleTitle(trickletitle[i]);
            currentTrickleItem.setTrickleDescription(trickledescription[i]);
            currentTrickleItem.setTrickleRipleCount(trickleriplecount[i]);
            currentTrickleItem.setTrickleCommentCount(tricklecommentcount[i]);


            data.add(currentTrickleItem);
        }
        return data;
    }


}


