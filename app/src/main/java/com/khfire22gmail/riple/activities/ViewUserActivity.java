package com.khfire22gmail.riple.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.khfire22gmail.riple.utils.Constants;
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

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;


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
    private String mClickedUserRank;
    private String mClickedUserRipleCount;
    private TextView authorRipleRank;
    private TextView authorRipleCount;
    private String stringTestVariable;
    private ArrayList<DropItem> mViewUserDropList;
    private String mClickedUserInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        mViewUserDropList = new ArrayList<DropItem>();

        ViewCompat.setTransitionName(findViewById(R.id.appbar_view_user), EXTRA_IMAGE);

        // Instantiate name, rank, riple count and empty TVs, RV and layout manager
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.view_user_collapsing_tool_bar);
        collapsingToolbar.setContentScrimColor(ContextCompat.getColor(this, R.color.ColorPrimary));

        profilePictureView = (ImageView) findViewById(R.id.view_user_profile_picture);
        authorRipleRank = (TextView) findViewById(R.id.view_user_rank);
        authorRipleCount = (TextView) findViewById(R.id.view_user_ripleCount);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mViewUserRecyclerView = (RecyclerView) findViewById(R.id.view_user_recycler_view);
        mViewUserRecyclerView.setLayoutManager(layoutManager);
        mViewUserRecyclerView.setItemAnimator(new DefaultItemAnimator());
        viewUserEmptyView = (TextView) findViewById(R.id.view_user_empty_view);
        ////////////////////////////////////////////////////////////////////////////


        //Receive extra intent information to load clicked user's profile
        Intent intent = getIntent();
        mClickedUserId = intent.getStringExtra(Constants.CLICKED_USER_ID);
        mClickedUserName = intent.getStringExtra(Constants.CLICKED_USER_NAME);
        mClickedUserRank = intent.getStringExtra(Constants.CLICKED_USER_RANK);
        mClickedUserRipleCount = intent.getStringExtra(Constants.CLICKED_USER_RIPLE_COUNT);
        mClickedUserInfo = intent.getStringExtra(Constants.CLICKED_USER_INFO);
        Log.d("rViewUser", "mClickedUserId = " + mClickedUserId);
        Log.d("rViewUser", "mClickedUserName = " + mClickedUserName);
        Log.d("rViewUser", "mClickedUserRipleCount = " + mClickedUserRipleCount);
        Log.d("rViewUser", "mClickedUserRank = " + mClickedUserRank);


        //Populate the viewed users data////////////////////////////////////

        //Get viewedUsers parseProfilePicture and set it to imageView
        getViewedUserProfilePicture(mClickedUserId);

        //Set the name, rank and riple count of the viewed user
        collapsingToolbar.setTitle(mClickedUserName);
        authorRipleRank.setText(mClickedUserRank);

        stringTestVariable = String.valueOf(1);

        //If ripleCount == stringTestVariable(1)
        if (mClickedUserRipleCount.equals(stringTestVariable)) {
            authorRipleCount.setText(mClickedUserRipleCount + " Riple");
        } else {
            authorRipleCount.setText(mClickedUserRipleCount + " Riples");
        }
        ////////////////////////////////////////////////////////////////////

        LoadUserActivityFromParse onCreateQuery = new LoadUserActivityFromParse();
        onCreateQuery.runLoadUserActivityFromParse();

        //Set hero image OCL
        profilePictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewUserInfo(mClickedUserName, mClickedUserInfo);
            }
        });

       //Set FAB onClick for messaging the user
        FloatingActionButton messageFab = (FloatingActionButton) findViewById(R.id.fab_message);
        messageFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(ViewUserActivity.this, MessagingActivity.class);
                intent.putExtra("RECIPIENT_ID", mClickedUserId);
                startActivity(intent);
            }
        });

        mViewUserRecyclerView.addOnScrollListener(new EndlessRecyclerViewOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                LoadUserActivityFromParse onCreateQuery = new LoadUserActivityFromParse(current_page);
                onCreateQuery.runLoadUserActivityFromParse();
            }
        });

        showUserTips();
    }

    public void showUserTips() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean test = sharedPreferences.getBoolean("viewUserTips", true);

        if (test) {
            viewUserTip();
        }
    }

    public void saveTipPreferences(String key, Boolean value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.putBoolean("allTipsBoolean", false);
        editor.commit();

//        MainActivity mainActivity = new MainActivity();
//        mainActivity.isBoxChecked(false);
    }

    public void viewUserTip() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewUserActivity.this, R.style.MyAlertDialogStyle);

        builder.setTitle("View Riple");
        builder.setMessage("Here, you can view other users Riple activity. View all the Drops" +
                " they have created and completed. Feel free to send them a message as well.");

        builder.setNegativeButton("HIDE THIS TIP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveTipPreferences("viewUserTips", false);
            }
        });

        builder.setPositiveButton("KEEP THIS AROUND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    //View users info
    public void viewUserInfo(String userName, String userInfo ) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ViewUserActivity.this, R.style.MyAlertDialogStyle);
        //Set title to users name
        builder.setTitle(userName);
        //If user has message show it, otherwise show default note
        if (userInfo.equals("") || userInfo == null) {
            builder.setMessage("This user has not added any information yet.");
        } else {
            builder.setMessage(userInfo);
        }

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
                if (e != null) {
                } else {
                    ParseFile viewUserProfilePicture = (ParseFile) clickedUserObject.get("parseProfilePicture");
                    parseProfilePicture = viewUserProfilePicture;
                    if (parseProfilePicture != null) {
                        parseProfilePicture.getDataInBackground(new GetDataCallback() {

                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null) {
                                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    Bitmap resized = Bitmap.createScaledBitmap(bmp, 500, 500, true);
                                    profilePictureView.setImageBitmap(resized);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public class LoadUserActivityFromParse {

        //The passed in refresh boolean, defaults to false
//        public boolean refresh = false;
        //The passed in pageNumber, defaults to 0
        public int pageNumber = 0;
        //The limit of Drop Objects to get from Parse
        public int queryLimit = 10;
        //The amount of Drop Objects to skip from Parse
        public int skipNumber = 0;

        //Default constructor for onCreate query
        public LoadUserActivityFromParse() {
        }

//        //Refresh constructor for pull to refresh query
//        public LoadUserActivityFromParse(boolean refresh) {
//            this.refresh = refresh;
//        }

        //Page constuctor for onScroll query
        public LoadUserActivityFromParse(int pageNumber) {
            this.pageNumber = pageNumber;
        }

        public void runLoadUserActivityFromParse() {

            if (pageNumber != 0) {
                int pageMultiplier = pageNumber - 1;
                skipNumber = pageMultiplier * queryLimit;
                // Otherwise, clear the list, because this is a default(refresh) query
            }else {
                mViewUserDropList.clear();
            }

            ParseQuery viewedUserQuery = ParseQuery.getQuery("_User");
            viewedUserQuery.whereEqualTo("objectId", mClickedUserId);
            viewedUserQuery.getFirstInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser viewedUser, ParseException e) {

                    if (e != null) {
                        Log.i("KEVIN", "error error" + e);
                    } else {

                        ParseRelation createdRelation = viewedUser.getRelation("createdDrops");
                        ParseRelation completedRelation = viewedUser.getRelation("completedDrops");

                        ParseQuery createdQuery = createdRelation.getQuery();
                        ParseQuery completedQuery = completedRelation.getQuery();

                        List<ParseQuery<ParseObject>> queries = new ArrayList<>();
                        queries.add(createdQuery);
                        queries.add(completedQuery);

                        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
                        mainQuery.include("authorPointer");
                        mainQuery.orderByDescending("createdAt");
                        mainQuery.setSkip(skipNumber);
                        mainQuery.setLimit(queryLimit);
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
                                                        Bitmap resized = Bitmap.createScaledBitmap(bmp, 100, 100, true);
                                                        dropItem.setParseProfilePicture(resized);

                                                        if (pageNumber != 0) {
                                                            mViewUserAdapter.notifyDataSetChanged();
                                                        } else {
                                                            updateRecyclerView(mViewUserDropList);
                                                        }
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
                                        //Author RipleCount
                                        dropItem.setAuthorRipleCount(String.valueOf(authorData.getInt("userRipleCount")));
                                        //Author Info
                                        dropItem.setAuthorInfo(authorData.getString("userInfo"));

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


                                        mViewUserDropList.add(dropItem);
                                    }
                                }
                            }
                        });
                    }
                }

            });
        }
    }

    private void updateRecyclerView(ArrayList<DropItem> clickedUserList) {
        Log.d("VIEWUSERLIST", "CLICKED USER LIST SIZE: " + clickedUserList.size());

        if (clickedUserList.isEmpty()) {
            mViewUserRecyclerView.setVisibility(View.GONE);
            viewUserEmptyView.setVisibility(View.VISIBLE);
        } else {
            mViewUserRecyclerView.setVisibility(View.VISIBLE);
            viewUserEmptyView.setVisibility(View.GONE);
        }

        // Alpha animation
        mViewUserAdapter = new DropAdapter(this, clickedUserList, "riple");
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mViewUserAdapter);
        mViewUserRecyclerView.setAdapter(alphaAdapter);
        alphaAdapter.setDuration(1000);

        // Alpha and scale animation
//        mViewUserAdapter = new DropAdapter(this, clickedUserList, "riple");
//        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(mViewUserAdapter);
//        scaleAdapter.setDuration(250);
//        mViewUserRecyclerView.setAdapter(new AlphaInAnimationAdapter(scaleAdapter));
//        mViewUserRecyclerView.setItemAnimator(new SlideInLeftAnimator());
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
