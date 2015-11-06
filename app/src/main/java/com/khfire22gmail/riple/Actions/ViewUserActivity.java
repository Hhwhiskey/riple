package com.khfire22gmail.riple.actions;

import android.content.Intent;
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

public class ViewUserActivity extends AppCompatActivity {

    private Object animator;
    private RecyclerView mViewOtherUserRecyclerView;
    private DropAdapter mOtherUserAdapter;
    private List<DropItem> mOtherUserList;
    String EXTRA_IMAGE;

    public String mAuthorId;
    public String mAuthorName;
    public String mFacebookId;
    private ProfilePictureView otherProfilePictureView;
    private TextView otherNameView;
    private ProfilePictureView profilePictureView;
    private TextView nameView;
    private String clickedUser;
    private Object currentUser;
    private String objectId;
    private String mClickedUserParseId;
    private String mClickedUserId;
    private String mClickedUserName;
    private String mClickedUserFacebookId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        ViewCompat.setTransitionName(findViewById(R.id.appbar), EXTRA_IMAGE);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.view_user_collapsing_tool_bar);
        collapsingToolbar.setTitle(mAuthorName);

        collapsingToolbar.setContentScrimColor(ContextCompat.getColor(this, R.color.ColorPrimary));
//        collapsingToolbar.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));

        FloatingActionButton messageFab = (FloatingActionButton) findViewById(R.id.fab_message);
        messageFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d("Message", "You are trying to send a new message");
            }
        });

        Intent intent = getIntent();

        mClickedUserId = intent.getStringExtra("clickedUserId");
        mClickedUserName = intent.getStringExtra("clickedUserName");
        mClickedUserFacebookId = intent.getStringExtra("clickedUserFacebookId");

        Log.d("rViewUserAcitivty", "mClickedUserId = " + mClickedUserId);
        Log.d("rViewUserAcitivty", "mClickedUserName = " + mClickedUserName);
        Log.d("rViewUserAcitivty", "mClickedUserFacebookId = " + mClickedUserFacebookId);

        // Set collapsable toolbar picture and text
        profilePictureView = (ProfilePictureView)findViewById(R.id.other_profile_picture);
        profilePictureView.setProfileId(mClickedUserFacebookId);
        collapsingToolbar.setTitle(mClickedUserName);

//        int size = (int) getResources().getDimension(R.dimen.com_facebook_profilepictureview_preset_size_large);
        profilePictureView.setPresetSize(ProfilePictureView.LARGE);

        mViewOtherUserRecyclerView = (RecyclerView) findViewById(R.id.other_user_recycler_view);
        mViewOtherUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadRipleItemsFromParse();

        mViewOtherUserRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        mRecyclerView.setItemAnimator(animator);
    }

    public void loadRipleItemsFromParse() {
        final List<DropItem> ripleList = new ArrayList<>();

        // this block of code is equivalent to some function that findUserById(ParseUserId)
        ParseUser clickedUser = null;
        ParseQuery<ParseUser> getClickedUserquery = ParseQuery.getQuery("_User");
        getClickedUserquery.whereEqualTo("objectId", mClickedUserId);

        // TODO: Change this to findInBackground and pass in a callback to listen to when this inBackground finishes
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

                        DropItem dropItem = new DropItem();

                        //ObjectId
                        dropItem.setObjectId(list.get(i).getObjectId());
                        //Picture
                        dropItem.setFacebookId(list.get(i).getString("facebookId"));
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

                        //Id that connects authorName to drop
//                              dropItem.setAuthorName(list.get(i).getString("authorName"));

                        ripleList.add(dropItem);
                    }

                    Log.i("KEVIN", "PARSE LIST SIZE: " + ripleList.size());
                    updateRecyclerView(ripleList);
                }
            }
        });
    }

    private void updateRecyclerView(List<DropItem> items) {
        Log.d("KEVIN", "RIPLE LIST SIZE: " + items.size());

        mOtherUserList = items;

        mOtherUserAdapter = new DropAdapter(this, mOtherUserList, "other");
        mViewOtherUserRecyclerView.setAdapter(mOtherUserAdapter);
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
