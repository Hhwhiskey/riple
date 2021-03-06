package com.khfire22.riple.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.khfire22.riple.MainActivity;
import com.khfire22.riple.R;
import com.khfire22.riple.ViewPagers.DropPagerAdapter;
import com.khfire22.riple.model.CommentItem;
import com.khfire22.riple.model.CompletedByAdapter;
import com.khfire22.riple.utils.Constants;
import com.khfire22.riple.utils.SaveToSharedPrefs;
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

import java.util.List;

public class ViewDropActivity extends AppCompatActivity {


    private Object animator;
    private RecyclerView mRecyclerView;
    private List<CommentItem> mCommentList;
    private CompletedByAdapter mCommenterAdapter;
    private RecyclerView.Adapter mCommentAdapter;
    private String currentDrop;
    private String mAuthorId;
    private String mAuthorName;
    private String mFacebookId;
    private String mObjectId;
    private String mRipleCount;
    private String mCommentCount;
    private String mCreatedAt;
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
    private ParseFile parseProfilePicture;
    private String mAuthorRank;
    private TextView rankView;
    private String mTabName;
    private String drop;
    private String trickle;
    private int mPosition;
    private String displayName;
    Context mContext;
    private ParseUser currentUser;
    private String mAuthorRipleCount;
    private String mAuthorInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_drop);

        // Get instance of ViewPager and set viewPager adapter
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new DropPagerAdapter(getSupportFragmentManager(), ViewDropActivity.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        currentUser = ParseUser.getCurrentUser();

        drop = "drop";
        trickle = "trickle";

        // Author and Drop Information
        Intent intent = getIntent();
        mDropObjectId = intent.getStringExtra("dropObjectId");
        mAuthorId = intent.getStringExtra("authorId");
        mAuthorName = intent.getStringExtra("commenterName");
        mAuthorRank = intent.getStringExtra("authorRank");
        mAuthorRipleCount = intent.getStringExtra("clickedUserRipleCount");
        mAuthorInfo = intent.getStringExtra("clickedUserInfo");
        mAuthorFacebookId = intent.getStringExtra("authorFacebookId");
        mDropDescription = intent.getStringExtra("dropDescription");
        mRipleCount = intent.getStringExtra("ripleCount");
        mCommentCount = intent.getStringExtra("commentCount");
        mCreatedAt = (String) intent.getSerializableExtra("createdAt");
        mTabName = intent.getStringExtra("mTabName");

        Log.d("rDropExtra", "mDropObjectId = " + mDropObjectId);
        Log.d("rDropExtra", "mAuthorId = " + mAuthorId);
        Log.d("rDropExtra", "mAuthorName = " + mAuthorName);
        Log.d("rDropExtra", "mAuthorRipleCount = " + mAuthorRipleCount);
        Log.d("rDropExtra", "mAuthorFacebookId = " + mFacebookId);
        Log.d("rDropExtra", "mDropDescription = " + mDropDescription);
        Log.d("rDropExtra", "mRipleCount = " + mRipleCount);
        Log.d("rDropExtra", "mCommentCount = " + mCommentCount);
        Log.d("rDropExtra", "mCreatedAt = " + mCreatedAt);
        Log.d("rDropExtra", "mTabName = " + mTabName);

        //Get author profile picture and set it to TV
        getAuthorProfilePicture(mAuthorId);

        //Set toolbar onClick
        Toolbar toolBar = (Toolbar) findViewById(R.id.tool_bar);
        toolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewOtherUser(mAuthorId, mAuthorName);
            }
        });
        //Set toolbar onLongClick
        toolBar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibrator vb = (Vibrator) ViewDropActivity.this.getSystemService(ViewDropActivity.VIBRATOR_SERVICE);
                vb.vibrate(5);
                if (mTabName.equals(drop)) {
                    showDropMenu();
                } else {
                    showTrickleMenu();
                }
                return false;
            }
        });
        //Set author picture OnClick
        authorProfilePictureView = (ImageView) findViewById(R.id.other_profile_picture);
        authorProfilePictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewOtherUser(mAuthorId, mAuthorName);
            }
        });
        //Set overflow OnClick
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

        //Set author name on Drop card
        nameView = (TextView) findViewById(R.id.name);
        nameView.setText(mAuthorName);
        //Set author rank on Drop card
        rankView = (TextView) findViewById(R.id.author_rank);
        rankView.setText(mAuthorRank);
        //Set drop descroption on Drop card
        descriptionView = (TextView) findViewById(R.id.description);
        descriptionView.setText(mDropDescription);
        //Set createdAt on Drop card
        createdAtView = (TextView) findViewById(R.id.comment_created_at);
        createdAtView.setText(String.valueOf(mCreatedAt));
        ///////////////

        //Allows the query of the viewed drop
        currentDrop = mObjectId;

        showUserTips();
    }

    public void showUserTips() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean test = sharedPreferences.getBoolean("viewDropTips", true);

        if (test) {
            viewUserTip();
        }
    }

    public void viewUserTip() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewDropActivity.this, R.style.MyAlertDialogStyle);

        builder.setTitle("View Drop");
        builder.setMessage(R.string.view_drop_tip);

        builder.setNegativeButton("HIDE THIS TIP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SaveToSharedPrefs saveToSharedPrefs = new SaveToSharedPrefs();
                saveToSharedPrefs.saveBooleanPreferences(ViewDropActivity.this, "viewDropTips", false);
            }
        });

        builder.setPositiveButton("KEEP THIS AROUND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    private void getAuthorProfilePicture(String mAuthorId) {
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
                                if (bmp != null) {
                                    Bitmap resized = Bitmap.createScaledBitmap(bmp, 100, 100, true);
                                    authorProfilePictureView.setImageBitmap(resized);
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    // Allow user to view the Drop's Author's profile
    private void viewOtherUser(String mAuthorId, String mAuthorName) {

        Intent intent = new Intent(this, ViewUserActivity.class);
        intent.putExtra(Constants.CLICKED_USER_ID, mAuthorId);
        intent.putExtra(Constants.CLICKED_USER_NAME, mAuthorName);
        intent.putExtra(Constants.CLICKED_USER_RANK, mAuthorRank);
        intent.putExtra(Constants.CLICKED_USER_RIPLE_COUNT, mAuthorRipleCount);
        intent.putExtra(Constants.CLICKED_USER_INFO, mAuthorInfo);

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

    private void messageTheAuthor() {

        String author = mAuthorId;

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", author);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> user, ParseException e) {
                if (e == null) {
                    Intent messageIntent = new Intent(ViewDropActivity.this, MessagingActivity.class);
                    messageIntent.putExtra("RECIPIENT_ID", user.get(0).getObjectId());
                    startActivity(messageIntent);
                } else {
                    Toast.makeText(mContext,
                            "Error finding that user",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //Report the Drop author and store the Drop in question in UserReportCount table
    public void reportDropAuthor() {

        final String dropObjectId = mDropObjectId;

        //Get Drop data, which includes the Author pointer
        ParseQuery parseQuery = ParseQuery.getQuery("Drop");
        parseQuery.whereEqualTo("objectId", dropObjectId);
        parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject dropObject, ParseException e) {
                if (e == null) {
                    //Get author pointer out of Drop
                    final ParseObject reportedDropAuthorPointer = dropObject.getParseObject("authorPointer");

                    //Get author for report
                    ParseQuery reportQuery = ParseQuery.getQuery("UserReportCount");
                    reportQuery.whereEqualTo("userPointer", reportedDropAuthorPointer);
                    reportQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(final ParseObject reportedUser, ParseException e) {
                            //Increment the author's report count and save mark the Drop in
                            ParseRelation reportRelation = reportedUser.getRelation("reportedDrops");
                            reportRelation.add(dropObject);
                            reportedUser.increment("reportCount");
                            reportedUser.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    Toast.makeText(ViewDropActivity.this, "The author has been reported. Thank you for keeping Riple safe!", Toast.LENGTH_LONG).show();

                                }
                            });
                        }
                    });
                }
            }
        });
    }

    public void shareToFacebook() {
        String displayName = currentUser.getString("displayName");
        String shareAuthor = mAuthorName;
        String shareDescription = mDropDescription;
//        Bitmap sharedImage = data.get(position).getCommenterParseProfilePicture();

        ShareDialog shareDialog;
        FacebookSdk.sdkInitialize(this);
        shareDialog = new ShareDialog(this);

        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle(displayName + " shared " + shareAuthor + "'s" + " Drop from \"Riple\".")
                .setContentDescription(shareDescription)
                .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.khfire22.riple"))
                .setImageUrl(Uri.parse("https://scontent-ord1-1.xx.fbcdn.net/hphotos-xap1/v/t1.0-9/923007_665600676799506_1701143490_n.png?oh=9a224427d5c5807ed0db56582363057b&oe=57079C4E"))
                .build();

        shareDialog.show(linkContent);
    }

    // TODO: 11/19/2015 Include pics with share
    public void shareWithOther() {
        String displayName = currentUser.getString("displayName");
        String shareAuthor = mAuthorName;
        String shareDescription = mDropDescription;
//        Bitmap sharedImage = data.get(position).getCommenterParseProfilePicture();

        //Share to Other
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, (displayName + " shared " + shareAuthor + "'s" + " Drop from \"Riple\":\n\n" + "\"" + shareDescription + "\"\n\n" + "If you have an Android device you can download \"Riple\" now and start making Riples of your own. Click the link to get started!\n" + "https://play.google.com/store/apps/details?id=com.khfire22.riple"));
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//      Uri imageUri = Uri.parse("https://scontent-ord1-1.xx.fbcdn.net/hphotos-xap1/v/t1.0-9/923007_665600676799506_1701143490_n.png?oh=9a224427d5c5807ed0db56582363057b&oe=57079C4E");
//      shareIntent.setType("*/*");
//      shareIntent.putExtra(Intent.EXTRA_STREAM, String.valueOf(imageUri));
//      shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, "Share this Drop with friends"));


    }

    // If the viewed Drop is not on your to-do list, show this menu
    public void showTrickleMenu() {

        CharSequence trickleDrop[] = new CharSequence[]{"Message the Author", "Share with Facebook", "Share", "Report"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setTitle("Drop Menu");
        builder.setItems(trickleDrop, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected) {
                if (selected == 0) {
                    messageTheAuthor();
                }else if (selected == 1) {
                    shareToFacebook();

                } else if (selected == 2) {
                    shareWithOther();

                }else if (selected == 3) {
                    final AlertDialog.Builder builderVerify = new AlertDialog.Builder(ViewDropActivity.this, R.style.MyAlertDialogStyle);
                    builderVerify.setTitle("Report Drop Author");
                    builderVerify.setMessage("Would you say this Drop contains spam or inappropriate/offensive material?");
                    builderVerify.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    builderVerify.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            reportDropAuthor();
                        }
                    });
                    builderVerify.show();
                }
            }
        });
        builder.show();
    }

    //If the viewed Drop is on your to-do list, show this menu
    public void showDropMenu() {

        CharSequence todoDrop[] = new CharSequence[]{"Message the Author", "Share with Facebook", "Share", "Remove From Todo", "Report"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ViewDropActivity.this, R.style.MyAlertDialogStyle);
        builder.setTitle("Drop Menu");
        builder.setItems(todoDrop, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected) {

                if (selected == 0) {
                    messageTheAuthor();

                }else if (selected == 1) {
                    shareToFacebook();

                }else if (selected == 2) {
                    shareWithOther();

                } else if (selected == 3) {
                    getDropObject(mDropObjectId);

                } else if (selected == 4) {
                    final AlertDialog.Builder builderVerify = new AlertDialog.Builder(ViewDropActivity.this, R.style.MyAlertDialogStyle);
                    builderVerify.setTitle("Report Drop Author");
                    builderVerify.setMessage("Would you say this Drop contains spam or inappropriate/offensive material?");
                    builderVerify.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    builderVerify.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            reportDropAuthor();
                        }
                    });
                    builderVerify.show();

                }
            }
        });
        builder.show();
    }

    public void getDropObject(String dropObjectId) {
        ParseQuery<ParseObject> interactedDropQuery = ParseQuery.getQuery("Drop");
        interactedDropQuery.getInBackground(dropObjectId, new GetCallback<ParseObject>() {
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

        Intent intent = new Intent(ViewDropActivity.this, MainActivity.class);
        startActivity(intent);

//        DropAdapter.data.remove(mPosition);
    }
}
