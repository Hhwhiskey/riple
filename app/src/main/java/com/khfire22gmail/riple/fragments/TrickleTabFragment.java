package com.khfire22gmail.riple.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.model.DropAdapter;
import com.khfire22gmail.riple.model.DropItem;
import com.khfire22gmail.riple.utils.EndlessRecyclerViewOnScrollListener;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by Kevin on 9/8/2015.
 */

public class TrickleTabFragment extends Fragment {

    private static final String TAG = "TrickleTabFragment";

    //    public static final String TAG = TrickleTabFragment.class.getSimpleName();
    private ListView mListview;
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    private RelativeLayout relativeLayout;
    private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    private String currentUserObject;
    private String currentUserName;
    private RecyclerView.ItemAnimator animator;
    private RecyclerView mTrickleRecyclerView;
    public static DropAdapter mTrickleAdapter;
    final ArrayList <DropItem> hasRelationList = new ArrayList<>();
    public static final ArrayList <ParseObject> trickleObjectsList = new ArrayList<>();
    public static final ArrayList <DropItem> allDropsList  = new ArrayList<>();
    public static ArrayList<DropItem> trickleTabInteractionList;
    private TextView trickleEmptyView;
    private EndlessRecyclerViewOnScrollListener mEndlessListener;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trickle_tab, container, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        mTrickleRecyclerView = (RecyclerView) view.findViewById(R.id.trickle_recycler_view);
        mTrickleRecyclerView.setLayoutManager(layoutManager);
        mTrickleRecyclerView.setItemAnimator(new SlideInLeftAnimator(new AnticipateInterpolator(2f)));
        mTrickleRecyclerView.getItemAnimator().setRemoveDuration(500);

        trickleEmptyView = (TextView) view.findViewById(R.id.trickle_tab_empty_view);

        //Default query call
        LoadAllDropsTask loadAllDropsTask = new LoadAllDropsTask();
        loadAllDropsTask.execute();


        //Scroll Query
        mTrickleRecyclerView.addOnScrollListener(mEndlessListener = new EndlessRecyclerViewOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                LoadAllDropsTask loadAllDropsTask = new LoadAllDropsTask(current_page);
                loadAllDropsTask.execute();

            }
        });

        //Pull Query
        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) view.findViewById(R.id.trickle_swipe);
        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                // Load all drops and refresh
                LoadAllDropsTask loadAllDropsTask = new LoadAllDropsTask(true);
                loadAllDropsTask.execute();
            }
        });

        return view;
    }

    /*Get all of the currentUser hasRelation
     */

    //All Drops Async
    public class LoadAllDropsTask extends AsyncTask<Void, Void, ArrayList<DropItem>> {

        List<ParseObject> listFromParse;
        boolean refresh = false;
        int page = 0;

        //Default constructor for onCreate query
        public LoadAllDropsTask(){
        }

        //Page constuctor for onScroll query
        public LoadAllDropsTask(int page){
            Log.d(TAG, "currentPage = " + page);
            this.page = page;
        }

        //Refresh constructor for pull to refresh query
        public LoadAllDropsTask(boolean refresh) {
            this.refresh = refresh;
        }



        //Get all Drops from Parse in Async
        @Override
        protected ArrayList<DropItem> doInBackground(Void... params) {
            final ParseQuery<ParseObject> dropQuery = ParseQuery.getQuery("Drop");

            // Limit of Drops to get from Parse and the
            int queryLimit = 25;
            // Amount of Drops to skip, activated with the onScroll constructor
            int skipNumber = 0;
            // If called with onScroll constructor
            // do some logic to determine the
            // amount of Drops to skip
            if (page != 0) {
                int pageMultiplier = page - 1;
                skipNumber = pageMultiplier * queryLimit;
            // Otherwise, clear the list, because this is a default(refresh) query
            }else {
                allDropsList.clear();
            }

            dropQuery.orderByDescending("createdAt");
            dropQuery.include("authorPointer");
            // Set the query limit
            dropQuery.setLimit(queryLimit);
            // Set the skip amount with logic above
            dropQuery.setSkip(skipNumber);
            try {
                listFromParse = dropQuery.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < listFromParse.size(); i++) {

                final DropItem dropItemAll = new DropItem();

                //Drop Author Data/////////////////////////////////////////////////////////
                ParseObject authorData = (ParseObject) listFromParse.get(i).get("authorPointer");

                ParseFile parseProfilePicture = (ParseFile) authorData.get("parseProfilePicture");
                if (parseProfilePicture != null) {
                    parseProfilePicture.getDataInBackground(new GetDataCallback() {

                        @Override
                        public void done(byte[] data, ParseException e) {
                            if (e == null) {
                                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        Bitmap resized = Bitmap.createScaledBitmap(bmp, 100, 100, true);
                                dropItemAll.setParseProfilePicture(resized);
//                                if (bmp != null) {
//                                    bmp.recycle();
//                                }
                            }
                        }
                    });
                }

                //dropItemAll.setAuthorName(authorName);
                dropItemAll.setAuthorName((String) authorData.get("displayName"));
                //Author id
                dropItemAll.setAuthorId(authorData.getObjectId());
                //Author Rank
                dropItemAll.setAuthorRank(authorData.getString("userRank"));
                //Author Riple Count
                dropItemAll.setAuthorRipleCount(String.valueOf(authorData.getInt("userRipleCount")));

                //Drop Data///////////////////////////////////////////////////////////////
                //DropObjectId
                dropItemAll.setObjectId(listFromParse.get(i).getObjectId());
                //Drop description
                dropItemAll.setDescription(listFromParse.get(i).getString("description"));
                //CreatedAt
                dropItemAll.setCreatedAt(listFromParse.get(i).getCreatedAt());
                //Riple Count
                dropItemAll.setRipleCount(String.valueOf(listFromParse.get(i).getInt("ripleCount") + " Riples"));
                //Comment Count
                dropItemAll.setCommentCount(String.valueOf(listFromParse.get(i).getInt("commentCount") + " Comments"));


                allDropsList.add(dropItemAll);
            }

            Log.d(TAG, "allDropsList = " + allDropsList.size());
            return allDropsList;
        }

        @Override
        protected void onPostExecute(ArrayList<DropItem> dropItems) {

            //Run the onScroll query
            if (page != 0) {
                LoadRelationDropsTask nextTask = new LoadRelationDropsTask(page);
                nextTask.execute();
            }

            //Run the pull to refresh query
            if (refresh) {
                LoadRelationDropsTask nextTask = new LoadRelationDropsTask(true);
                nextTask.execute();
            }

            //Otherwise call the default constructor for the standard query
            if (page == 0 && !refresh) {
                LoadRelationDropsTask nextTask = new LoadRelationDropsTask();
                nextTask.execute();
            }
        }
    }

    //Relation Drops Async
    class LoadRelationDropsTask extends AsyncTask<Void, Void, ArrayList<DropItem>> {

        List<ParseObject> parseRelationDrops = new ArrayList<>();
        boolean refresh = false;
        int page = 0;

        //Default constuctor for onCreate and pull to refresh
        LoadRelationDropsTask() {
        }

        //Constructor for onScrollQuery
        public LoadRelationDropsTask(int page) {
            this.page = page;
        }

        //Constructor for pull to refresh query
        public LoadRelationDropsTask(boolean refresh) {
            this.refresh = refresh;

        }

        //Get all Drops the currentUser is related to from Parse
        @Override
        protected ArrayList<DropItem> doInBackground(Void... params) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            ParseRelation relation = currentUser.getRelation("hasRelationTo");

            final ParseQuery hasRelationQuery = relation.getQuery();
            try {
                parseRelationDrops = hasRelationQuery.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            hasRelationList.clear();

            //If relation Drops from Parse is not empty, set their objetIds and add them to the
            //hasRelationList
            if (!parseRelationDrops.isEmpty()) {

                for (int i = 0; i < parseRelationDrops.size(); i++) {
                    DropItem dropItemRelation = new DropItem();
                    dropItemRelation.setObjectId(parseRelationDrops.get(i).getObjectId());
                    hasRelationList.add(dropItemRelation);
                }

                asyncFilterDrops(hasRelationList, allDropsList);

            }
            Log.d(TAG, "HasRelationList = " + hasRelationList.size());
            return hasRelationList;

        }

        //Async to filter the Drops, removing the Drops that the currentUser has created or completed
        public void asyncFilterDrops(ArrayList<DropItem> mHasRelationList, ArrayList<DropItem> mAllDropsList){

            Iterator<DropItem> allDropsIterator = mAllDropsList.iterator();

            while(allDropsIterator.hasNext()) {
                DropItem dropItemAll = allDropsIterator.next();

                for(DropItem dropItemRelation  : mHasRelationList) {
                    if(dropItemAll.getObjectId().equals(dropItemRelation.getObjectId())){
                        allDropsIterator.remove();
                    }
                }
            }

            trickleTabInteractionList = allDropsList;
//            Log.d(TAG, "DisplayedList = " + trickleTabInteractionList.size());
        }

        //After Relation query finishes
        @Override
        protected void onPostExecute(ArrayList<DropItem> dropItems) {

            //If pull to refresh caused this chain, end the refresh and reset the listener
            if (refresh) {
                mWaveSwipeRefreshLayout.setRefreshing(false);
                mEndlessListener.reset(1, 0, true);
                Log.d(TAG, "Page after reset = " + page);
            }

            //If scroll caused this chain, notify data changed to show the new data
            if(page != 0) {
                Log.d(TAG, "onScrollSize = " + allDropsList.size());
                mTrickleAdapter.notifyDataSetChanged();

                //Otherwise update the recyclerView
            } else {
                Log.d(TAG, "Refresh size = " + allDropsList.size());
                updateRecyclerView(allDropsList);

            }
        }
    }

    //Default update RecyclerView method used when activity is created. Only shows 10 items until scrolled
    private void updateRecyclerView(ArrayList<DropItem> filteredDropList) {


        if (filteredDropList.isEmpty()) {
            mTrickleRecyclerView.setVisibility(View.GONE);
            trickleEmptyView.setVisibility(View.VISIBLE);
        }
        else {
            mTrickleRecyclerView.setVisibility(View.VISIBLE);
            trickleEmptyView.setVisibility(View.GONE);
        }

        //Alpha animation
        mTrickleAdapter = new DropAdapter(getActivity(), filteredDropList, "trickle");
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mTrickleAdapter);
        mTrickleRecyclerView.setAdapter(alphaAdapter);
        alphaAdapter.setDuration(1000);

        //Alpha and scale animation
//        mTrickleAdapter = new DropAdapter(getActivity(), filteredDropList, "trickle");
//        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(mTrickleAdapter);
//        mTrickleRecyclerView.setAdapter(new AlphaInAnimationAdapter(scaleAdapter));
//        scaleAdapter.setDuration(500);
    }


    public void loadSavedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean trickleTipBoolean = sharedPreferences.getBoolean("trickleTipBoolean", true);
        if (trickleTipBoolean) {
            trickleTip();
        }
    }

    public void savePreferences(String key, Boolean value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void trickleTip() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TrickleTabFragment.this.getActivity(), R.style.MyAlertDialogStyle);

        builder.setTitle("Trickle...");
        builder.setMessage("This is the Trickle. A steady flow of Drops where every Riple begins. " +
                "Add these Drops to your to-do list with the conveniently placed switch. " +
                "Touch the author or the Drop to get a better look at them, respectively. Please do" +
                " your part and report harassment, by touching the setting button seen on each Drop" +
                ". If someone posts something offensive here then report it so it can be taken care of promptly");

        builder.setNegativeButton("HIDE THIS TIP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                savePreferences("trickleTipBoolean", false);

            }
        });

        builder.setPositiveButton("KEEP THIS AROUND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    @Override
    public void onResume() {

        super.onResume();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}