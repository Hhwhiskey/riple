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
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;


/**
 * Created by Kevin on 9/8/2015.
 */
// TODO WaveSwipeRefreshLayout
public class TrickleTabFragment extends Fragment /*implements WaveSwipeRefreshLayout.OnRefreshListener*/ {

    public static final String TAG = TrickleTabFragment.class.getSimpleName();

    private ListView mListview;
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    private RelativeLayout relativeLayout;
    private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    private String currentUserObject;
    private String currentUserName;
    private RecyclerView.ItemAnimator animator;
    private RecyclerView mRecyclerView;
    public static DropAdapter mTrickleAdapter;
    final ArrayList <DropItem> hasRelationList = new ArrayList<>();
    public static final ArrayList <ParseObject> trickleObjectsList = new ArrayList<>();
    public static final ArrayList <DropItem> allDropsList  = new ArrayList<>();
    public static ArrayList<DropItem> trickleTabInteractionList;



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
        View view = inflater.inflate(R.layout.fragment_trickle_tab, container, false);





//        Create recyclerView and set it to display list
        mRecyclerView = (RecyclerView) view.findViewById(R.id.trickle_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Log.d("KEVIN", "loading items from Parse now");
//        loadHasRelationToFromParse();
//        loadAllDropsFromParse();


        //Query currentUser "hasRelationTo" and compare to all Drops, showing only no relation Drops.

        loadAllDropsFromParse();
//        filterDrops(hasRelationList, allDropsList);
//        updateRecyclerView(filterDrops(hasRelationList, allDropsList));




        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        mRecyclerView.setItemAnimator(animator);
        mRecyclerView.setItemAnimator(new SlideInUpAnimator());

        return view;
    }

   /* @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            //probably orientation change
            myData = (List<String>) savedInstanceState.getSerializable("list");
        } else {
            if (myData != null) {
                //returning from backstack, data is fine, do nothing
            } else {
                //newly created, compute data
                myData = computeData();
            }
        }


    }*/

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

    public void loadAllDropsFromParse() {

        final ParseQuery<ParseObject> dropQuery = ParseQuery.getQuery("Drop");

        dropQuery.orderByDescending("createdAt");

        dropQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (e != null) {
                    Log.d("KEVIN", "error error");

                } else {

                    allDropsList.clear();

                    for (int i = 0; i < list.size(); i++) {

                        //Collects Drop Objects
                        trickleObjectsList.add(list.get(i));

                        DropItem dropItemAll = new DropItem();

                        //DropObject
//                        dropItemAll.setDrop(list.get(i).getParseObject("objectId"));
                        //ObjectId
                        dropItemAll.setObjectId(list.get(i).getObjectId());
                        //Author id
                        dropItemAll.setAuthorId(list.get(i).getString("author"));
                        //Author name
                        dropItemAll.setAuthorName(list.get(i).getString("name"));
                        //Picture
                        dropItemAll.setFacebookId(list.get(i).getString("facebookId"));
                        //CreatedAt
                        dropItemAll.setCreatedAt(list.get(i).getCreatedAt());
                        //dropItem.createdAt = new SimpleDateFormat("EEE, MMM d yyyy @ hh 'o''clock' a").parse("date");
                        //Drop description
                        dropItemAll.setDescription(list.get(i).getString("description"));
                        //Riple Count
                        dropItemAll.setRipleCount(String.valueOf(list.get(i).getInt("ripleCount") + " Riples"));
                        //Comment Count
                        dropItemAll.setCommentCount(String.valueOf(list.get(i).getInt("commentCount") + " Comments"));

                        allDropsList.add(dropItemAll);


                    }
                }

                loadRelationDropsFromParse();
            }
        });

    }

    public void loadRelationDropsFromParse() {

        ParseUser user = ParseUser.getCurrentUser();

        ParseRelation relation = user.getRelation("hasRelationTo");

        final ParseQuery hasRelationQuery = relation.getQuery();

//        hasRelationToQuery.orderByDescending("createdAt");

        hasRelationQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (e != null) {
                    Log.d("KEVIN", "error error");

                } else {
                    for (int i = 0; i < list.size(); i++) {

                        DropItem dropItemRelation = new DropItem();

                        dropItemRelation.setObjectId(list.get(i).getObjectId());

                        hasRelationList.add(dropItemRelation);
                    }
                }
                filterDrops(hasRelationList, allDropsList);
            }
        });

    }



    public void filterDrops(ArrayList <DropItem> hasRelationList, ArrayList <DropItem> filteredDropList) {
        Iterator<DropItem> allDropsIterator = filteredDropList.iterator();


        while(allDropsIterator.hasNext()) {
            DropItem dropItemAll = allDropsIterator.next();

            for(DropItem dropItemRelation  : hasRelationList) {
                if(dropItemAll.getObjectId().equals(dropItemRelation.getObjectId())){
                    allDropsIterator.remove();


                }
            }
        }



        /*ArrayList <ParseObject> dropObjects = new ArrayList<>();

        for(int i = 0; i < filteredDropList.size(); i++) {
            dropObjects.set(filteredDropList.get(i));
        }*/
        trickleTabInteractionList = filteredDropList;
        updateRecyclerView(filteredDropList);
    }

    private void updateRecyclerView(ArrayList<DropItem> filteredDropList) {
        Log.d("KEVIN", "TRICKLE LIST SIZE: " + filteredDropList.size());

        mTrickleAdapter = new DropAdapter(getActivity(), filteredDropList, "trickle");
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(mTrickleAdapter);
        scaleAdapter.setDuration(250);
        mRecyclerView.setAdapter(new AlphaInAnimationAdapter(scaleAdapter));
        mRecyclerView.setItemAnimator(new SlideInLeftAnimator());
    }
}