package com.khfire22gmail.riple.tabs;

import android.content.Intent;
import android.os.Bundle;
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
public class TrickleTab extends Fragment /*implements WaveSwipeRefreshLayout.OnRefreshListener*/ {

    private ListView mListview;
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    private RelativeLayout relativeLayout;
    private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    List<DropItem> list;
    DropAdapter trickleAdapter;
    private ParseUser currentUser;
    private String currentUserObject;
    private String currentUserName;
    private RecyclerView.ItemAnimator animator;

    /*public void onActivityCreated (Bundle savedInstanceState)

    Added in API level 11
    Called when the fragment's activity has been created and this fragment's view hierarchy instantiated. It can be used to do final initialization once these pieces are in place, such as retrieving views or restoring state. It is also useful for fragments that use setRetainInstance(boolean) to retain their instance, as this callback tells the fragment when it is fully associated with the new activity instance. This is called after onCreateView(LayoutInflater, ViewGroup, Bundle) and before onViewStateRestored(Bundle).

    Parameters
    savedInstanceState	If the fragment is being re-created from a previous saved state, this is the state.*/
    /*@Override
    public void onActivityCreated(Bundle savedInstanceState) {
    
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_trickle, container, false);

//        Create recyclerView and set it to display list
        mRecyclerView = (RecyclerView) view.findViewById(R.id.trickle_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        list = getItemsFromParse();
        trickleAdapter = new DropAdapter(getActivity(), list, "trickle");
        mRecyclerView.setAdapter(trickleAdapter);

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

    /*private void initView() {
        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) getActivity().findViewById(R.id.main_swipe);
        mWaveSwipeRefreshLayout.setColorSchemeColors(Color.WHITE, Color.WHITE);
        mWaveSwipeRefreshLayout.setOnRefreshListener(this);
        mWaveSwipeRefreshLayout.setWaveColor(0x00000000);
//        mWaveSwipeRefreshLayout.setMaxDropHeight(1500);

        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.trickle_recycler_view);
    }*/

    @Override
    public void onResume() {
       /* mWaveSwipeRefreshLayout.setRefreshing(true);
        refresh();*/
        super.onResume();
    }

    /*@Override
    public void onRefresh() {
        refresh();

    }*/

    /*private void refresh(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                *//*mWaveSwipeRefreshLayout.setRefreshing(false);
                getItemsFromParse();
                dropAdapter = new DropAdapter(getActivity(), list, "trickle");*//*
            }
        }, 3000);
    }*/

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
//            mWaveSwipeRefreshLayout.setRefreshing(true);
//            refresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static List<DropItem> getItemsFromParse() {
        final List<DropItem> dropList = new ArrayList<>();

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Drop");

                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {

                        if (e != null) {
                            Log.i("KEVIN", "error error");

                        } else {
                            for (int i = 0; i < list.size(); i++) {

                                DropItem dropItem = new DropItem();

                                //Picture
                                dropItem.setFacebookId(list.get(i).getString("facebookId"));
                                //Name
                                dropItem.setAuthor(list.get(i).getString("name"));

                                //Date
                                dropItem.setCreatedAt(list.get(i).getCreatedAt());

//                                dropItem.createdAt = new SimpleDateFormat("EEE, MMM d yyyy @ hh 'o''clock' a").parse("date");

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




