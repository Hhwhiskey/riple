package com.khfire22gmail.riple.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.facebook.login.widget.ProfilePictureView;
import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.model.CommentAdapter;
import com.khfire22gmail.riple.model.CompletedByAdapter;
import com.khfire22gmail.riple.model.CommentItem;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ViewDropActivity extends AppCompatActivity {


    private Object animator;
    private RecyclerView mRecyclerView;
    private List<CommentItem> mCommentList;
    private CompletedByAdapter mCommenterAdapter;
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
    public String mDropObjectId;
    private String mAuthorFacebookId;
    private String mDropDescription;
    private ImageView authorProfilePictureView;
    private ImageView commenterProfilePictureView;
    private String commentText;
    private AutoCompleteTextView newCommentView;
    private Switch viewedDropTodoSwitch;
    private CheckBox viewedDropCompleteCheckBox;
    private ParseFile parseProfilePicture;


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

        getViewedUserProfilePicture(mAuthorId);

        commenterProfilePictureView = (ImageView) findViewById(R.id.post_comment_profile_picture);

        authorProfilePictureView = (ImageView) findViewById(R.id.profile_picture);

        authorProfilePictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewOtherUser(mAuthorId, mAuthorName);
            }
        });

        nameView = (TextView) findViewById(R.id.name);
        nameView.setText(mAuthorName);

        descriptionView = (TextView) findViewById(R.id.description);
        descriptionView.setText(mDropDescription);

        ripleCountView = (TextView) findViewById(R.id.riple_count);
        ripleCountView.setText(mRipleCount);
        ripleCountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewCompletedBy();
            }
        });

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
                try {
                    postNewComment(commentText);
                    newCommentView.setText("");
//                    generatedString = "";
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

//        int size = (int) getResources().getDimension(R.dimen.com_facebook_profilepictureview_preset_size_large);
//        profilePictureView.setPresetSize(ProfilePictureView.LARGE);

        loadCommentsFromParse();

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setItemAnimator((RecyclerView.ItemAnimator) animator);


/////////Add/Remove/Complete Drop from within ViewDrop//////////////////////////////////////////////////
        //To do Switch
//        if (viewedDropTodoSwitch != null) {
           /* viewedDropTodoSwitch = (Switch) findViewById(R.id.switch_todo);
            viewedDropTodoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        getDropObjectToAdd(mDropObjectId);
                    } else {
                        getDropObjectToRemove(mDropObjectId);
                    }
                }
            });*/
//        }

       /* // Complete CheckBox Listener
        if (completeDropButton != null) {
            completeDropButton = (Button) findViewById(R.id.button_complete);
            completeDropButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        getDropObjectToComplete(mDropObjectId);
                    }
                }
            });
        }*/
    }

    private void getViewedUserProfilePicture(String mAuthorId) {
        ParseQuery<ParseUser> viewUserQuery = ParseQuery.getQuery("_User");
        viewUserQuery.getInBackground(mAuthorId, new GetCallback<ParseUser>() {
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
                                authorProfilePictureView.setImageBitmap(bmp);
                            }
                        }
                    });
                }
            }
        });
    }

    private void viewCompletedBy(){

        Intent intent = new Intent(ViewDropActivity.this, CompletedByActivity.class);
        intent.putExtra("dropObjectId", mDropObjectId);
        intent.putExtra("authorId", mAuthorId);
        intent.putExtra("commenterName", mAuthorName);
        intent.putExtra("dropDescription", mDropDescription);
        intent.putExtra("ripleCount", mRipleCount);
        intent.putExtra("commentCount", mCommentCount);
        intent.putExtra("createdAt", mCreatedAt);
        this.startActivity(intent);
    }

    /*private void getDropObjectToAdd(String mDropObjectId) {

        ParseQuery<ParseObject> viewedDropQuery = ParseQuery.getQuery("Drop");
        viewedDropQuery.getInBackground(mDropObjectId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    todoDrop(object);
                }
            }
        });
    }

    private void getDropObjectToRemove(String mDropObjectId) {

        ParseQuery<ParseObject> viewedDropQuery = ParseQuery.getQuery("Drop");
        viewedDropQuery.getInBackground(mDropObjectId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    removeFromTodo(object);
                }
            }
        });
    }

    private void getDropObjectToComplete(String mDropObjectId) {

        ParseQuery<ParseObject> viewedDropQuery = ParseQuery.getQuery("Drop");
        viewedDropQuery.getInBackground(mDropObjectId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    completeDrop(object);
                }
            }
        });
    }

    public static void todoDrop(ParseObject viewedDropObject) {

        ParseUser user = ParseUser.getCurrentUser();

        ParseRelation<ParseObject> todoRelation1 = user.getRelation("todoDrops");
        todoRelation1.add(viewedDropObject);
        user.saveInBackground();

        ParseRelation<ParseObject> todoRelation2 = user.getRelation("hasRelationTo");
        todoRelation2.add(viewedDropObject);
        user.saveInBackground();
    }

    public void removeFromTodo(ParseObject viewedDropObject) {

        ParseUser user = ParseUser.getCurrentUser();

        ParseRelation<ParseObject> removeRelation1 = user.getRelation("todoDrops");
        removeRelation1.remove(viewedDropObject);
        user.saveInBackground();

        ParseRelation<ParseObject> removeRelation2 = user.getRelation("hasRelationTo");
        removeRelation2.remove(viewedDropObject);
        user.saveInBackground();
    }

    public void completeDrop(ParseObject viewedDropObject) {

        //Increment the user
        ParseUser user = ParseUser.getCurrentUser();
        user.increment("userRipleCount");
        user.saveInBackground();

        //Increment the Drop
        viewedDropObject.increment("ripleCount");
        viewedDropObject.saveInBackground();

        ParseRelation completeRelation1 = user.getRelation("completedDrops");
        completeRelation1.add(viewedDropObject);

        ParseRelation completeRelation2 = user.getRelation("todoDrops");
        completeRelation2.remove(viewedDropObject);
        user.saveInBackground();

        ParseRelation completeRelation3 = user.getRelation("hasRelationTo");
        completeRelation3.add(viewedDropObject);
        user.saveInBackground();

        //Todo Add completed timestamp and update the data on parse
       *//* Date date = new Date();
        Long time = (date.getTime());*//*
    }*/
/////////////////////////////////////////////////////////////////////


    public void loadCommentsFromParse() {
        final List<CommentItem> commentList = new ArrayList<>();

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Comments");
        query.whereEqualTo("dropId", mDropObjectId);
        query.orderByDescending("createdAt");
        query.include("commenterPointer");
//        query.setLimit(25);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (e != null) {
                    Log.d("KEVIN", "error error");

                } else {
                    for (int i = 0; i < list.size(); i++) {

                        final CommentItem commentItem = new CommentItem();

                        ParseObject commenterData = (ParseObject) list.get(i).get("commenterPointer");

                        ParseFile profilePicture = (ParseFile) commenterData.get("parseProfilePicture");
                        if (profilePicture != null) {
                            profilePicture.getDataInBackground(new GetDataCallback() {

                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null) {
                                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//                                        Bitmap resized = Bitmap.createScaledBitmap(bmp, 100, 100, true);
                                       commentItem.setParseProfilePicture(bmp);
                                        updateRecyclerView(commentList);
                                    }
                                }
                            });
                        }

                        // Comment Id
//                        commentItem.setObjectId(list.get(i).getObjectId());

                        // DropId
                        commentItem.setDropId(list.get(i).getString("dropId"));

                        //CommenterId
                        commentItem.setCommenterId(commenterData.getObjectId());

                        //Commenter
                        commentItem.setCommenterName((String) commenterData.get("displayName"));

                        //Commenter Picture
//                        commentItem.setCommenterFacebookId(list.get(i).getString("commenterFacebookId"));

                        //Comment
                        commentItem.setCommentText(list.get(i).getString("commentText"));

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

    public void postNewComment(final String commentText) throws InterruptedException {

        final ParseUser user = ParseUser.getCurrentUser();
        final ParseObject comment = new ParseObject("Comments");

        if (commentText != null) {
            comment.put("dropId", mDropObjectId);
            comment.put("commenterPointer", user);
//            comment.put("commenterId", user.getObjectId());
//            comment.put("commenterName", user.get("username"));
//            comment.put("commenterProfilePicture", user.getParseFile("parseProfilePicture"));
            comment.put("commentText", commentText);
            comment.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Toast.makeText(getApplicationContext(), "Your comment has been posted!", Toast.LENGTH_SHORT).show();

                    recreate();
                }
            });
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Drop");

        query.getInBackground(mDropObjectId, new GetCallback<ParseObject>() {
            public void done(ParseObject drop, ParseException e) {
                if (e == null) {

                    drop.increment("commentCount");
                    drop.saveInBackground();
                }
            }
        });

        hideSoftKeyboard();
    }

    private void updateUserInfo() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        parseProfilePicture = (ParseFile) currentUser.get("parseProfilePicture");
        Bundle parametersPicture = new Bundle();
        parametersPicture.putString("fields", "picture.width(150).height(150)");

        //get parse profile picture if exists, if not, store Facebook picture on Parse and show

        if(parseProfilePicture != null) {
            Glide.with(this)
                    .load(parseProfilePicture.getUrl())
                    .crossFade()
                    .fallback(R.drawable.ic_user_default)
                    .error(R.drawable.ic_user_default)
                    .signature(new StringSignature(UUID.randomUUID().toString()))
                    .into(commenterProfilePictureView);
        } else {
            Toast.makeText(getApplicationContext(), "Please upload a picture first, don't be shy :)",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ViewDropActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
    }


    // Allow user to view the Drop's Author's profile
    private void viewOtherUser(String mAuthorId, String mAuthorName) {

        String mClickedUserId = mAuthorId;
        String mClickedUserName = mAuthorName;

        Log.d("sDropViewUser", "Clicked User's Id = " + mClickedUserId);
        Log.d("sDropViewUser", "Clicked User's Name = " + mClickedUserName);

        Intent intent = new Intent(this, ViewUserActivity.class);
        intent.putExtra("clickedUserId", mClickedUserId);
        intent.putExtra("clickedUserName", mClickedUserName);
        this.startActivity(intent);
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
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
