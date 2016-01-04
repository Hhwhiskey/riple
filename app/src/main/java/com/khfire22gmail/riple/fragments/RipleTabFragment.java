package com.khfire22gmail.riple.fragments;

import android.content.DialogInterface;
import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.activities.SettingsActivity;
import com.khfire22gmail.riple.activities.ViewUserActivity;
import com.khfire22gmail.riple.model.DropAdapter;
import com.khfire22gmail.riple.model.DropItem;
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;


/**
 * Created by Kevin on 9/8/2015.
 */
public class RipleTabFragment extends Fragment {

    private ImageView profilePictureView;
    private TextView nameView;
    private RecyclerView ripleRecyclerView;
    private List<DropItem> mRipleList;
    private DropAdapter ripleAdapter;
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
    private ArrayList <DropItem> mOnScrollListFromFromParse;
    private ArrayList<DropItem> mRipleListLocal;
    private List<ParseObject> listFromParse;
    private List<ParseObject> mParseList;
    private EndlessRecyclerViewOnScrollListener mEndlessListener;
    private static String TAG = RipleTabFragment.class.getSimpleName();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateUserInfo();
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_riple_tab, container, false);

        mOnScrollListFromFromParse = new ArrayList<>();

        currentUser =  ParseUser.getCurrentUser();
//        userName  = currentUser.getString("displayName");
        facebookId = currentUser.getString("facebookId");

        //loadSavedPreferences();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        ripleRecyclerView = (RecyclerView) view.findViewById(R.id.riple_recycler_view);
        ripleRecyclerView.setLayoutManager(layoutManager);
        ripleRecyclerView.setItemAnimator(new SlideInLeftAnimator());


        // Set onScroll Listener
        ripleRecyclerView.addOnScrollListener(mEndlessListener = new EndlessRecyclerViewOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                Log.d(TAG, "onLoadMore current_page: " + current_page);
                loadRipleItemsOnScroll(current_page);
            }
        });

        ripleEmptyView = (TextView) view.findViewById(R.id.riple_tab_empty_view);

        //Displays data on profile Card
        profilePictureView = (ImageView) view.findViewById(R.id.profile_card_picture);
        nameView = (TextView) view.findViewById(R.id.profile_name);
        profileRankView = (TextView) view.findViewById(R.id.profile_rank);
        profileRipleCountView = (TextView) view.findViewById(R.id.profile_riple_count);

        //Update riple list and profile card
        loadRipleItemsFromParse();
//        updateUserInfo();

        savePreferences("ripleTipBoolean", true);

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

        //Pull refresh drops
        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) view.findViewById(R.id.riple_swipe);
        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                // Do work to refresh the list here.
                loadRipleItemsFromParse();
//                loadRipleItemsFromLocal();
//                updateUserInfo();
                mEndlessListener.reset();
                new refreshQuery().execute();
            }
        });



        return view;
    }

    private class refreshQuery extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... params) {
            return new String[0];
        }

        @Override protected void onPostExecute(String[] result) {
            // Call setRefreshing(false) when the list has been refreshed.
            mWaveSwipeRefreshLayout.setRefreshing(false);
//            mEndlessListener.reset();

//            final int resetPageNumer = 1;
//
//            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//            ripleRecyclerView.addOnScrollListener(new EndlessRecyclerViewOnScrollListener(layoutManager) {
//                @Override
//                public void onLoadMore(int current_page) {
//
//                    current_page = resetPageNumer;
//
//                    Log.d(TAG, "onLoadMore current_page: " + current_page);
//                    loadRipleItemsOnScroll(current_page);
//                }
//            });

            super.onPostExecute(result);
        }
    }

    public void loadSavedPreferences() {
        android.content.SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean ripleTipBoolean = sharedPreferences.getBoolean("ripleTipBoolean", true);
        if (ripleTipBoolean) {
            ripleTip();
        }
    }

    public void savePreferences(String key, Boolean value) {
        android.content.SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }


    public void unCheckAllTipsCheckBox(String key, Boolean value) {
        android.content.SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void ripleTip() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RipleTabFragment.this.getActivity(), R.style.MyAlertDialogStyle);

        builder.setTitle("Riple...");
        builder.setMessage("This is your Riple headquarters, all of your created and " +
                "completed Drops will be listed here. Your Riple count will be tracked and you will" +
                " be given a Riple Rank, accordingly. Make a bigger Riple to increase your rank." +
                " Nobody likes a showoff, but it certainly does feel good to see the impact you " +
                "have made, doesn't it?");

        builder.setNegativeButton("HIDE THIS TIP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                savePreferences("ripleTipBoolean", false);
                unCheckAllTipsCheckBox("allTipsBoolean", false);
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
        String currentUserName = currentUser.getString("displayName");

        Intent intent = new Intent(getActivity(), ViewUserActivity.class);
        intent.putExtra("clickedUserId", currentUserId);
        intent.putExtra("clickedUserName",currentUserName);
        getActivity().startActivity(intent);
    }



    public void loadRipleItemsFromParse() {

//        final ArrayList<DropItem> ripleListFromParse = new ArrayList<>();

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
        mainQuery.setLimit(10);
        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> listParse, ParseException e) {

                if (e != null) {
                    Log.i("KEVIN", "error error");

                } else {

                    mOnScrollListFromFromParse.clear();

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
//                                        Bitmap resized = Bitmap.createScaledBitmap(bmp, 100, 100, true);
                                        dropItem.setParseProfilePicture(bmp);
                                        updateRecyclerView(mOnScrollListFromFromParse);
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
                        }else {
                            dropItem.setCommentCount(String.valueOf(listParse.get(i).getInt("commentCount") + " Comments"));
                        }

                        mOnScrollListFromFromParse.add(dropItem);
//                        ParseObject.pinAllInBackground("pinnedQuery", listParse);
                    }
                }
            }
        });
    }

    public void loadRipleItemsOnScroll(int page) {

        // If the number isn't 0
        // The page number minus 1 times 10

        // Do not replace data in adapter but
        // add to the dataset

        // it should only replace it if we clear the
        // list or reset the adapter

        int skipNumber = 0;
        if (page != 0) {
            int pageMultiplier = page - 1;
            skipNumber = pageMultiplier * 10;
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
        mainQuery.setLimit(10);
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
//                                        Bitmap resized = Bitmap.createScaledBitmap(bmp, 100, 100, true);
                                        dropItem.setParseProfilePicture(bmp);
                                        updateRecyclerView(mOnScrollListFromFromParse);
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
                        }else {
                            dropItem.setCommentCount(String.valueOf(listParse.get(i).getInt("commentCount") + " Comments"));
                        }

                        mOnScrollListFromFromParse.add(dropItem);
//                        ParseObject.pinAllInBackground("pinnedQuery", listParse);
                    }
                }
            }
        });
    }

    public void loadRipleItemsFromLocal() {
        ParseUser currentUser = ParseUser.getCurrentUser();

        mRipleListLocal = new ArrayList<>();

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
        mainQuery.fromLocalDatastore();
//        mainQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> listLocal, ParseException e) {

                if (e != null) {
                    Log.i("KEVIN", "error error");

                    mOnScrollListFromFromParse.clear();

                } else {
                    for (int i = 0; i < mOnScrollListFromFromParse.size(); i++) {


                        final DropItem dropItem = new DropItem();

                        //Drop Author Data//////////////////////////////////////////////////////////
                        ParseObject authorData = (ParseObject) listLocal.get(i).get("authorPointer");

                        ParseFile parseProfilePicture = (ParseFile) authorData.get("parseProfilePicture");
                        if (parseProfilePicture != null) {
                            parseProfilePicture.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null) {
                                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//                                        Bitmap resized = Bitmap.createScaledBitmap(bmp, 100, 100, true);
                                        dropItem.setParseProfilePicture(bmp);
                                        updateRecyclerView(mOnScrollListFromFromParse);
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

                        //Drop Data////////////////////////////////////////////////////////////////
                        //DropObjectId
                        dropItem.setObjectId(listLocal.get(i).getObjectId());
                        //CreatedAt
                        dropItem.setCreatedAt(listLocal.get(i).getCreatedAt());
                        //dropItem.createdAt = new SimpleDateFormat("EEE, MMM d yyyy @ hh 'o''clock' a").parse("date");
                        //Drop description
                        dropItem.setDescription(listLocal.get(i).getString("description"));

                        //Riple Count
                        int ripleCount = (listLocal.get(i).getInt("ripleCount"));
                        if (ripleCount == 1) {
                            dropItem.setRipleCount(String.valueOf(listLocal.get(i).getInt("ripleCount") + " Riple"));
                        } else {
                            dropItem.setRipleCount(String.valueOf(listLocal.get(i).getInt("ripleCount") + " Riples"));
                        }

                        //Comment Count
                        int commentCount = (listLocal.get(i).getInt("commentCount"));
                        if (commentCount == 1) {
                            dropItem.setCommentCount(String.valueOf(listLocal.get(i).getInt("commentCount") + " Comment"));
                        }else {
                            dropItem.setCommentCount(String.valueOf(listLocal.get(i).getInt("commentCount") + " Comments"));
                        }

                        mOnScrollListFromFromParse.add(dropItem);
                    }
                }
            }
        });
    }

    public void updateRecyclerView(List<DropItem> mRipleList) {

        Log.d("Riple", "list size = " + mRipleList.size());

        if (mRipleList.isEmpty()) {
            ripleRecyclerView.setVisibility(View.GONE);
            ripleEmptyView.setVisibility(View.VISIBLE);
        }
        else {
            ripleRecyclerView.setVisibility(View.VISIBLE);
            ripleEmptyView.setVisibility(View.GONE);
        }

        ripleAdapter = new DropAdapter(getActivity(), mRipleList, "riple");
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(ripleAdapter);
        ripleRecyclerView.setAdapter(new AlphaInAnimationAdapter(scaleAdapter));
        scaleAdapter.setDuration(500);

    }

    private void updateUserInfo() {

        if ((currentUser != null) && currentUser.isAuthenticated()) {

            parseProfilePicture = (ParseFile) currentUser.get("parseProfilePicture");
        }

        //get parse profile picture if exists, if not, store Facebook picture on Parse and show
        if (parseProfilePicture != null) {
            Glide.with(this)
                    .load(parseProfilePicture.getUrl())
                    .crossFade()
                    .fallback(R.drawable.ic_user_default)
                    .error(R.drawable.ic_user_default)
                    .signature(new StringSignature(UUID.randomUUID().toString()))
                    .into(profilePictureView);
        } else {
            if (facebookId != null) {
                Log.d("MyApp", "FB ID (Main Activity) = " + facebookId);
                new DownloadImageTask(profilePictureView)
                        .execute("https://graph.facebook.com/" + facebookId + "/picture?type=large");
            }
        }

         String userName =currentUser.getString("displayName");

        // Update UserName
        if (userName != null) {
            nameView.setText(userName);
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

        String ripleRank = "\"Drop\"";

        int ripleCount = userObject.getInt("ripleCount");

        if (ripleCount > 19) {
            ripleRank = ("\"Volunteer\"");//2
        }
        if (ripleCount > 39) {
            ripleRank = ("\"Do-Gooder\"");//3
        }
        if (ripleCount > 79) {
            ripleRank = ("\"Contributor\"");//4
        }
        if (ripleCount > 159) {
            ripleRank = ("\"Patron\"");//5
        }
        if (ripleCount > 319) {
            ripleRank = ("\"Beneficent\"");//6
        }
        if (ripleCount > 639) {
            ripleRank = ("\"Humanitarian\"");//7
        }
        if (ripleCount > 1299) {
            ripleRank = ("\"Saint\"");//8
        }
        if (ripleCount > 2499) {
            ripleRank = ("\"Gandhi\"");//9
        }
        if (ripleCount > 4999) {
            ripleRank = ("\"Mother Teresa\"");//10
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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImageView;


        public DownloadImageTask(ImageView bmImage) {
            this.bmImageView = bmImage;
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
    }



    @Override
    public void onResume() {
        super.onResume();
        updateUserInfo();
    }


}
