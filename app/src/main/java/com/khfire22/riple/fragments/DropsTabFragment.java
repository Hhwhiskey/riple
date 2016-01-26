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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.khfire22.riple.R;
import com.khfire22.riple.model.DropAdapter;
import com.khfire22.riple.model.DropItem;
import com.khfire22.riple.utils.ConnectionDetector;
import com.khfire22.riple.utils.EndlessRecyclerViewOnScrollListener;
import com.khfire22.riple.utils.SaveToSharedPrefs;
import com.parse.FindCallback;
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
import java.util.List;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by Kevin on 9/8/2015.
 */
public class DropsTabFragment extends Fragment {

    private static final String TAG = "DropsTabFragment";
    private RecyclerView mDropRecyclerView;
    private Button button;
    private ArrayList<DropItem> mDropListFromParse;
    private DropAdapter mDropAdapter;
    public static ArrayList<DropItem> dropTabInteractionList;
    private boolean dropTips;
    private TextView dropEmptyView;
    private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    private EndlessRecyclerViewOnScrollListener mEndlessOnScrollListener;
    private LinearLayoutManager layoutManager;
    private ConnectionDetector detector;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drop_tab, container, false);

        detector = new ConnectionDetector(getActivity());

        mDropListFromParse = new ArrayList<>();

        layoutManager = new LinearLayoutManager(getActivity());

        mDropRecyclerView = (RecyclerView) view.findViewById(R.id.drop_recycler_view);
        mDropRecyclerView.setLayoutManager(layoutManager);
        mDropRecyclerView.setItemAnimator(new SlideInLeftAnimator(new AnticipateInterpolator(2f)));
        mDropRecyclerView.getItemAnimator().setRemoveDuration(500);
        dropEmptyView = (TextView) view.findViewById(R.id.drop_tab_empty_view);

        if (!detector.isConnectedToInternet()) {
            Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_LONG).show();
        } else {

            LoadDropItemsFromParse dropOnCreateQuery = new LoadDropItemsFromParse();
            dropOnCreateQuery.runLoadDropItemsFromParse();
        }

        mDropRecyclerView.addOnScrollListener(mEndlessOnScrollListener = new EndlessRecyclerViewOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {

                if (!detector.isConnectedToInternet()) {
                    Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                } else {
                    LoadDropItemsFromParse dropOnScrollQuery = new LoadDropItemsFromParse(current_page);
                    dropOnScrollQuery.runLoadDropItemsFromParse();
                }
            }
        });

        //Swipe Refresh
        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) view.findViewById(R.id.drop_swipe);
        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (!detector.isConnectedToInternet()) {
                    Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                } else {
                    // Do work to refresh the list here.
                    LoadDropItemsFromParse dropRefreshQuery = new LoadDropItemsFromParse(true);
                    dropRefreshQuery.runLoadDropItemsFromParse();
                    new dropRefreshTask().execute();
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

//        LoadDropItemsFromParse loadDropItemsFromParse = new LoadDropItemsFromParse();
//        loadDropItemsFromParse.runLoadDropItemsFromParse();

        if (isVisibleToUser && loadSavedPreferences()) {
            dropTip();
        }
    }

    public boolean loadSavedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean test = sharedPreferences.getBoolean("dropTips", true);

        if (!test) {
            return false;
        } else {
            return true;
        }

    }

//    public void saveTipPreferences(String key, Boolean value) {
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putBoolean(key, value);
//        editor.putBoolean("allTipsBoolean", false);
//        editor.commit();
//
////        MainActivity mainActivity = new MainActivity();
////        mainActivity.isBoxChecked(false);
//    }

    public void dropTip() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DropsTabFragment.this.getActivity(), R.style.MyAlertDialogStyle);

        builder.setTitle("Drops...");
        builder.setMessage(R.string.drop_tip);

        builder.setNegativeButton("HIDE THIS TIP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SaveToSharedPrefs saveToSharedPrefs = new SaveToSharedPrefs();
                saveToSharedPrefs.saveBooleanPreferences(getActivity(), "dropTips", false);

            }
        });

        builder.setPositiveButton("KEEP THIS AROUND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    public class LoadDropItemsFromParse {

        //The passed in refresh boolean, defaults to false
        public boolean refresh = false;
        //The passed in pageNumber, defaults to 0
        public int pageNumber = 0;
        //The limit of Drop Objects to get from Parse
        public int queryLimit = 10;
        //The amount of Drop Objects to skip from Parse
        public int skipNumber = 0;

        //Default constructor for onCreate query
        public LoadDropItemsFromParse() {
        }

        //Refresh constructor for pull to refresh query
        public LoadDropItemsFromParse(boolean refresh) {
            this.refresh = refresh;
        }

        //Page constuctor for onScroll query
        public LoadDropItemsFromParse(int pageNumber) {
            this.pageNumber = pageNumber;
        }

        public void runLoadDropItemsFromParse() {

            if (pageNumber != 0) {
                int pageMultiplier = pageNumber - 1;
                skipNumber = pageMultiplier * queryLimit;
                // Otherwise, clear the list, because this is a default(refresh) query
            } else {
                if (mDropListFromParse != null) {
                    mDropListFromParse.clear();
                }
            }

            ParseUser user = ParseUser.getCurrentUser();

            ParseRelation relation = user.getRelation("todoDrops");

            ParseQuery query = relation.getQuery();

            query.setLimit(queryLimit);
            query.setSkip(skipNumber);
            query.include("authorPointer");
            query.orderByDescending("createdAt");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {

                    if (e != null) {
                        Log.i("KEVIN", "error error");

                    } else {

                        for (int i = 0; i < list.size(); i++) {

                            //Collects Drop Objects
                            //                        dropObjectsList.add(list.get(i));

                            final DropItem dropItem = new DropItem();

                            //Drop Author Data//////////////////////////////////////////////////////////
                            ParseObject authorData = (ParseObject) list.get(i).get("authorPointer");

                            ParseFile parseProfilePicture = (ParseFile) authorData.get("parseProfilePicture");
                            if (parseProfilePicture != null) {
                                parseProfilePicture.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {
                                        if (e == null) {
                                            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                            Bitmap resized = Bitmap.createScaledBitmap(bmp, 100, 100, true);
                                            dropItem.setParseProfilePicture(resized);
                                            if (pageNumber != 0) {
                                                mDropAdapter.notifyDataSetChanged();
                                            } else {

                                                if (mDropListFromParse != null) {
                                                    updateRecyclerView(mDropListFromParse);
                                                }
                                            }
                                        }
                                    }
                                });
                            }

                            //dropItemAll.setAuthorName(authorName);
                            dropItem.setAuthorName((String) authorData.get("displayName"));
                            //Author id
                            dropItem.setAuthorId(authorData.getObjectId());
                            //Author Rank
                            dropItem.setAuthorRank(authorData.getString("userRank"));
                            //Author Riple Count
                            dropItem.setAuthorRipleCount(String.valueOf(authorData.getInt("userRipleCount")));
                            //Author Info
                            dropItem.setAuthorInfo(authorData.getString("userInfo"));

                            //Drop Data////////////////////////////////////////////////////////////////
                            //DropObjectId
                            dropItem.setObjectId(list.get(i).getObjectId());
                            //Drop description
                            dropItem.setDescription(list.get(i).getString("description"));

                            //Get created at from parse and convert it to friendly String
                            Format formatter = new SimpleDateFormat("MMM dd, yyyy @ h a");
                            String dateAfter = formatter.format(list.get(i).getCreatedAt());
                            dropItem.setCreatedAt(dateAfter);

                            //Riple Count
                            int ripleCount = (list.get(i).getInt("ripleCount"));
                            if (ripleCount == 1) {
                                dropItem.setRipleCount(String.valueOf(list.get(i).getInt("ripleCount") + " Riple"));
                            } else {
                                dropItem.setRipleCount(String.valueOf(list.get(i).getInt("ripleCount") + " Riples"));
                            }

                            //Comment Count
                            int commentCount = (list.get(i).getInt("commentCount"));
                            if (commentCount == 1) {
                                dropItem.setCommentCount(String.valueOf(list.get(i).getInt("commentCount") + " Comment"));
                            } else {
                                dropItem.setCommentCount(String.valueOf(list.get(i).getInt("commentCount") + " Comments"));
                            }


                            mDropListFromParse.add(dropItem);
                        }
                    }

                    Log.d(TAG, "DropList = " + mDropListFromParse.size());
                    dropTabInteractionList = mDropListFromParse;
                }
            });
        }
    }

    private void updateRecyclerView(ArrayList<DropItem> dropList) {
        Log.d("kevinDropList", "Drop LIST SIZE: " + dropList.size());

        if (dropList.isEmpty()) {
            mDropRecyclerView.setVisibility(View.GONE);
            dropEmptyView.setVisibility(View.VISIBLE);
        } else {
            mDropRecyclerView.setVisibility(View.VISIBLE);
            dropEmptyView.setVisibility(View.GONE);
        }

        // Alpha animation
        mDropAdapter = new DropAdapter(getActivity(), dropList, "drop");
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mDropAdapter);
        mDropRecyclerView.setAdapter(alphaAdapter);
        alphaAdapter.setDuration(1000);

        // Alpha and scale animation
//        mDropAdapter = new DropAdapter(getActivity(), dropList, "drop");
//        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(mDropAdapter);
//        mDropRecyclerView.setAdapter(new AlphaInAnimationAdapter(scaleAdapter));
//        scaleAdapter.setDuration(500);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private class dropRefreshTask extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... params) {
            return new String[0];
        }

        @Override
        protected void onPostExecute(String[] result) {
            // Call setRefreshing(false) when the list has been refreshed.
            mWaveSwipeRefreshLayout.setRefreshing(false);
            mEndlessOnScrollListener.reset(1, 0, true);
            super.onPostExecute(result);
        }
    }
}