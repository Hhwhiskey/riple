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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.activities.SettingsActivity;
import com.khfire22gmail.riple.activities.ViewUserActivity;
import com.khfire22gmail.riple.model.DropAdapter;
import com.khfire22gmail.riple.model.DropItem;
import com.khfire22gmail.riple.utils.Constants;
import com.khfire22gmail.riple.utils.EndlessRecyclerViewOnScrollListener;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;


/**
 * Created by Kevin on 9/8/2015.
 */
public class RipleTabFragment extends Fragment {

    private static final String TAG = "RipleTabFragment";
    private ImageView profilePictureView;
    private TextView nameView;
    private RecyclerView mRipleRecyclerView;
    private List<DropItem> mRipleList;
    private DropAdapter mRipleAdapter;
    private RecyclerView.ItemAnimator animator;
    private TextView profileRankView;
    private TextView profileRipleCountView;
    private TextView parseRankView;
    private ParseFile parseProfilePicture;
    private boolean ripleTipBoolean;
    private TextView ripleEmptyView;
    private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    private ParseUser currentUser;
    public String userName;
    public String facebookId;
    private ArrayList<DropItem> mRipleListFromParse;
    private ArrayList<DropItem> mRipleListLocal;
    private List<ParseObject> listFromParse;
    private List<ParseObject> mParseList;
    private EndlessRecyclerViewOnScrollListener mEndlessListener;
    private boolean visible;
    private LinearLayoutManager layoutManager;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        updateUserInfo();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_riple_tab, container, false);

        mRipleListFromParse = new ArrayList<>();

        currentUser = ParseUser.getCurrentUser();
        userName = currentUser.getString("displayName");
        facebookId = currentUser.getString("facebookId");

        mRipleRecyclerView = (RecyclerView) view.findViewById(R.id.riple_recycler_view);
        mRipleRecyclerView.setLayoutManager(layoutManager = new LinearLayoutManager(getActivity()));
        mRipleRecyclerView.setItemAnimator(new SlideInLeftAnimator());

        ripleEmptyView = (TextView) view.findViewById(R.id.riple_tab_empty_view);

        //Displays data on profile Card
        profilePictureView = (ImageView) view.findViewById(R.id.profile_card_picture);
        nameView = (TextView) view.findViewById(R.id.profile_name);
        profileRankView = (TextView) view.findViewById(R.id.profile_rank);
        profileRipleCountView = (TextView) view.findViewById(R.id.profile_riple_count);

        updateUserInfo();

        //Default onCreate Query call
        LoadRipleItemsFromParse onCreateQuery = new LoadRipleItemsFromParse();
        onCreateQuery.runLoadRipleItemsFromParse();

        profilePictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parseProfilePicture == null) {
                    Intent settingIntent = new Intent(getActivity(), SettingsActivity.class);
                    startActivity(settingIntent);
                } else {
                    viewCurrentUserProfileExtra();
                }
            }
        });

        nameView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                viewCurrentUserProfileExtra();
            }
        });

        profileRankView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                viewCurrentUserProfileExtra();
            }
        });
        profileRipleCountView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                viewCurrentUserProfileExtra();
            }
        });

        // Set onScroll Listener
        mRipleRecyclerView.addOnScrollListener(mEndlessListener = new EndlessRecyclerViewOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                Log.d(TAG, "onLoadMore current_page: " + current_page);
                //OnScroll Query call
                LoadRipleItemsFromParse onScrollQuery = new LoadRipleItemsFromParse(current_page);
                onScrollQuery.runLoadRipleItemsFromParse();

            }
        });

        //Pull to refresh riple list
        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) view.findViewById(R.id.riple_swipe);
        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Run the standard Riple Query for most recent items
                //onRefresh Query call
                LoadRipleItemsFromParse onRefreshQuery = new LoadRipleItemsFromParse(true);
                onRefreshQuery.runLoadRipleItemsFromParse();
                new ripleRefreshTask().execute();

            }
        });

        return view;
    }

    //Code to force crash for Fabric
//    public void forceCrash(View view) {
//        throw new RuntimeException("This is a crash");
//    }

    private class ripleRefreshTask extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... params) {
            return new String[0];
        }

        @Override
        protected void onPostExecute(String[] result) {
            // Call setRefreshing(false) when the list has been refreshed.
            mWaveSwipeRefreshLayout.setRefreshing(false);
            mEndlessListener.reset(1, 0, true);

            super.onPostExecute(result);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//
//        LoadRipleItemsFromParse loadRipleItemsFromParse = new LoadRipleItemsFromParse();
//        loadRipleItemsFromParse.runLoadRipleItemsFromParse();

        if (isVisibleToUser && loadSavedPreferences()) {
            ripleTip();
        }
    }

    public boolean loadSavedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean test = sharedPreferences.getBoolean("ripleTips", true);

        if (!test) {
            return false;
        } else {
            return true;
        }

    }

    public void saveTipPreferences(String key, Boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.putBoolean("allTipsBoolean", false);
        editor.commit();

//        MainActivity mainActivity = new MainActivity();
//        mainActivity.isBoxChecked(false);
    }

    public void ripleTip() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RipleTabFragment.this.getActivity(), R.style.MyAlertDialogStyle);

        builder.setTitle("Riple...");
        builder.setMessage("This is your Riple headquarters. All of your created and " +
                "completed Drops will be listed here. You will be given a rank based on how many " +
                "Riples you have created. Nobody likes a showoff, but it certainly does feel good " +
                "to see the impact you have made.");

        builder.setNegativeButton("HIDE THIS TIP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveTipPreferences("ripleTips", false);
            }
        });


        builder.setPositiveButton("KEEP THIS AROUND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    // Extra for currentUser profile view
    private void viewCurrentUserProfileExtra() {

        String currentUserId = currentUser.getObjectId();
        String currentUserDisplayName = currentUser.getString("displayName");
        String currentUserRank = currentUser.getString("userRank");
        String currentUserRipleCount = String.valueOf(currentUser.getInt("userRipleCount"));
        String currentUserInfo = currentUser.getString("userInfo");

        Intent intent = new Intent(getActivity(), ViewUserActivity.class);
        intent.putExtra(Constants.CLICKED_USER_ID, currentUserId);
        intent.putExtra(Constants.CLICKED_USER_NAME, currentUserDisplayName);
        intent.putExtra(Constants.CLICKED_USER_RANK, currentUserRank);
        intent.putExtra(Constants.CLICKED_USER_RIPLE_COUNT, currentUserRipleCount);
        intent.putExtra(Constants.CLICKED_USER_INFO, currentUserInfo);
        getActivity().startActivity(intent);
    }


    public void loadRipleItemsFromParse() {

        int queryLimit = 10;

        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseRelation createdRelation = currentUser.getRelation("createdDrops");
        ParseRelation completedRelation = currentUser.getRelation("completedDrops");

        ParseQuery createdQuery = createdRelation.getQuery();
        ParseQuery completedQuery = completedRelation.getQuery();

        List<ParseQuery<ParseObject>> queries = new ArrayList<>();
        queries.add(createdQuery);
        queries.add(completedQuery);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
        mainQuery.include("authorPointer");
        mainQuery.orderByDescending("createdAt");
        mainQuery.setLimit(queryLimit);
        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> listParse, ParseException e) {

                if (e != null) {
                    Log.i("KEVIN", "error error");

                } else {

                    mRipleListFromParse.clear();

                    for (int i = 0; i < listParse.size(); i++) {

                        final DropItem dropItem = new DropItem();

                        //Drop Author Data//////////////////////////////////////////////////////////
                        ParseObject authorData = (ParseObject) listParse.get(i).get("authorPointer");

                        ParseFile parseProfilePicture = (ParseFile) authorData.get("parseProfilePicture");
                        if (parseProfilePicture != null) {
                            parseProfilePicture.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null) {
                                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        Bitmap resized = Bitmap.createScaledBitmap(bmp, 100, 100, true);
                                        dropItem.setParseProfilePicture(resized);
                                        updateRecyclerView(mRipleListFromParse);
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

                        //Drop Data////////////////////////////////////////////////////////////////
                        //DropObjectId
                        dropItem.setObjectId(listParse.get(i).getObjectId());
                        //CreatedAt
                        dropItem.setCreatedAt(listParse.get(i).getCreatedAt());
                        //dropItem.createdAt = new SimpleDateFormat("EEE, MMM d yyyy @ hh 'o''clock' a").parse("date");
                        //Drop description
                        dropItem.setDescription(listParse.get(i).getString("description"));

                        //Riple Count
                        int ripleCount = (listParse.get(i).getInt("ripleCount"));
                        if (ripleCount == 1) {
                            dropItem.setRipleCount(String.valueOf(listParse.get(i).getInt("ripleCount") + " Riple"));
                        } else {
                            dropItem.setRipleCount(String.valueOf(listParse.get(i).getInt("ripleCount") + " Riples"));
                        }

                        //Comment Count
                        int commentCount = (listParse.get(i).getInt("commentCount"));
                        if (commentCount == 1) {
                            dropItem.setCommentCount(String.valueOf(listParse.get(i).getInt("commentCount") + " Comment"));
                        } else {
                            dropItem.setCommentCount(String.valueOf(listParse.get(i).getInt("commentCount") + " Comments"));
                        }

                        mRipleListFromParse.add(dropItem);
//                        ParseObject.pinAllInBackground("pinnedQuery", listParse);
                    }
                }
            }
        });
    }


    //Riple Query method with 3 constructors for different parameters
    public class LoadRipleItemsFromParse {

        //The passed in refresh boolean, defaults to false
        public boolean refresh = false;
        //The passed in pageNumber, defaults to 0
        public int pageNumber = 0;
        //The limit of Drop Objects to get from Parse
        public int queryLimit = 10;
        //The amount of Drop Objects to skip from Parse
        public int skipNumber = 0;

        //Default constructor for onCreate query
        public LoadRipleItemsFromParse() {
        }

        //Refresh constructor for pull to refresh query
        public LoadRipleItemsFromParse(boolean refresh) {
            this.refresh = refresh;
        }

        //Page constuctor for onScroll query
        public LoadRipleItemsFromParse(int pageNumber) {
            this.pageNumber = pageNumber;
        }

        // If a pageNumber is passed in, the IF statement will be true and the logic will run
        public void runLoadRipleItemsFromParse() {

            if (pageNumber != 0) {
                int pageMultiplier = pageNumber - 1;
                skipNumber = pageMultiplier * queryLimit;
                // Otherwise, clear the list, because this is a default(refresh) query
            } else {
                if (mRipleListFromParse != null) {
                    mRipleListFromParse.clear();
                }
            }

            ParseUser currentUser = ParseUser.getCurrentUser();

            ParseRelation createdRelation = currentUser.getRelation("createdDrops");
            ParseRelation completedRelation = currentUser.getRelation("completedDrops");

            ParseQuery createdQuery = createdRelation.getQuery();
            ParseQuery completedQuery = completedRelation.getQuery();

            List<ParseQuery<ParseObject>> queries = new ArrayList<>();
            queries.add(createdQuery);
            queries.add(completedQuery);

            ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
            mainQuery.include("authorPointer");
            mainQuery.orderByDescending("createdAt");
            mainQuery.setLimit(queryLimit);
            mainQuery.setSkip(skipNumber);
            mainQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> listParse, ParseException e) {

                    if (e != null) {
                        Log.i("KEVIN", "error error");

                    } else {

                        for (int i = 0; i < listParse.size(); i++) {

                            final DropItem dropItem = new DropItem();

                            //Drop Author Data//////////////////////////////////////////////////////////
                            ParseObject authorData = (ParseObject) listParse.get(i).get("authorPointer");

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
                                                mRipleAdapter.notifyDataSetChanged();
                                            } else {
                                                updateRecyclerView(mRipleListFromParse);
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
                            //Author RipleCount
                            dropItem.setAuthorRipleCount(String.valueOf(authorData.getInt("userRipleCount")));
                            //Author Info
                            dropItem.setAuthorInfo(authorData.getString("userInfo"));

                            //Drop Data////////////////////////////////////////////////////////////////
                            //DropObjectId
                            dropItem.setObjectId(listParse.get(i).getObjectId());
                            //CreatedAt
                            dropItem.setCreatedAt(listParse.get(i).getCreatedAt());
                            //dropItem.createdAt = new SimpleDateFormat("EEE, MMM d yyyy @ hh 'o''clock' a").parse("date");
                            //Drop description
                            dropItem.setDescription(listParse.get(i).getString("description"));

                            //Riple Count
                            int ripleCount = (listParse.get(i).getInt("ripleCount"));
                            if (ripleCount == 1) {
                                dropItem.setRipleCount(String.valueOf(listParse.get(i).getInt("ripleCount") + " Riple"));
                            } else {
                                dropItem.setRipleCount(String.valueOf(listParse.get(i).getInt("ripleCount") + " Riples"));
                            }

                            //Comment Count
                            int commentCount = (listParse.get(i).getInt("commentCount"));
                            if (commentCount == 1) {
                                dropItem.setCommentCount(String.valueOf(listParse.get(i).getInt("commentCount") + " Comment"));
                            } else {
                                dropItem.setCommentCount(String.valueOf(listParse.get(i).getInt("commentCount") + " Comments"));
                            }

                            Log.d(TAG, "Riple List = " + mRipleListFromParse.size());
                            mRipleListFromParse.add(dropItem);

                        }
                    }
                }
            });
        }
    }


    //Default updateRecyclerView method
    public void updateRecyclerView(List<DropItem> mRipleList) {

        Log.d("Riple", "list size = " + mRipleList.size());

        if (mRipleList.isEmpty()) {
            mRipleRecyclerView.setVisibility(View.GONE);
            ripleEmptyView.setVisibility(View.VISIBLE);
        } else {
            mRipleRecyclerView.setVisibility(View.VISIBLE);
            ripleEmptyView.setVisibility(View.GONE);
        }

        // Alpha animation
        mRipleAdapter = new DropAdapter(getActivity(), mRipleList, "riple");
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mRipleAdapter);
        mRipleRecyclerView.setAdapter(alphaAdapter);
        alphaAdapter.setDuration(1000);

//        // Alpha and scale animation
//        mRipleAdapter = new DropAdapter(getActivity(), mRipleList, "riple");
//        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(mRipleAdapter);
//        mRipleRecyclerView.setAdapter(new AlphaInAnimationAdapter(scaleAdapter));
//        scaleAdapter.setDuration(500);
    }

    //Updates all of their current user data in their profile card. Picture, name, rank and riple count
    private void updateUserInfo() {

        if ((currentUser != null) && currentUser.isAuthenticated()) {

            parseProfilePicture = (ParseFile) currentUser.get("parseProfilePicture");

            //If user has a parse picture, get/set it
            if (parseProfilePicture != null) {
                parseProfilePicture.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        if (e == null) {
                            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                            Bitmap resized = Bitmap.createScaledBitmap(bmp, 200, 200, true);
                            profilePictureView.setImageBitmap(resized);
                        }
                    }
                });
                //Otherwise, if they have a facebook id, use that picture instead
            } else {
                if (facebookId != null) {
                    Log.d("MyApp", "FB ID (Main Activity) = " + facebookId);
                    new DownloadImageTask(profilePictureView)
                            .execute("https://graph.facebook.com/" + facebookId + "/picture?type=large");
                }
            }
        }

        //Get currentUser displayName
        String displayName = currentUser.getString("displayName");

        // Update UserName
        if (displayName != null) {
            nameView.setText(displayName);
        } else {
            nameView.setText("Anonymous");
        }


        //Update Riple count and Rank
        ParseQuery userRipleCountQuery = ParseQuery.getQuery("UserRipleCount");
        userRipleCountQuery.whereEqualTo("userPointer", currentUser);
        userRipleCountQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                updateRipleCount(parseObject);
            }
        });
    }


    public void updateRipleCount(ParseObject userObject) {

        String ripleRank = "\"Drop\"";//1

        int ripleCount = userObject.getInt("ripleCount");

        if (ripleCount > 4) {
            ripleRank = ("\"Volunteer\"");//2
        }
        if (ripleCount > 9) {
            ripleRank = ("\"Contributor\"");//3

        }
        if (ripleCount > 19) {
            ripleRank = ("\"Do-Gooder\"");//4

        }
        if (ripleCount > 39) {
            ripleRank = ("\"Kind\"");//5
        }
        if (ripleCount > 79) {
            ripleRank = ("\"Generous\"");//6
        }
        if (ripleCount > 159) {
            ripleRank = ("\"Patron\"");//7
        }
        if (ripleCount > 319) {
            ripleRank = ("\"Benevolent\"");//8
        }
        if (ripleCount > 639) {
            ripleRank = ("\"Humanitarian\"");//9
        }
        if (ripleCount > 1279) {
            ripleRank = ("\"Altruist\"");//10
        }
        if (ripleCount > 2559) {
            ripleRank = ("\"Saint\"");//11
        }
        if (ripleCount > 4999) {
            ripleRank = ("\"Riple Master\"");//12
        }

        if (ripleCount > 9999) {
            ripleRank = ("\"2nd Riple Master\"");//13
        }
        if (ripleCount > 14999) {
            ripleRank = ("\"3rd Riple Master\"");//14
        }
        if (ripleCount > 19999) {
            ripleRank = ("\"4th Riple Master\"");//15
        }
        if (ripleCount > 24999) {
            ripleRank = ("\"5th Riple Master\"");//16
        }
        if (ripleCount > 29999) {
            ripleRank = ("\"6th Riple Master\"");//17
        }
        if (ripleCount > 34999) {
            ripleRank = ("\"7th Riple Master\"");//18
        }
        if (ripleCount > 39999) {
            ripleRank = ("\"8th Riple Master\"");//19
        }
        if (ripleCount > 44999) {
            ripleRank = ("\"9th Riple Master\"");//20
        }
        if (ripleCount > 49999) {
            ripleRank = ("\"10th Riple Master\"");//21
        }

        // Save the currentUser ripleCount and rank to the user table
        currentUser.put("userRipleCount", ripleCount);
        currentUser.put("userRank", ripleRank);
        currentUser.saveInBackground();

        // Display the currentUser riple count and rank
        if (ripleCount == 1) {
            profileRipleCountView.setText(String.valueOf(ripleCount) + " Riple");
        } else {
            profileRipleCountView.setText(String.valueOf(ripleCount) + " Riples");
        }
        profileRankView.setText(ripleRank);
    }

    //Task to download the users facebook picture
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;


        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("MyApp", e.getMessage());
                e.printStackTrace();
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            if (bmImage != null) {
                bmImage.setImageBitmap(result);
                //convert bitmap to byte array and upload to Parse
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                result.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                final ParseFile file = new ParseFile("parseProfilePicture.png", byteArray);
                file.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            currentUser.put("parseProfilePicture", file);
                            currentUser.saveInBackground();
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUserInfo();
    }
}
