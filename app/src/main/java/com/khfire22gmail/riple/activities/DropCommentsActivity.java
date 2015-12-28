package com.khfire22gmail.riple.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import com.khfire22gmail.riple.MainActivity;
import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.model.CommentAdapter;
import com.khfire22gmail.riple.model.CommentItem;
import com.khfire22gmail.riple.model.CompletedByAdapter;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DropCommentsActivity extends AppCompatActivity {


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
    private String mAuthorRank;
    private TextView rankView;
    private String mTabName;
    private String drop;
    private String trickle;
    private int mPosition;
    private String displayName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_drop);

        drop = "drop";
        trickle = "trickle";

        mRecyclerView = (RecyclerView) findViewById(R.id.view_drop_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Author and Drop Information
        Intent intent = getIntent();
        mDropObjectId = intent.getStringExtra("dropObjectId");
        mAuthorId = intent.getStringExtra("authorId");
        mAuthorRank = intent.getStringExtra("authorRank");
        mAuthorName = intent.getStringExtra("commenterName");
        mAuthorFacebookId = intent.getStringExtra("authorFacebookId");
        mDropDescription = intent.getStringExtra("dropDescription");
        mRipleCount = intent.getStringExtra("ripleCount");
        mCommentCount = intent.getStringExtra("commentCount");
        mCreatedAt = (Date) intent.getSerializableExtra("createdAt");
        mTabName = intent.getStringExtra("mTabName");
//        mPosition = Integer.parseInt(intent.getStringExtra("mPosition"));


        Log.d("rDropExtra", "mDropObjectId = " + mDropObjectId);
        Log.d("rDropExtra", "mAuthorId = " + mAuthorId);
        Log.d("rDropExtra", "mAuthorName = " + mAuthorName);
        Log.d("rDropExtra", "mAuthorFacebookId = " + mFacebookId);
        Log.d("rDropExtra", "mDropDescription = " + mDescription);
        Log.d("rDropExtra", "mRipleCount = " + mRipleCount);
        Log.d("rDropExtra", "mCommentCount = " + mCommentCount);
        Log.d("rDropExtra", "mCreatedAt = " + mCreatedAt);
        Log.d("rDropExtra", "mTabName = " + mTabName);

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

        rankView = (TextView) findViewById(R.id.author_rank);
        rankView.setText(mAuthorRank);

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

        ImageView menuButton = (ImageView) findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTabName.equals(drop)) {
                    showDropMenu();
                } else {
                    showTrickleMenu();
                }
            }
        });

        commentCountView = (TextView) findViewById(R.id.comment_count);
        commentCountView.setText(mCommentCount);

        createdAtView = (TextView) findViewById(R.id.comment_created_at);
        createdAtView.setText(String.valueOf(mCreatedAt));
        ///////////////

        //Update currentUser Commenter picture
        updateUserInfo();

        //Allows the query of the viewed drop
        currentDrop = mObjectId;

        final Button postCommentButton = (Button) findViewById(R.id.button_post_comment);
        newCommentView = (AutoCompleteTextView) findViewById(R.id.enter_comment_text);

        // Allow user to input Drop
        postCommentButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 ParseUser currentUser = ParseUser.getCurrentUser();
                 parseProfilePicture = (ParseFile) currentUser.get("parseProfilePicture");
                 displayName = (String) currentUser.get("displayName");

                 if (parseProfilePicture == null && displayName == null) {
                     Toast.makeText(getApplicationContext(), "Please upload a picture and set your User Name first, don't be shy :)", Toast.LENGTH_LONG).show();
                     Intent intent = new Intent(DropCommentsActivity.this, SettingsActivity.class);
                     startActivity(intent);
                 } else if (parseProfilePicture == null) {
                     Toast.makeText(getApplicationContext(), "Please upload a picture first, don't be shy :)", Toast.LENGTH_LONG).show();
                     Intent intent = new Intent(DropCommentsActivity.this, SettingsActivity.class);
                     startActivity(intent);

                 } else if (displayName == null) {
                     Toast.makeText(getApplicationContext(), "Please set your User Name first, don't be shy :)", Toast.LENGTH_LONG).show();
                     Intent intent = new Intent(DropCommentsActivity.this, SettingsActivity.class);
                     startActivity(intent);
                 } else  {
                     commentText = newCommentView.getEditableText().toString();
                     try {
                         postNewComment(commentText);
                         newCommentView.setText("");

                     } catch (InterruptedException e) {
                         e.printStackTrace();
                     }
                 }

             }
         });

        loadCommentsFromParse();
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setItemAnimator((RecyclerView.ItemAnimator) animator);
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

    private void viewCompletedBy() {

        Intent intent = new Intent(DropCommentsActivity.this, DropCompletedActivity.class);
        intent.putExtra("dropObjectId", mDropObjectId);
        intent.putExtra("authorId", mAuthorId);
        intent.putExtra("commenterName", mAuthorName);
        intent.putExtra("authorRank", mAuthorRank);
        intent.putExtra("dropDescription", mDropDescription);
        intent.putExtra("ripleCount", mRipleCount);
        intent.putExtra("commentCount", mCommentCount);
        intent.putExtra("createdAt", mCreatedAt);

        this.startActivity(intent);
    }

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

                        //Commenter data////////////////////////////////////////////////////////////
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


                        //CommenterId
                        commentItem.setCommenterId(commenterData.getObjectId());

                        //Commenter Name
                        commentItem.setCommenterName((String) commenterData.get("displayName"));

                        //Rank
                        commentItem.setCommenterRank((String) commenterData.get("userRank"));

                        //Comment Data/////////////////////////////////////////////////////////////
                        // DropId
                        commentItem.setDropId(list.get(i).getString("dropId"));

                        //Comment
                        commentItem.setCommentText(list.get(i).getString("commentText"));

                        //Date
                        commentItem.setCreatedAt(list.get(i).getCreatedAt());

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

        if(commentText != null && !commentText.isEmpty()) {
            comment.put("dropId", mDropObjectId);
            comment.put("commenterPointer", user);
            comment.put("commentText", commentText);
            comment.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Toast.makeText(getApplicationContext(), "Your comment has been posted!", Toast.LENGTH_SHORT).show();
                    loadCommentsFromParse();
                }
            });

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Drop");

            query.getInBackground(mDropObjectId, new GetCallback<ParseObject>() {
                public void done(ParseObject drop, ParseException e) {
                    if (e == null) {

                        drop.increment("commentCount");
                        drop.saveInBackground();
                    }
                }
            });

        } else {
            Toast.makeText(DropCommentsActivity.this, "Please enter some text first!", Toast.LENGTH_LONG).show();
        }

        hideSoftKeyboard();
    }

    private void updateUserInfo() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        parseProfilePicture = (ParseFile) currentUser.get("parseProfilePicture");
        Bundle parametersPicture = new Bundle();
        parametersPicture.putString("fields", "picture.width(150).height(150)");

        //get parse profile picture if exists, if not, store Facebook picture on Parse and show

        if (parseProfilePicture != null) {
            Glide.with(this)
                    .load(parseProfilePicture.getUrl())
                    .crossFade()
                    .fallback(R.drawable.ic_user_default)
                    .error(R.drawable.ic_user_default)
                    .signature(new StringSignature(UUID.randomUUID().toString()))
                    .into(commenterProfilePictureView);
        } else {
            Toast.makeText(getApplicationContext(), "Please upload a picture first, don't be shy :)", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(DropCommentsActivity.this, SettingsActivity.class);
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
        if (getCurrentFocus() != null) {
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

    public void showTrickleMenu() {

        CharSequence trickleDrop[] = new CharSequence[]{"Share", "Report"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setTitle("Drop Menu");
        builder.setItems(trickleDrop, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected) {
                if (selected == 0) {
                    //share
                } else {
                    //report
                }

            }
        });
        builder.show();
    }

    public void showDropMenu() {

        CharSequence todoDrop[] = new CharSequence[]{"Share", "Remove From Todo", "Report"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setTitle("Drop Menu");
        builder.setItems(todoDrop, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected) {
                if (selected == 0) {
                    //share
                } else if (selected == 1) {
                    getDropObject(mDropObjectId);
                } else if (selected == 2) {
                    //report
                }
            }
        });
        builder.show();
    }

    public void getDropObject(String dropObject) {
        ParseQuery<ParseObject> interactedDropQuery = ParseQuery.getQuery("Drop");
        interactedDropQuery.getInBackground(dropObject, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    removeFromTodo(object);
                }
            }
        });

    }

    public void removeFromTodo(ParseObject dropObject) {

        ParseUser user = ParseUser.getCurrentUser();

        ParseRelation<ParseObject> removeRelation1 = user.getRelation("todoDrops");
        removeRelation1.remove(dropObject);
        user.saveInBackground();

        ParseRelation<ParseObject> removeRelation2 = user.getRelation("hasRelationTo");
        removeRelation2.remove(dropObject);
        user.saveInBackground();

        Intent intent = new Intent(DropCommentsActivity.this, MainActivity.class);
        startActivity(intent);

//        DropAdapter.data.remove(mPosition);
    }
}
