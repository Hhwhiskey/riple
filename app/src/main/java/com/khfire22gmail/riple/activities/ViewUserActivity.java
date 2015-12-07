package com.khfire22gmail.riple.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.model.DropAdapter;
import com.khfire22gmail.riple.model.DropItem;
import com.khfire22gmail.riple.sinch.MessagingActivity;
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

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;

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

    // Added this to track current User's drop stuff
    private ArrayList<DropItem> mCurrentUserDrops = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        ViewCompat.setTransitionName(findViewById(R.id.appbar_view_user), EXTRA_IMAGE);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.view_user_collapsing_tool_bar);
        collapsingToolbar.setContentScrimColor(ContextCompat.getColor(this, R.color.ColorPrimary));

        loadCurrentUserRipleItemsFromParse();

        //Receive extra intent information to load clicked user's profile
        Intent intent = getIntent();

        mClickedUserId = intent.getStringExtra("clickedUserId");
        mClickedUserName = intent.getStringExtra("clickedUserName");

        Log.d("rViewUser", "mClickedUserId = " + mClickedUserId);
        Log.d("rViewUser", "mClickedUserName = " + mClickedUserName);


        getViewedUserProfilePicture(mClickedUserId);

        // Set collapsable toolbar picture and text
        profilePictureView = (ImageView)findViewById(R.id.view_user_profile_picture);

        collapsingToolbar.setTitle(mClickedUserName);

        mViewUserRecyclerView = (RecyclerView) findViewById(R.id.view_user_recycler_view);
        mViewUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mViewUserRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        mRecyclerView.setItemAnimator(animator);

        FloatingActionButton messageFab = (FloatingActionButton) findViewById(R.id.fab_message);
        messageFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(ViewUserActivity.this, MessagingActivity.class);
                intent.putExtra("RECIPIENT_ID", mClickedUserId);
                startActivity(intent);
            }
        });
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
        final ArrayList<DropItem> clickedUsersList = new ArrayList<>();

        ParseUser clickedUser = null;
        ParseQuery<ParseUser> getClickedUserquery = ParseQuery.getQuery("_User");
        getClickedUserquery.whereEqualTo("objectId", mClickedUserId);

        // TODO: Change this to findInBackground and pass in a callback to listen to when this inBackground finishes
       /* getClickedUserquery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {*/

        try {
            if (getClickedUserquery.find().size() != 0) {
                clickedUser = getClickedUserquery.find().get(0);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

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

                        clickedUsersList.add(dropItem);
                    }

//                    Log.i("KEVIN", "PARSE LIST SIZE: " + clickedUsersList.size());
                    // Don't update this until we have the list of the current user's stuff as well
                    // Then, we'll have the relations that we need to display the proper card.
                    updateRecyclerView(clickedUsersList);
                }
            }
        });
    }


    public void loadCurrentUserRipleItemsFromParse() {

        final ArrayList<DropItem> currentUsersList = new ArrayList<>();

        ParseUser clickedUser = null;
/*        ParseQuery<ParseUser> getClickedUserquery = ParseQuery.getQuery("_User");
        getClickedUserquery.whereEqualTo("objectId", mClickedUserId);

        // TODO: Change this to findInBackground and pass in a callback to listen to when this inBackground finishes
       *//* getClickedUserquery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {*//*

        try {
            if (getClickedUserquery.find().size() != 0) {
                clickedUser = getClickedUserquery.find().get(0);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }*/

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
                    loadRipleItemsFromParse();
                }
            }
        });
    }





    private void updateRecyclerView(ArrayList<DropItem> clickedUserList) {
        Log.d("VIEWUSERLIST", "CLICKED USER LIST SIZE: " + clickedUserList.size());
        Log.d("VIEWUSERLIST", "CURRENT USER LIST SIZE: " + mCurrentUserDrops.size());

        mViewUserAdapter = new DropAdapter(this, clickedUserList, "viewUser");
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
