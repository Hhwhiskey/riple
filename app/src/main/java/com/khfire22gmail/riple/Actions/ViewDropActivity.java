package com.khfire22gmail.riple.actions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.model.CommentAdapter;
import com.khfire22gmail.riple.model.CommentItem;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewDropActivity extends AppCompatActivity {


    private Object animator;
    private RecyclerView mRecyclerView;
    private List<CommentItem> mCommentList;
    private CommentAdapter mCommenterAdapter;
    private RecyclerView.Adapter mCommentAdapter;
    private String mDescription;
    private String currentDrop;
    private String mAuthorId;
    private String mAuthorName;
    private String mFacebookId;
    private String mObjectId;
    private String mRipleCount;
    private String mCommentCount;
    private Date mCreatedAt;
    private ProfilePictureView profilePictureView;
    private TextView nameView;
    private TextView descriptionView;
    private TextView ripleCountView;
    private TextView commentCountView;
    private TextView createdAtView;
    private String mDropObjectId;
    private String mAuthorFacebookId;
    private String mDropDescription;
    private ProfilePictureView authorProfilePictureView;
    private ProfilePictureView postCommentProfilePictureView;
    private String commentText;
    private AutoCompleteTextView newCommentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_drop);

        mRecyclerView = (RecyclerView) findViewById(R.id.view_drop_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Author and Drop Information
        Intent intent = getIntent();
        mDropObjectId = intent.getStringExtra("dropObjectId");
        mAuthorId = intent.getStringExtra("authorId");
        mAuthorName = intent.getStringExtra("commenterName");
        mAuthorFacebookId = intent.getStringExtra("authorFacebookId");
        mDropDescription = intent.getStringExtra("dropDescription");
        mRipleCount = intent.getStringExtra("ripleCount");
        mCommentCount = intent.getStringExtra("commentCount");
        mCreatedAt = (Date) intent.getSerializableExtra("createdAt");

        Log.d("rDropExtra", "mDropObjectId = " + mDropObjectId);
        Log.d("rDropExtra", "mAuthorId = " + mAuthorId);
        Log.d("rDropExtra", "mAuthorName = " + mAuthorName);
        Log.d("rDropExtra", "mAuthorFacebookId = " + mFacebookId);
        Log.d("rDropExtra", "mDropDescription = " + mDescription);
        Log.d("rDropExtra", "mRipleCount = " + mRipleCount);
        Log.d("rDropExtra", "mCommentCount = " + mCommentCount);
        Log.d("rDropExtra", "mCreatedAt = " + mCreatedAt);

        authorProfilePictureView = (ProfilePictureView)findViewById(R.id.profile_picture);
        authorProfilePictureView.setProfileId(mAuthorFacebookId);

        authorProfilePictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewOtherUser(mAuthorId, mAuthorName, mAuthorFacebookId);
            }
        });

        nameView = (TextView) findViewById(R.id.name);
        nameView.setText(mAuthorName);

        descriptionView = (TextView) findViewById(R.id.description);
        descriptionView.setText(mDropDescription);

        ripleCountView = (TextView) findViewById(R.id.riple_count);
        ripleCountView.setText(mRipleCount);

        commentCountView = (TextView) findViewById(R.id.comment_count);
        commentCountView.setText(mCommentCount);

        createdAtView = (TextView) findViewById(R.id.created_at);
        createdAtView.setText(String.valueOf(mCreatedAt));
        ///////////////

        //Update currentUser Commenter picture
        updateUserInfo();

        //Allows the query of the viewed drop
        currentDrop = mObjectId;

        Button postCommentButton = (Button) findViewById(R.id.button_post_comment);
        newCommentView = (AutoCompleteTextView) findViewById(R.id.enter_comment_text);

        // Allow user to input Drop
        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                commentText = newCommentView.getEditableText().toString();
                postNewComment(commentText);
            }
        });

//        int size = (int) getResources().getDimension(R.dimen.com_facebook_profilepictureview_preset_size_large);
//        profilePictureView.setPresetSize(ProfilePictureView.LARGE);

        loadCommentsFromParse();

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setItemAnimator((RecyclerView.ItemAnimator) animator);
    }

    public void loadCommentsFromParse() {
        final List<CommentItem> commentList = new ArrayList<>();

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Comments");
        query.whereEqualTo("dropId", mDropObjectId);
        query.orderByDescending("createdAt");
//        query.setLimit(25);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (e != null) {
                    Log.d("KEVIN", "error error");

                } else {
                    for (int i = 0; i < list.size(); i++) {

                        CommentItem commentItem = new CommentItem();

                        // Commenter Id
                        commentItem.setObjectId(list.get(i).getObjectId());
                        // DropId
                        commentItem.setDropId(list.get(i).getString("dropId"));
                        //Comment
                        commentItem.setCommentText(list.get(i).getString("commentText"));
                        //CommenterId
                        commentItem.setCommenterId(list.get(i).getString("commenterId"));
                        //Author name
                        commentItem.setCommenterName(list.get(i).getString("commenterName"));
                        //Picture
                        commentItem.setFacebookId(list.get(i).getString("facebookId"));
                        //Date
                        commentItem.setCreatedAt(list.get(i).getCreatedAt());

//                      dropItem.createdAt = new SimpleDateFormat("EEE, MMM d yyyy @ hh 'o''clock' a").parse("date");

                        //Drop Title
//                        dropItem.setTitle(list.get(i).getString("title"));

                        //Drop description
//                        dropItem.setDescription(list.get(i).getString("description"));

                        //Riple Count
//                        dropItem.setRipleCount(String.valueOf(list.get(i).getInt("ripleCount") + " Riples"));

                        //Comment Count
//                        dropItem.setCommentCount(String.valueOf(list.get(i).getInt("commentCount") + " Comments"));

                        //Id that connects commenterName to drop
//                              dropItem.setCommenterName(list.get(i).getString("commenterName"));

                        commentList.add(commentItem);
                    }

                    Log.i("KEVIN", "PARSE LIST SIZE: " + commentList.size());
                    updateRecyclerView(commentList);
                }
            }
        });
    }

    private void updateRecyclerView(List<CommentItem> items) {
        Log.d("KEVIN", "VIEWDROP LIST SIZE: " + items.size());

        mCommentList = items;

        mCommentAdapter = new CommentAdapter(this, mCommentList);
        mRecyclerView.setAdapter(mCommentAdapter);
    }

    public void postNewComment(String commentText){

        final ParseObject drop = new ParseObject("Drop");
        ParseObject comment = new ParseObject("Comments");
        final ParseUser user = ParseUser.getCurrentUser();

        comment.put("dropId", mDropObjectId);
        comment.put("commenterId", user.getObjectId());
        comment.put("commenterName", user.get("name"));
        comment.put("facebookId", user.get("facebookId"));
        comment.put("commentText", commentText);
        comment.saveInBackground();

        /*(new SaveCallback() {// saveInBackground first and then run relation
            @Override
            public void done(ParseException e) {
                ParseRelation<ParseObject> relation = drop.getRelation("comments");
                relation.add(drop);
                drop.saveInBackground();
            }
        });*/

    }

    private void updateUserInfo() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        String facebookId = currentUser.getString("facebookId");
        Bundle parametersPicture = new Bundle();
        parametersPicture.putString("fields", "picture.width(150).height(150)");

        if (facebookId != null) {
           postCommentProfilePictureView = (ProfilePictureView) findViewById(R.id.post_comment_profile_picture);
            postCommentProfilePictureView.setProfileId(facebookId);

        } else {
            // Show the default, blank user profile picture
            postCommentProfilePictureView.setProfileId(null);
        }
    }


    // Allow user to view the Drop's Author's profile
    private void viewOtherUser(String mAuthorId, String mAuthorName, String mAuthorFacebookId) {

        String mClickedUserId = mAuthorId;
        String mClickedUserName = mAuthorName;
        String mClickedUserFacebookId = mAuthorFacebookId;

        Log.d("sDropViewUser", "Clicked User's Id = " + mClickedUserId);
        Log.d("sDropViewUser", "Clicked User's Name = " + mClickedUserName);
        Log.d("sDropViewUser", "Clicked User's facebookId = " + mClickedUserFacebookId);

        Intent intent = new Intent(this, ViewUserActivity.class);
        intent.putExtra("clickedUserId", mClickedUserId);
        intent.putExtra("clickedUserName", mClickedUserName);
        intent.putExtra("clickedUserFacebookId", mClickedUserFacebookId);
        this.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_clicked_drop, menu);
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
