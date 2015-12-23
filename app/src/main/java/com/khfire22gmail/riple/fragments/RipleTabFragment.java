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
import com.khfire22gmail.riple.activities.ViewUserActivity;
import com.khfire22gmail.riple.model.DropAdapter;
import com.khfire22gmail.riple.model.DropItem;
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
    private ParseUser currentUser = ParseUser.getCurrentUser();


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateUserInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_riple_tab, container, false);

        ripleRecyclerView = (RecyclerView) view.findViewById(R.id.riple_recycler_view);
        ripleRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ripleRecyclerView.setItemAnimator(new SlideInLeftAnimator());

        ripleEmptyView = (TextView) view.findViewById(R.id.riple_tab_empty_view);

        //Profile Card
        profilePictureView = (ImageView) view.findViewById(R.id.profile_card_picture);
        nameView = (TextView) view.findViewById(R.id.profile_name);
        profileRankView = (TextView) view.findViewById(R.id.profile_rank);
        profileRipleCountView = (TextView) view.findViewById(R.id.profile_riple_count);
        savePreferences("ripleTipBoolean", true);

        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) view.findViewById(R.id.riple_swipe);
        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                // Do work to refresh the list here.
                loadRipleItemsFromParse();
                updateUserInfo();
                new Task().execute();
            }
        });


//        loadSavedPreferences();



        currentUser = ParseUser.getCurrentUser();

        //Query the users created and completed drops_blue

        loadRipleItemsFromParse();

        nameView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                viewOtherUser();
            }
        });

        profileRankView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                viewOtherUser();
            }
        });

        profileRipleCountView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                viewOtherUser();
            }
        });

//        int size = (int) getResources().getDimension(R.dimen.com_facebook_profilepictureview_preset_size_large);
//        profilePictureView.setPresetSize(ProfilePictureView.LARGE);

//        Fetch Facebook user info if it is logged
//        ParseUser currentUser = ParseUser.getCurrentUser();
//        if ((currentUser != null) && currentUser.isAuthenticated()) {
//
//        }

        return view;
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

    private void viewOtherUser() {

        String currentUserId = currentUser.getObjectId();
        String currentUserName = currentUser.getString("displayName");

        Log.d("sDropViewUser", "Clicked User's Id = " + currentUserId);
        Log.d("sDropViewUser", "Clicked User's Name = " + currentUserName);

        Intent intent = new Intent(getActivity(), ViewUserActivity.class);
        intent.putExtra("clickedUserId", currentUserId);
        intent.putExtra("clickedUserName",currentUserName);
        getActivity().startActivity(intent);
    }


    // Listen
    // Action
    public interface onDropPostListener {
        public void onDropPost();
    }

    public onDropPostListener dropPostListener;

    public void loadRipleItemsFromParse() {
        final ArrayList<DropItem> ripleList = new ArrayList<>();

        ParseUser user = ParseUser.getCurrentUser();

        ParseRelation createdRelation = user.getRelation("createdDrops");
        ParseRelation completedRelation = user.getRelation("completedDrops");

        ParseQuery createdQuery = createdRelation.getQuery();
        ParseQuery completedQuery = completedRelation.getQuery();

        List<ParseQuery<ParseObject>> queries = new ArrayList<>();
        queries.add(createdQuery);
        queries.add(completedQuery);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
        mainQuery.include("authorPointer");
        mainQuery.orderByDescending("createdAt");
        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (e != null) {
                    Log.i("KEVIN", "error error");

                } else {
                    for (int i = 0; i < list.size(); i++) {

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
//                                        Bitmap resized = Bitmap.createScaledBitmap(bmp, 100, 100, true);
                                        dropItem.setParseProfilePicture(bmp);
                                        updateRecyclerView(ripleList);
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
                        dropItem.setObjectId(list.get(i).getObjectId());
                        //CreatedAt
                        dropItem.setCreatedAt(list.get(i).getCreatedAt());
                        //dropItem.createdAt = new SimpleDateFormat("EEE, MMM d yyyy @ hh 'o''clock' a").parse("date");
                        //Drop description
                        dropItem.setDescription(list.get(i).getString("description"));

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
                        }else {
                            dropItem.setCommentCount(String.valueOf(list.get(i).getInt("commentCount") + " Comments"));
                        }

                        ripleList.add(dropItem);

                    }
                }


            }
        });
    }

    public void updateRecyclerView(List<DropItem> mRipleList) {

        if (mRipleList.isEmpty()) {
            ripleRecyclerView.setVisibility(View.GONE);
            ripleEmptyView.setVisibility(View.VISIBLE);
        }
        else {
            ripleRecyclerView.setVisibility(View.VISIBLE);
            ripleEmptyView.setVisibility(View.GONE);
        }

        ripleAdapter = new DropAdapter(getActivity(), mRipleList, "created");
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(ripleAdapter);
        ripleRecyclerView.setAdapter(new AlphaInAnimationAdapter(scaleAdapter));
        scaleAdapter.setDuration(500);
    }

    private void updateUserInfo() {

        ParseUser currentUser = ParseUser.getCurrentUser();
        String userName = currentUser.getString("displayName");
        String facebookId = currentUser.getString("facebookId");

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
                //loader.displayImage("https://graph.facebook.com/" + fbId + "/picture?type=large", mProfPicField);
            }
        }

        // Update UserName
        if (userName != null) {
            nameView.setText(userName);
        } else {
            nameView.setText("Set your name!");
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
        if (ripleCount > 1279) {
            ripleRank = ("\"Saint\"");//8
        }
        if (ripleCount > 2559) {
            ripleRank = ("\"Gandhi\"");//9
        }
        if (ripleCount > 5119) {
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

        protected void onPostExecute(Bitmap result) {
            if (bmImageView != null) {
                bmImageView.setImageBitmap(result);
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

    private class Task extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... params) {
            return new String[0];
        }

        @Override protected void onPostExecute(String[] result) {
            // Call setRefreshing(false) when the list has been refreshed.
            mWaveSwipeRefreshLayout.setRefreshing(false);
            super.onPostExecute(result);
        }
    }
}
