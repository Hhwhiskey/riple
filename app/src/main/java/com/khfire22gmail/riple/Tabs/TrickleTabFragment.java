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
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
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
import java.util.List;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;


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
    RecyclerView mRecyclerView;
    List<DropItem> mTrickleList;
    DropAdapter mTrickleAdapter;
    public static ArrayList<ParseObject> trickleObjectsList;
    ProfilePictureView picture;
    TextView usedText;
    private ArrayList trickleList;


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

        trickleObjectsList = new ArrayList<>();

        setRetainInstance(true);

//        Create recyclerView and set it to display list
        mRecyclerView = (RecyclerView) view.findViewById(R.id.trickle_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Log.d("KEVIN", "loading items from Parse now");
//        loadHasRelationToFromParse();
        loadAllDropsFromParse();


        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        mRecyclerView.setItemAnimator(animator);
        mRecyclerView.setItemAnimator(new SlideInUpAnimator());

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*if (savedInstanceState != null) {
            //probably orientation change
            myData = (List<String>) savedInstanceState.getSerializable("list");
        } else {
            if (myData != null) {
                //returning from backstack, data is fine, do nothing
            } else {
                //newly created, compute data
                myData = computeData();
            }
        }*/


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

    public void loadRelationDropsFromParse() {
        final ArrayList<DropItem> hasRelationDrops = new ArrayList<>();

        // We only want to show Drops that have no relation to the user.
        // In other words, if the user has ToDo'd, Created, or Completed
        // this drop, we DO NOT want to show it.

        // 1st: put together query for hasRelationTo
        ParseUser user = ParseUser.getCurrentUser();

        ParseRelation relation = user.getRelation("hasRelationTo");

        ParseQuery hasRelationToQuery = relation.getQuery();

//        hasRelationToQuery.orderByDescending("createdAt");

        hasRelationToQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                // 2nd: save the "hasRelationTo" list locally so we don't have
                // to query it again

                if (e != null) {
                    Log.d("KEVIN", "error error");

                } else {
                    for (int i = 0; i < list.size(); i++) {

                        Log.d("KEVIN", hasRelationDrops.get(i).getObjectId());

                        DropItem dropItemRelation = new DropItem();

                        dropItemRelation.setObjectId(list.get(i).getObjectId());

                        hasRelationDrops.add(dropItemRelation);
                    }
                }
            }
        });
    }
        // 3rd: For each Drop in this Trickle List, check locally (by using ArrayList.contains to see
        // if the Drop is contained in the "hasRelationTo" list

        // 4th: If the drop is NOT contained, then the user has not interacted with it, and therefore
        // it should be displayed in this Trickle List


    public void loadAllDropsFromParse() {

        final ArrayList<DropItem> allDropsList = new ArrayList<>();

        final ParseQuery<ParseObject> dropQuery = ParseQuery.getQuery("Drop");

        dropQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (e != null) {
                    Log.d("KEVIN", "error error");

                } else {
                    for (int i = 0; i < list.size(); i++) {

//                        Log.d("KEVIN", trickleList.get(i).getDescription());

                        //Collects Drop Objects
                        trickleObjectsList.add(list.get(i));

                        DropItem dropItemAll = new DropItem();

                        //ObjectId
                        dropItemAll.setObjectId(list.get(i).getObjectId());
                        //Author id
                        dropItemAll.setAuthorId(list.get(i).getString("author"));
                        //Author name
                        dropItemAll.setAuthorName(list.get(i).getString("name"));
                        //Picture
                        dropItemAll.setFacebookId(list.get(i).getString("facebookId"));
                        //Date
                        dropItemAll.setCreatedAt(list.get(i).getCreatedAt());

                        //dropItem.createdAt = new SimpleDateFormat("EEE, MMM d yyyy @ hh 'o''clock' a").parse("date");

                        //Drop description
                        dropItemAll.setDescription(list.get(i).getString("description"));

                        //Riple Count
                        dropItemAll.setRipleCount(String.valueOf(list.get(i).getInt("ripleCount") + " Riples"));

                        //Comment Count
                        dropItemAll.setCommentCount(String.valueOf(list.get(i).getInt("commentCount") + " Comments"));

                        //Id that connects commenterName to drop
                        //                              dropItem.setCommenterName(list.get(i).getString("commenterName"));

                        allDropsList.add(dropItemAll);
                    }

                    updateRecyclerView(allDropsList);
                }
            }
        });
    }

   /* public ArrayList FilterDropsBasedOnRelation(ArrayList hasRelationToList , ArrayList allDropsList){

        for(String dropItemRelation  : hasRelationToList) {

            for(String dropItemAll : allDropsList) {

                if(dropItemRelation.getObjectId().equals(dropItemAll.getObjectId())) {
                    allDropsList.remove(dropItemAll);
                }
            }
        }

        trickleList = allDropsList;

        return trickleList;
    }*/


    private void updateRecyclerView(List<DropItem> items) {
        Log.d("KEVIN", "TRICKLE LIST SIZE: " + items.size());

        mTrickleList = items;

        mTrickleAdapter = new DropAdapter(getActivity(), mTrickleList, "trickle");
        mRecyclerView.setAdapter(mTrickleAdapter);
    }


    //    Adds this Drop to your Drops list
    public void AddDropToYourList() {


        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseObject drop = new ParseObject("Drop");
        drop.put("todo", currentUser.getObjectId());

        /*drop.put("facebookId", currentUser.get("facebookId"));
        drop.put("name", currentUser.get("name"));
        drop.put("title", dropTitle);
        drop.put("description", dropDescription);*/

        drop.saveInBackground();
    }
}