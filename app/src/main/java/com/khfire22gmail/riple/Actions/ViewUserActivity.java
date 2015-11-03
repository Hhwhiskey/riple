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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        ViewCompat.setTransitionName(findViewById(R.id.appbar), EXTRA_IMAGE);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.other_user_collapsing_tool_bar);
            collapsingToolbar.setTitle(mAuthorName);

        collapsingToolbar.setContentScrimColor(ContextCompat.getColor(this, R.color.ColorPrimary));
//        collapsingToolbar.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));

        FloatingActionButton messageFab = (FloatingActionButton) findViewById(R.id.fab_message);
        messageFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d("Message", "You are trying to send a new message");
            }
        });
            /*messageFab.setRippleColor(lightVibrantColor);
            messageFab.setBackgroundTintList(ColorStateList.valueOf(vibrantColor));*/

        Intent intent = getIntent();
        mAuthorId = intent.getStringExtra("author");
        mAuthorName = intent.getStringExtra("name");
        mFacebookId = intent.getStringExtra("facebookId");

        Log.d("extraUser", "mAuthorId = " + mAuthorId);
        Log.d("extraUser", "mAuthorName = " + mAuthorName);
        Log.d("extraUser", "mFacebookId = " + mFacebookId);


        profilePictureView = (ProfilePictureView)findViewById(R.id.other_profile_picture);
        profilePictureView.setProfileId(mFacebookId);

//        int size = (int) getResources().getDimension(R.dimen.com_facebook_profilepictureview_preset_size_large);
        profilePictureView.setPresetSize(ProfilePictureView.LARGE);


        /*currentUser = mAuthorId;
        
        JSONObject userProfile = currentUser.getJSONObject("profile");

        profilePictureView.setProfileId(userProfile.getString("facebookId"));*/



//        Using the "Extra Intent" I want to pass the clicked User info to the ViewUserActivity activity.
//        It should show that users facebook picture and name in place of the "Title"

//        updateViewsWithProfileInfo();

        mViewOtherUserRecyclerView = (RecyclerView) findViewById(R.id.other_user_recycler_view);
        mViewOtherUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadRipleItemsFromParse();

        mViewOtherUserRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        mRecyclerView.setItemAnimator(animator);


//        Set Title of Collapsable Toolbar

    }



    /*private void updateViewsWithProfileInfo() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser.has("profile")) {
            JSONObject userProfile = currentUser.getJSONObject("profile");
            try {
                String url;
                Bundle parametersPicture = new Bundle();
                parametersPicture.putString("fields", "picture.width(150).height(150)");

                if (userProfile.has("facebookId")) {
                    profilePictureView.setProfileId(userProfile.getString("facebookId"));

                } else {
                    // Show the default, blank user profile picture
                    profilePictureView.setProfileId(null);
                }

                if (userProfile.has("name")) {
                    nameView.setText(userProfile.getString("name"));
                } else {
                    nameView.setText("");
                }

            } catch (JSONException e) {
                Log.d(RipleApplication.TAG, "Error parsing saved user data.");
            }
        }
    }*/

    public void loadRipleItemsFromParse() {
        final List<DropItem> otherUserList = new ArrayList<>();
        final ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Drop");
        query1.whereEqualTo("author", mAuthorId);

        final ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Drop");
        query2.whereEqualTo("done", mAuthorId);

        List<ParseQuery<ParseObject>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);

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

                        //Id that connects commenter to drop
//                              dropItem.setCommenter(list.get(i).getString("commenter"));

                        otherUserList.add(dropItem);
                    }

                    Log.i("KEVIN", "PARSE LIST SIZE: " + otherUserList.size());
                    updateRecyclerView(otherUserList);
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
