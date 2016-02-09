package com.khfire22.riple.fragments;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.khfire22.riple.R;
import com.khfire22.riple.model.DropAdapter;
import com.khfire22.riple.model.DropItem;
import com.khfire22.riple.utils.ConnectionDetector;
import com.khfire22.riple.utils.EndlessRecyclerViewOnScrollListener;
import com.khfire22.riple.utils.SaveToSharedPrefs;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.text.Format;
import java.text.SimpleDateFormat;
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
    private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    private RecyclerView mTrickleRecyclerView;
    public static DropAdapter mTrickleAdapter;
    ArrayList <DropItem> hasRelationList;
    public static ArrayList <DropItem> allDropsList;
    public static ArrayList<DropItem> trickleTabInteractionList;
    private TextView trickleEmptyView;
    private EndlessRecyclerViewOnScrollListener mEndlessListener;
    ParseUser currentUser;
    private ConnectionDetector detector;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trickle_tab, container, false);

        detector = new ConnectionDetector(getActivity());

        //Get currentUser
        currentUser = ParseUser.getCurrentUser();
        //Create a field arrayList for allDrops from Parse
        allDropsList = new ArrayList<>();
        //Create a field arrayList for hasRelationDrops from Parse
        hasRelationList = new ArrayList<>();

        //Declare layout manager, recyclerView, animator and emptyTextView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mTrickleRecyclerView = (RecyclerView) view.findViewById(R.id.trickle_recycler_view);
        mTrickleRecyclerView.setLayoutManager(layoutManager);
        mTrickleRecyclerView.setItemAnimator(new SlideInLeftAnimator(new AnticipateInterpolator(2f)));
        mTrickleRecyclerView.getItemAnimator().setRemoveDuration(500);
        //This is the emptyTextView that will be displayed if the data for the RV is empty
        trickleEmptyView = (TextView) view.findViewById(R.id.trickle_tab_empty_view);


        if (!detector.isConnectedToInternet()) {
            Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_LONG).show();
        } else {
            //Default query call
            LoadAllDropsTask loadAllDropsTask = new LoadAllDropsTask();
            loadAllDropsTask.execute();
        }

        //Scroll Query listener
        mTrickleRecyclerView.addOnScrollListener(mEndlessListener = new EndlessRecyclerViewOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {

                if (!detector.isConnectedToInternet()) {
                    Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                } else {
                    LoadAllDropsTask loadAllDropsTask = new LoadAllDropsTask(current_page);
                    loadAllDropsTask.execute();
                }
            }
        });

        //Pull to refresh Query listener
        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) view.findViewById(R.id.trickle_swipe);
        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {

                if (!detector.isConnectedToInternet()) {
                    Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                } else {
                    // Load all drops and refresh
                    LoadAllDropsTask loadAllDropsTask = new LoadAllDropsTask(true);
                    loadAllDropsTask.execute();
                }

                // Hide the refresh indicator after 5 seconds if no data is found
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mWaveSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 5000);

            }
        });

        return view;
    }

    //If the TrickleTabFragment is current in view and the user has this tip enabled, show it.
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

//        LoadAllDropsTask loadAllDropsTask = new LoadAllDropsTask();
//        loadAllDropsTask.execute();

        if (isVisibleToUser && loadSavedPreferences()) {
                trickleTip();
            }
    }

    //Test that gets shared prefs value for trickleTips and returns boolean
    public boolean loadSavedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean test = sharedPreferences.getBoolean("trickleTips", true);

        if (!test) {
            return false;
        } else {
            return true;
        }

    }

//    //Saves the shared prefs when the currentUser hides the tip
//    public void saveTipPreferences(String key, Boolean value){
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putBoolean(key, value);
//        editor.putBoolean("allTipsBoolean", false);
//        editor.commit();
//    }

    //Dialog box that shows Trickle tip upon viewing of this fragment until hidden by user
    public void trickleTip() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TrickleTabFragment.this.getActivity(), R.style.MyAlertDialogStyle);

        builder.setTitle("Trickle...");
        builder.setMessage(R.string.trickle_tip);

        builder.setNegativeButton("HIDE THIS TIP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SaveToSharedPrefs saveToSharedPrefs = new SaveToSharedPrefs();
                saveToSharedPrefs.saveBooleanPreferences(getActivity(), "trickleTips", false);
            }
        });

        builder.setPositiveButton("KEEP THIS AROUND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }


    //Async to start query of the 10 newest Drops
    public class LoadAllDropsTask extends AsyncTask<Void, Void, ArrayList<DropItem>> {

        List<ParseObject> listFromParse;
        boolean refresh = false;
        int pageNumber = 0;

        //Default constructor for onCreate query
        public LoadAllDropsTask(){
        }

        //Scroll constructor for onScroll query
        public LoadAllDropsTask(int pageNumber){
            Log.d(TAG, "currentPage = " + pageNumber);
            this.pageNumber = pageNumber;
        }

        //Refresh constructor for pull to refresh query
        public LoadAllDropsTask(boolean refresh) {
            this.refresh = refresh;
        }


        //Get Drops from Parse in Async
        @Override
        protected ArrayList<DropItem> doInBackground(Void... params) {
            final ParseQuery<ParseObject> dropQuery = ParseQuery.getQuery("Drop");

            // Limit of Drops to get from Parse
            int queryLimit = 10;
            // Amount of Drops to skip, activated with the onScroll constructor
            int skipNumber = 0;
            // If called with onScroll constructor do some logic
            // to determine the amount of Drops to skip
            if (pageNumber != 0) {
                int pageMultiplier = pageNumber - 1;
                skipNumber = pageMultiplier * queryLimit;
            // Otherwise, clear the list, because this is a default(refresh) query
            }else {
                if (allDropsList != null) {
                    allDropsList.clear();
                }
            }

            //Get Drops from Parse that were not created by the currentUser
            //and include the authorPointer, order these Drops based on the
            //the date they were posted
            dropQuery.orderByDescending("createdAt");
            dropQuery.include("authorPointer");
            dropQuery.whereNotEqualTo("authorPointer", currentUser);
            //Set the query limit
            dropQuery.setLimit(queryLimit);
            //Set the skip amount with logic above
            dropQuery.setSkip(skipNumber);
            //Set the data from Parse to listFromParse
            try {
                listFromParse = dropQuery.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //Iterate through the list and get all required Drop and Author data
            if (listFromParse != null) {
                for (int i = 0; i < listFromParse.size(); i++) {

                    final DropItem dropItemAll = new DropItem();

                    //Drop Author Data/////////////////////////////////////////////////////////
                    ParseObject authorData = (ParseObject) listFromParse.get(i).get("authorPointer");

                    //Get the authors picture
                    ParseFile parseProfilePicture = (ParseFile) authorData.get("parseProfilePicture");
                    if (parseProfilePicture != null) {
                        parseProfilePicture.getDataInBackground(new GetDataCallback() {
                            //Decode the picture at 100x100
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null) {
                                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    if (bmp != null) {
                                        Bitmap resized = Bitmap.createScaledBitmap(bmp, 100, 100, true);
                                        dropItemAll.setParseProfilePicture(resized);
                                    }
                                }
                            }
                        });
                    }

                    //Author display name
                    dropItemAll.setAuthorName((String) authorData.get("displayName"));
                    //Author id
                    dropItemAll.setAuthorId(authorData.getObjectId());
                    //Author Rank
                    dropItemAll.setAuthorRank(authorData.getString("userRank"));
                    //Author Riple Count
                    dropItemAll.setAuthorRipleCount(String.valueOf(authorData.getInt("userRipleCount")));
                    //Author Info
                    dropItemAll.setAuthorInfo(authorData.getString("userInfo"));

                    //Author location
                    String authorLocation = authorData.getString("userLastLocation");
                    if (authorLocation == null) {
                        dropItemAll.setUserLastLocation("Location unknown");
                    } else {
                        dropItemAll.setUserLastLocation("From " + authorData.getString("userLastLocation"));
                    }

                    //Drop Data///////////////////////////////////////////////////////////////
                    //DropObjectId
                    dropItemAll.setObjectId(listFromParse.get(i).getObjectId());
                    //Drop description
                    dropItemAll.setDescription(listFromParse.get(i).getString("description"));



                   //Get created at from parse and convert it to friendly String
                    Format formatter = new SimpleDateFormat("MMM dd, yyyy @ h a");
                    String dateAfter = formatter.format(listFromParse.get(i).getCreatedAt());
                    dropItemAll.setCreatedAt(dateAfter);


//                    //Get created at from parse and convert it to friendly String
//                    SimpleDateFormat input = new SimpleDateFormat( "yyyy'-'MM'-'dd'T'HH':'mm':'ss.SSS'Z'");
//                    input.setTimeZone(TimeZone.getTimeZone("UTC"));
//                    Date date = listFromParse.get(i).getCreatedAt();
//
//                    SimpleDateFormat output = new SimpleDateFormat("MMM dd, yyyy @ h a z");
//                    output.setTimeZone(TimeZone.getDefault());
//                    String dateAfter = input.format(date);
//                    dropItemAll.setCreatedAt(dateAfter);




                    //Riple Count
                    int ripleCount = (listFromParse.get(i).getInt("ripleCount"));
                    if (ripleCount == 1) {
                        dropItemAll.setRipleCount(String.valueOf(ripleCount) + " Riple");
                    } else {
                        dropItemAll.setRipleCount(String.valueOf(ripleCount) + " Riples");
                    }

                    //Comment Count
                    int commentCount = (listFromParse.get(i).getInt("commentCount"));
                    if (commentCount == 1) {
                        dropItemAll.setCommentCount(String.valueOf(commentCount) + " Comment");
                    } else {
                        dropItemAll.setCommentCount(String.valueOf(commentCount) + " Comments");
                    }

                    //Add all of these DropItems to the field arrayList allDropsList
                    allDropsList.add(dropItemAll);
                }
            }


            //Return the said list
            return allDropsList;
        }

        @Override
        protected void onPostExecute(ArrayList<DropItem> dropItems) {

            //Upon postExecute of the allDropQuery, this will pass certain parameters to the
            //second Async that will give it the proper arguments to complete the query properly


            //If the query is the result of a scroll, pass the pageNumber to the LoadRelationDropsTask
            if (pageNumber != 0) {
                LoadRelationDropsTask nextTask = new LoadRelationDropsTask(pageNumber);
                nextTask.execute();
            }

            //If the query is the result of a refresh, pass the pageNumber to the LoadRelationDropsTask
            if (refresh) {
                LoadRelationDropsTask nextTask = new LoadRelationDropsTask(true);
                nextTask.execute();
            }

            //Otherwise call the default constructor for the standard LoadRelationDropsTask
            if (pageNumber == 0 && !refresh) {
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

        //Default constuctor for onCreate query
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
        //This will allow us to iterate through the allDropsList
        //and remove duplicate DropItems. This will cause all Drops
        //that the currentUser is related to to be omitted
        //from the TrickleTab

        // ***We only want to see Drops we haven't created, completed or to-do'd in the TrickleTab***

        @Override
        protected ArrayList<DropItem> doInBackground(Void... params) {

            //Get currentUser hasRelationTo reference
            ParseRelation relation = currentUser.getRelation("hasRelationTo");

            //Get hasRelationToQuery
            final ParseQuery hasRelationQuery = relation.getQuery();
            try {
                parseRelationDrops = hasRelationQuery.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //Clear this list to avoid duplicates and then get updated hasRelationTo list
            hasRelationList.clear();

            //If relation Drops from Parse is not empty, set their objetIds and add them to the
            //hasRelationList. All we need in these DropItems are the objectId for the sake
            //of finding duplicates when we iterate through the allDrops and hasRelation lists
            if (!parseRelationDrops.isEmpty()) {

                for (int i = 0; i < parseRelationDrops.size(); i++) {
                    DropItem dropItemRelation = new DropItem();
                    dropItemRelation.setObjectId(parseRelationDrops.get(i).getObjectId());
                    hasRelationList.add(dropItemRelation);
                }

                //Once done, send the two lists to the filterMethod
                asyncFilterDrops(hasRelationList, allDropsList);

            }
            Log.d(TAG, "HasRelationList = " + hasRelationList.size());

            //Return this hasRelationList
            return hasRelationList;

        }

        //Method to filter the Drops, removing the Drops that the currentUser
        //has created, completed or to-do'd
        public void asyncFilterDrops(ArrayList<DropItem> mHasRelationList, ArrayList<DropItem> mAllDropsList){

            //Create an allDropsIterator
            Iterator<DropItem> allDropsIterator = mAllDropsList.iterator();

            //While there are remaining Drops in the list, compare the allDropsList to the
            //hasRelationList and remove Duplicate Drops from theAllDropsList based on their objectId
            while(allDropsIterator.hasNext()) {
                DropItem dropItemAll = allDropsIterator.next();

                for(DropItem dropItemRelation  : mHasRelationList) {
                    if(dropItemAll.getObjectId().equals(dropItemRelation.getObjectId())){
                        allDropsIterator.remove();
                    }
                }
            }

            //Assign the filtered list to this trickleTabInteractionList for global use
            trickleTabInteractionList = allDropsList;
        }

        //Once Relation query finishes
        @Override
        protected void onPostExecute(ArrayList<DropItem> dropItems) {

            // If this was a onScrollQuery, notify data changed
            if (page != 0) {
                mTrickleAdapter.notifyDataSetChanged();
            //Otherwise, update the recyclerView
            } else {
                mWaveSwipeRefreshLayout.setRefreshing(false);
                mEndlessListener.reset(1, 0, true);
                updateRecyclerView(allDropsList);
            }


            //If pull to refresh caused this chain, end the refresh and reset the listener
//            if (refresh) {
//                mWaveSwipeRefreshLayout.setRefreshing(false);
//                mEndlessListener.reset(1, 0, true);
//
//                Log.d(TAG, "Page after reset = " + page);
//            }
//
//            //If scroll caused this chain, notify data changed to show the new data
//            if(page != 0) {
//                Log.d(TAG, "onScrollSize = " + allDropsList.size());
//                mTrickleAdapter.notifyDataSetChanged();
//
//            //Otherwise update the recyclerView
//            } else {
//                Log.d(TAG, "Refresh size = " + allDropsList.size());
//                updateRecyclerView(allDropsList);
// // //


        }
    }

    //Default update RecyclerView method used when activity is created. Only shows 10 items until scrolled
    private void updateRecyclerView(ArrayList<DropItem> filteredDropList) {

        //Logic to either show or hide the emptyTextView based on whether or not there is data to show
        if (filteredDropList.isEmpty()) {
            mTrickleRecyclerView.setVisibility(View.GONE);
            trickleEmptyView.setVisibility(View.VISIBLE);
        }
        else {
            mTrickleRecyclerView.setVisibility(View.VISIBLE);
            trickleEmptyView.setVisibility(View.GONE);
        }

        //Create an instance of the DropAdapter, and set it tot he mTrickleRecyclerView
        //with a alpha animation
        mTrickleAdapter = new DropAdapter(getActivity(), filteredDropList, "trickle");
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mTrickleAdapter);
        mTrickleRecyclerView.setAdapter(alphaAdapter);
        alphaAdapter.setDuration(1000);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}