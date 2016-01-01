package com.khfire22gmail.riple.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.khfire22gmail.riple.R;
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

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;


public class ViewUserActivity extends AppCompatActivity {

    private Object animator;
    private RecyclerView mViewUserRecyclerView;
    private DropAdapter mViewUserAdapter;
    private List<DropItem> mViewUserList;
    String EXTRA_IMAGE;
    public String mAuthorName;
    private String mClickedUserId;
    private String mClickedUserName;
    private String mClickedUserFacebookId;
    private ParseFile parseProfilePicture;
    private ImageView profilePictureView;
    private TextView viewUserEmptyView;

    // Added this to track current User's drop stuff
    private ArrayList<DropItem> mCurrentUserDrops = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        ViewCompat.setTransitionName(findViewById(R.id.appbar_view_user), EXTRA_IMAGE);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.view_user_collapsing_tool_bar);
        collapsingToolbar.setContentScrimColor(ContextCompat.getColor(this, R.color.ColorPrimary));

        loadSavedPreferences();
//        viewUserTip();

        //Receive extra intent information to load clicked user's profile
        Intent intent = getIntent();
        mClickedUserId = intent.getStringExtra("clickedUserId");
        mClickedUserName = intent.getStringExtra("clickedUserName");
        Log.d("rViewUser", "mClickedUserId = " + mClickedUserId);
        Log.d("rViewUser", "mClickedUserName = " + mClickedUserName);

        getViewedUserProfilePicture(mClickedUserId);

        loadRipleItemsFromParse();

        // Set collapsable toolbar picture and text
        profilePictureView = (ImageView)findViewById(R.id.view_user_profile_picture);

        collapsingToolbar.setTitle(mClickedUserName);

        mViewUserRecyclerView = (RecyclerView) findViewById(R.id.view_user_recycler_view);
        mViewUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mViewUserRecyclerView.setItemAnimator(new DefaultItemAnimator());

        viewUserEmptyView = (TextView) findViewById(R.id.view_user_empty_view);

        profilePictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewUserInfo(mClickedUserName, mClickedUserId);
            }
        });

        FloatingActionButton messageFab = (FloatingActionButton) findViewById(R.id.fab_message);
        messageFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(ViewUserActivity.this, MessagingActivity.class);
                intent.putExtra("RECIPIENT_ID", mClickedUserId);
                startActivity(intent);
            }
        });
    }

    public void loadSavedPreferences() {
        android.content.SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean viewUserTipBoolean = sharedPreferences.getBoolean("viewUserTipBoolean", true);
        if (viewUserTipBoolean) {
            viewUserTip();
        }
    }

    public void savePreferences(String key, Boolean value) {
        android.content.SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void unCheckAllTipsCheckBox(String key, Boolean value) {
        android.content.SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void viewUserTip() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewUserActivity.this, R.style.MyAlertDialogStyle);

        builder.setTitle("View Profile");
        builder.setMessage("This is where you view others Riple page. View all the Drops" +
                " they have created and completed. Touch their picture to get more info - You " +
                "can even check out their riple count and rank. Cool, huh?");

        builder.setNegativeButton("HIDE THIS TIP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                savePreferences("viewUserTipBoolean", false);
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

    public void viewUserInfo(String usersName, String aboutUser) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewUserActivity.this, R.style.MyAlertDialogStyle);

        builder.setTitle(usersName);
        builder.setMessage("Hi I'm Kevin and I am the creator of Riple. I am glad you are using " +
                "this exciting app alongside me. Lets make some big Riples!!!");

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.show();
    }

    private void getViewedUserProfilePicture(String mClickedUserId) {
        ParseQuery<ParseUser> viewUserQuery = ParseQuery.getQuery("_User");
        viewUserQuery.getInBackground(mClickedUserId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser clickedUserObject, ParseException e) {
                ParseFile viewUserProfilePicture = (ParseFile) clickedUserObject.get("parseProfilePicture");
                parseProfilePicture = viewUserProfilePicture;
                if (parseProfilePicture != null) {
                    parseProfilePicture.getDataInBackground(new GetDataCallback() {

                        @Override
                        public void done(byte[] data, ParseException e) {
                            if (e == null) {
                                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//                                Bitmap resized = Bitmap.createScaledBitmap(bmp, 1000, 1000, true);
                                profilePictureView.setImageBitmap(bmp);
                            }
                        }
                    });
                }
            }
        });
    }



    public void loadRipleItemsFromParse() {

        final ArrayList<DropItem> viewUserList = new ArrayList<>();

        ParseUser clickedUser = null;
        ParseQuery viewedUserQuery = ParseQuery.getQuery("_User");
        viewedUserQuery.whereEqualTo("objectId", mClickedUserId);

        try {
            if (viewedUserQuery.find().size() != 0) {
                clickedUser = (ParseUser) viewedUserQuery.find().get(0);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert clickedUser != null;
        ParseRelation createdRelation = clickedUser.getRelation("createdDrops");
        ParseRelation completedRelation = clickedUser.getRelation("completedDrops");

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
                                        updateRecyclerView(viewUserList);
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
                        dropItem.setRipleCount(String.valueOf(list.get(i).getInt("ripleCount") + " Riples"));
                        //Comment Count
                        dropItem.setCommentCount(String.valueOf(list.get(i).getInt("commentCount") + " Comments"));


                        viewUserList.add(dropItem);
                        ParseObject.pinAllInBackground(list);
                    }

//                    Log.i("KEVIN", "PARSE LIST SIZE: " + clickedUsersList.size());
                    // Don't update this until we have the list of the current user's stuff as well
                    // Then, we'll have the relations that we need to display the proper card.

                }

            }
        });
    }


    /*public void loadCurrentUserRipleItemsFromParse() {

        final ArrayList<DropItem> currentUsersList = new ArrayList<>();

        ParseUser clickedUser = null;
*//*        ParseQuery<ParseUser> getClickedUserquery = ParseQuery.getQuery("_User");
        getClickedUserquery.whereEqualTo("objectId", mClickedUserId);

        // TODO: Change this to findInBackground and pass in a callback to listen to when this inBackground finishes
       *//**//* getClickedUserquery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {*//**//*

        try {
            if (getClickedUserquery.find().size() != 0) {
                clickedUser = getClickedUserquery.find().get(0);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }*//*

        clickedUser = ParseUser.getCurrentUser();

        ParseRelation createdRelation = clickedUser.getRelation("createdDrops");
        ParseRelation completedRelation = clickedUser.getRelation("completedDrops");

        ParseQuery createdQuery = createdRelation.getQuery();
        ParseQuery completedQuery = completedRelation.getQuery();

        Log.d("viewUserId", "Id is currently " + mClickedUserId);

        List<ParseQuery<ParseObject>> queries = new ArrayList<>();
        queries.add(createdQuery);
        queries.add(completedQuery);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);

        mainQuery.orderByDescending("createdAt");

        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (e != null) {
                    Log.i("KEVIN", "error error");

                } else {
                    for (int i = 0; i < list.size(); i++) {

                        final DropItem dropItem = new DropItem();

                        ParseFile profilePicture = (ParseFile) list.get(i).get("authorPicture");
                        if (profilePicture != null) {
                            profilePicture.getDataInBackground(new GetDataCallback() {

                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null) {
                                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//                                        Bitmap resized = Bitmap.createScaledBitmap(bmp, 100, 100, true);
                                        dropItem.setParseProfilePicture(bmp);
                                    }
                                }
                            });
                        }

                        //ObjectId
                        dropItem.setObjectId(list.get(i).getObjectId());
                        //Author name
                        dropItem.setAuthorName(list.get(i).getString("name"));
                        //Author id
                        dropItem.setAuthorId(list.get(i).getString("author"));
                        //Date
                        dropItem.setCreatedAt(list.get(i).getCreatedAt());
//                      dropItem.createdAt = new SimpleDateFormat("EEE, MMM d yyyy @ hh 'o''clock' a").parse("date");

                        //Drop Title
//                        dropItem.setTitle(list.get(i).getString("title"));

                        //Drop description
                        dropItem.setDescription(list.get(i).getString("description"));

                        //Riple Count
                        dropItem.setRipleCount(String.valueOf(list.get(i).getInt("ripleCount") + " Riples"));

                        //Comment Count
                        dropItem.setCommentCount(String.valueOf(list.get(i).getInt("commentCount") + " Comments"));

                        //Id that connects commenterName to drop
//                              dropItem.setCommenterName(list.get(i).getString("commenterName"));

                        currentUsersList.add(dropItem);
                    }

                    Log.i("KEVIN", "CURRENT USER DROP LIST SIZE: " + currentUsersList.size());
                    // Don't update this until we have the list of the current user's stuff as well
                    // Then, we'll have the relations that we need to display the proper card.
                    mCurrentUserDrops = currentUsersList;

                }
            }
        });
    }*/





    private void updateRecyclerView(ArrayList<DropItem> clickedUserList) {
        Log.d("VIEWUSERLIST", "CLICKED USER LIST SIZE: " + clickedUserList.size());
//        Log.d("VIEWUSERLIST", "CURRENT USER LIST SIZE: " + mCurrentUserDrops.size());

        if (clickedUserList.isEmpty()) {
            mViewUserRecyclerView.setVisibility(View.GONE);
            viewUserEmptyView.setVisibility(View.VISIBLE);
        }
        else {
            mViewUserRecyclerView.setVisibility(View.VISIBLE);
            viewUserEmptyView.setVisibility(View.GONE);
        }

        mViewUserAdapter = new DropAdapter(this, clickedUserList, "riple");
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(mViewUserAdapter);
        scaleAdapter.setDuration(250);
        mViewUserRecyclerView.setAdapter(new AlphaInAnimationAdapter(scaleAdapter));
        mViewUserRecyclerView.setItemAnimator(new SlideInLeftAnimator());

        /* mViewUserView = items;

        mOtherUserAdapter = new DropAdapter(this, mViewUserView, "other");
        mViewUserRecyclerView.setAdapter(mOtherUserAdapterrAdapter);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_riple, menu);
        return true;
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
