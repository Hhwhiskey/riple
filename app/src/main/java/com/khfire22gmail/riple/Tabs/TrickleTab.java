package com.khfire22gmail.riple.tabs;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.model.DropAdapter;
import com.khfire22gmail.riple.model.DropItem;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;


/**
 * Created by Kevin on 9/8/2015.
 */
// TODO WaveSwipeRefreshLayout
public class TrickleTab extends Fragment implements WaveSwipeRefreshLayout.OnRefreshListener {

    private ListView mListview;
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    private RelativeLayout relativeLayout;
    private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    List<DropItem> list;
    DropAdapter dropAdapter;
    private ParseUser currentUser;
    private String currentUserObject;
    private String currentUserName;
    private RecyclerView.ItemAnimator animator;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View view = inflater.inflate(R.layout.tab_trickle, container, false);

        initView();

//        Create recyclerView and set it to display list
        mRecyclerView = (RecyclerView) view.findViewById(R.id.trickle_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        list = getItemsFromParse();  //returns droplist
        dropAdapter = new DropAdapter(getActivity(), list, "trickle");
        mRecyclerView.setAdapter(dropAdapter);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setItemAnimator(animator);


/*//        This will show a popup window which will contain the activity_clicked_drop
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.activity_clicked_drop, null);

                popupWindow = new PopupWindow(container, 500, 500, true);
                popupWindow.showAtLocation(relativeLayout, Gravity.NO_GRAVITY, 500, 500);

                container.setOnTouchListener(new View.OnTouchListener() {
                     @Override
                     public boolean onTouch(View view, MotionEvent motionEvent) {
                         popupWindow.dismiss();

                         return true;
                     }
                 });
            }
        });*/

        return view;
    }

    private void initView() {
        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) mWaveSwipeRefreshLayout.findViewById(R.id.trickle_recycler_view);
        mWaveSwipeRefreshLayout.setColorSchemeColors(Color.WHITE, Color.WHITE);
        mWaveSwipeRefreshLayout.setOnRefreshListener(this);
        mWaveSwipeRefreshLayout.setWaveColor(0x00000000);
        mWaveSwipeRefreshLayout.setMaxDropHeight(1500);

        mRecyclerView = (RecyclerView) mWaveSwipeRefreshLayout.findViewById(R.id.trickle_recycler_view);
    }

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
        //mWaveSwipeRefreshLayout.setRefreshing(true);
        refresh();
        super.onResume();
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    private void refresh(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mWaveSwipeRefreshLayout.setRefreshing(false);
            }
        }, 3000);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            mWaveSwipeRefreshLayout.setRefreshing(true);
            refresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



     /*public static List<DropItem> getDataFake() {

//        Idea Hardcode
        List<DropItem> data = new ArrayList<>();
        String[] trickleitem = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
        String[] username = {"Kevin Hodges", "Arnold Swarzenegger", "Payton Manning", "Kevin Hodges", "Arnold Swarzenegger", "Payton Manning", "Kevin Hodges", "Arnold Swarzenegger", "Payton Manning"};
        String[] time = {"Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m."};
        String[] title = {"Wash your neighbors car", "Volunteer at a local church", "Buy a strangers groceries", "Wash your neighbors car", "Volunteer at a local church", "Buy a strangers groceries", "Wash your neighbors car", "Volunteer at a local church", "Buy a strangers groceries"};
        String[] description = {"Surprise an elderly neighbor and wash their car", "Find a local church and volunteer", "While in the store, find someone in need.", "Surprise an elderly neighbor and wash their car", "Find a local church and volunteer", "While in the store, find someone in need.", "Surprise an elderly neighbor and wash their car", "Find a local church and volunteer", "While in the store, find someone in need."};
        Integer[] ripleCount = {34, 77, 18, 34, 77, 18, 34, 77, 18};
        Integer[] commentCount = {50, 15, 6, 50, 15, 6, 50, 15, 6};

        for (int i = 0; i < trickleitem.length; i++) {
            DropItem currentParseItem = new DropItem();

            //currentParseItem.setTrickleProfilePic(trickleprofilepic[i]);
            currentParseItem.setUserName(username[i]);
            currentParseItem.setTime(time[i]);
            currentParseItem.setTitle(title[i]);
            currentParseItem.setDescription(description[i]);
            currentParseItem.setRipleCount(ripleCount[i]);
            currentParseItem.setCommentCount(commentCount[i]);


            data.add(currentParseItem);
        }
        return data;
    }*/

    public static List<DropItem> getItemsFromParse() {
        final List<DropItem> dropList = new ArrayList<>();

//        ParseUser currentUser = ParseUser.getCurrentUser();

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Drop");

                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {

                        if (e != null) {
                            Log.i("KEVIN", "error error");

                        } else {
                            for (int i = 0; i < list.size(); i++) {

                                DropItem dropItem = new DropItem(/*list.get(i)*/);

//                              dropItem.setFacebookId(list.get(i)getString("userProfilePictureView"));

//                              dropItem.setName(list.get(i)getString("author"));

//                                dropItem.setObjectId(list.get(i).getObjectId());

                                //Picture
                                dropItem.setFacebookId(list.get(i).getString("facebookId"));
//                              Name
                                dropItem.setAuthor(list.get(i).getString("name"));

                                //Creation Time
                                dropItem.setCreatedAt(list.get(i).getDate("createdAt"));

                                //Drop Title
                                dropItem.setTitle(list.get(i).getString("title"));
                                //Drop description
                                dropItem.setDescription(list.get(i).getString("description"));

                                //Riple Count
                                dropItem.setRipleCount(String.valueOf(list.get(i).getInt("ripleCount") + " Riples"));
                                //Comment Count
                                dropItem.setCommentCount(String.valueOf(list.get(i).getInt("commentCount") + " Comments"));

                                //Id that connects commenter to drop
//                              dropItem.setCommenter(list.get(i).getString("commenter"));

                                dropList.add(dropItem);
                            }

                            Log.i("KEVIN", "list size: " + list.size());
                        }

                    }
                });

        return dropList;
    }
}




/*
package com.khfire22gmail.riple.tabs;

        import android.content.Intent;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.support.v7.widget.DefaultItemAnimator;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ListView;
        import android.widget.PopupWindow;
        import android.widget.RelativeLayout;

        import com.khfire22gmail.riple.R;
        import com.khfire22gmail.riple.model.DropAdapter;
        import com.khfire22gmail.riple.model.DropItem;
        import com.parse.FindCallback;
        import com.parse.ParseException;
        import com.parse.ParseObject;
        import com.parse.ParseQuery;
        import com.parse.ParseUser;

        import java.util.ArrayList;
        import java.util.List;

        import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;


*/
/**
 * Created by Kevin on 9/8/2015.
 *//*

// TODO WaveSwipeRefreshLayout
public class TrickleTab extends Fragment */
/*implements WaveSwipeRefreshLayout.OnRefreshListener*//*
 {

    private ListView mListview;
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    private RelativeLayout relativeLayout;
    private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    List<DropItem> list;
    DropAdapter dropAdapter;
    private ParseUser currentUser;
    private String currentUserObject;
    private String currentUserName;
    private RecyclerView.ItemAnimator animator;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        */
/*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);*//*

        View view = inflater.inflate(R.layout.tab_trickle, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.trickle_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

//
        //list = getDataFake();   //returns data
        list = getItemsFromParse();  //returns droplist

        dropAdapter = new DropAdapter(getActivity(), list, "trickle");

        mRecyclerView.setAdapter(dropAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setItemAnimator(animator);



*/
/*//*
/        This will show a popup window which will contain the activity_clicked_drop
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.activity_clicked_drop, null);

                popupWindow = new PopupWindow(container, 500, 500, true);
                popupWindow.showAtLocation(relativeLayout, Gravity.NO_GRAVITY, 500, 500);

                container.setOnTouchListener(new View.OnTouchListener() {
                     @Override
                     public boolean onTouch(View view, MotionEvent motionEvent) {
                         popupWindow.dismiss();

                         return true;
                     }
                 });
            }
        });*//*


        */
/*initView();
        getData2();*//*


        return view;
    }

    */
/*private void initView() {
        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) mListview.findViewById(R.id.main_swipe);
        mWaveSwipeRefreshLayout.setColorSchemeColors(Color.WHITE, Color.WHITE);
        mWaveSwipeRefreshLayout.setOnRefreshListener(this);
        mWaveSwipeRefreshLayout.setWaveColor(0x00000000);
        mWaveSwipeRefreshLayout.setMaxDropHeight(1500);

        mListview = (ListView) mListview.findViewById(R.id.trickle_recycler_view);
    }*//*


    */
/*private void refresh(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                mWaveSwipeRefreshLayout.setRefreshing(false);
            }
        }, 3000);
    }*//*


    @Override
    public void onResume() {
        super.onResume();
    }

    */
/*@Override
    public void onRefresh() {
        refresh();
    }*//*


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }



     */
/*public static List<DropItem> getDataFake() {

//        Idea Hardcode
        List<DropItem> data = new ArrayList<>();
        String[] trickleitem = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
        String[] username = {"Kevin Hodges", "Arnold Swarzenegger", "Payton Manning", "Kevin Hodges", "Arnold Swarzenegger", "Payton Manning", "Kevin Hodges", "Arnold Swarzenegger", "Payton Manning"};
        String[] time = {"Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m.", "Thursday, October 1 @ 5:30 p.m."};
        String[] title = {"Wash your neighbors car", "Volunteer at a local church", "Buy a strangers groceries", "Wash your neighbors car", "Volunteer at a local church", "Buy a strangers groceries", "Wash your neighbors car", "Volunteer at a local church", "Buy a strangers groceries"};
        String[] description = {"Surprise an elderly neighbor and wash their car", "Find a local church and volunteer", "While in the store, find someone in need.", "Surprise an elderly neighbor and wash their car", "Find a local church and volunteer", "While in the store, find someone in need.", "Surprise an elderly neighbor and wash their car", "Find a local church and volunteer", "While in the store, find someone in need."};
        Integer[] ripleCount = {34, 77, 18, 34, 77, 18, 34, 77, 18};
        Integer[] commentCount = {50, 15, 6, 50, 15, 6, 50, 15, 6};

        for (int i = 0; i < trickleitem.length; i++) {
            DropItem currentParseItem = new DropItem();

            //currentParseItem.setTrickleProfilePic(trickleprofilepic[i]);
            currentParseItem.setUserName(username[i]);
            currentParseItem.setTime(time[i]);
            currentParseItem.setTitle(title[i]);
            currentParseItem.setDescription(description[i]);
            currentParseItem.setRipleCount(ripleCount[i]);
            currentParseItem.setCommentCount(commentCount[i]);


            data.add(currentParseItem);
        }
        return data;
    }*//*


    public static List<DropItem> getItemsFromParse() {
        final List<DropItem> dropList = new ArrayList<>();

//        ParseUser currentUser = ParseUser.getCurrentUser();

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Drop");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (e != null) {
                    Log.i("KEVIN", "error error");

                } else {
                    for (int i = 0; i < list.size(); i++) {

                        DropItem dropItem = new DropItem(*/
/*list.get(i)*//*
);

//                              dropItem.setFacebookId(list.get(i)getString("userProfilePictureView"));

//                              dropItem.setName(list.get(i)getString("author"));

//                                dropItem.setObjectId(list.get(i).getObjectId());

                        //Picture
                        dropItem.setFacebookId(list.get(i).getString("facebookId"));
//                              Name
                        dropItem.setAuthor(list.get(i).getString("name"));

                        //Creation Time
                        dropItem.setCreatedAt(list.get(i).getDate("createdAt"));

                        //Drop Title
                        dropItem.setTitle(list.get(i).getString("title"));
                        //Drop description
                        dropItem.setDescription(list.get(i).getString("description"));

                        //Riple Count
                        dropItem.setRipleCount(String.valueOf(list.get(i).getInt("ripleCount") + " Riples"));
                        //Comment Count
                        dropItem.setCommentCount(String.valueOf(list.get(i).getInt("commentCount") + " Comments"));

                        //Id that connects commenter to drop
//                              dropItem.setCommenter(list.get(i).getString("commenter"));

                        dropList.add(dropItem);
                    }

                    Log.i("KEVIN", "list size: " + list.size());
                }

            }
        });

        return dropList;
    }
}


*/





