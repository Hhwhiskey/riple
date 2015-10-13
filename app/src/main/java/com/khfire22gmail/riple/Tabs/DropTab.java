package com.khfire22gmail.riple.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.model.DropAdapter;
import com.khfire22gmail.riple.model.DropItem;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Kevin on 9/8/2015.
 */
public class DropTab extends Fragment {

    private RecyclerView mRecyclerView;
    private List<DropItem> list;
    private DropAdapter dropAdapter;
    private Button button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_drop, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.drop_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

// TODO Allow other fragments to see Parse Queries
        list = TrickleTab.getItemsFromParse();
//        list = getDataFake();

//        dropAdapter = new DropAdapter(getActivity(), list, "drop");

        mRecyclerView.setAdapter(dropAdapter);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static List<DropItem> getData() {
        List<DropItem> data = new ArrayList<>();

        return data;
    }

    /*public static List<DropItem> getDataFake() {

//        Idea Hardcode
        List<DropItem> data = new ArrayList<>();
        String[] trickleitem = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
        //String[] trickleprofilepic = {}
        String[] username= {"Kevin Hodges", "Arnold Swarzenegger", "Payton Manning","Kevin Hodges", "Arnold Swarzenegger", "Payton Manning","Kevin Hodges", "Arnold Swarzenegger", "Payton Manning"};
        String[] createdAt = {"Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m."};
        String[] title = {"Wash your neighbors car", "Volunteer at a local church", "Buy a strangers groceries", "Wash your neighbors car", "Volunteer at a local church", "Buy a strangers groceries", "Wash your neighbors car", "Volunteer at a local church", "Buy a strangers groceries"};
        String[] description = {"Surprise an elderly neighbor and wash their car", "Find a local church and volunteer", "While in the store, find someone in need.", "Surprise an elderly neighbor and wash their car", "Find a local church and volunteer", "While in the store, find someone in need.", "Surprise an elderly neighbor and wash their car", "Find a local church and volunteer", "While in the store, find someone in need."};
        String[] ripleCount = {"34", "77", "18", "34", "77", "18", "34", "77", "18"};
        String[] commentCount = {"50", "15", "6", "50", "15", "6", "50", "15", "6"};

        for (int i = 0; i < trickleitem.length; i++) {
            DropItem currentParseItem = new DropItem();

            //currentParseItem.setTrickleProfilePic(trickleprofilepic[i]);
            currentParseItem.setUserName(username[i]);
            currentParseItem.setCreatedAt(createdAt[i]);
            currentParseItem.setTitle(title[i]);
            currentParseItem.setDescription(description[i]);
            currentParseItem.setRipleCount(ripleCount[i]);
            currentParseItem.setCommentCount(commentCount[i]);


            data.add(currentParseItem);
        }
        return data;
    }*/

}


