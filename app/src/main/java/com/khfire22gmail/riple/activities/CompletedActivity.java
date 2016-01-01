package com.khfire22gmail.riple.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.khfire22gmail.riple.MainViewPager.DropPagerAdapter;
import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.model.CompletedByAdapter;
import com.khfire22gmail.riple.model.CompletedByItem;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;


public class CompletedActivity extends AppCompatActivity {

    private String mDropObjectId;
    private String mAuthorId;
    private String mAuthorName;
    private String mDropDescription;
    private String mRipleCount;
    private String mCommentCount;
    private Date mCreatedAt;
    private CompletedByAdapter mCompletedByAdapter;
    private RecyclerView mRecyclerView;
    private ParseFile parseProfilePicture;
    private TextView nameView;
    private TextView descriptionView;
    private TextView ripleCountView;
    private TextView commentCountView;
    private TextView createdAtView;
    private ImageView authorProfilePicture;
    private String mAuthorRank;
    private TextView mAuthorRankView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_drop);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new DropPagerAdapter(getSupportFragmentManager(), CompletedActivity.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        Intent intent = getIntent();
        mDropObjectId = intent.getStringExtra("dropObjectId");
        mAuthorId = intent.getStringExtra("authorId");
        mAuthorName = intent.getStringExtra("commenterName");
        mAuthorRank = intent.getStringExtra("authorRank");
        mDropDescription = intent.getStringExtra("dropDescription");
        mRipleCount = intent.getStringExtra("ripleCount");
        mCommentCount = intent.getStringExtra("commentCount");
        mCreatedAt = (Date) intent.getSerializableExtra("createdAt");

        authorProfilePicture = (ImageView) findViewById(R.id.profile_picture);

        getViewedUserProfilePicture(mAuthorId);

        nameView = (TextView) findViewById(R.id.name);
        nameView.setText(mAuthorName);

        mAuthorRankView = (TextView) findViewById(R.id.author_rank);
        mAuthorRankView.setText(mAuthorRank);

        descriptionView = (TextView) findViewById(R.id.description);
        descriptionView.setText(mDropDescription);

        ripleCountView = (TextView) findViewById(R.id.riple_count);
        ripleCountView.setText(mRipleCount);

        commentCountView = (TextView) findViewById(R.id.comment_count);
        commentCountView.setText(mCommentCount);
        commentCountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDrop();
            }
        });

//        createdAtView = (TextView) findViewById(R.id.comment_created_at);
//        createdAtView.setText(String.valueOf(mCreatedAt));

//        mRecyclerView = (RecyclerView) findViewById(R.id.completed_by_recycler_view);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mRecyclerView.setItemAnimator(new SlideInUpAnimator());


//        loadCompletedListFromParse();
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
                                authorProfilePicture.setImageBitmap(bmp);
                            }
                        }
                    });
                }
            }
        });
    }



    private void viewDrop(){

        Intent intent = new Intent(CompletedActivity.this, ViewDropActivity.class);
        intent.putExtra("dropObjectId", mDropObjectId);
        intent.putExtra("authorId", mAuthorId);
        intent.putExtra("commenterName", mAuthorName);
        intent.putExtra("dropDescription", mDropDescription);
        intent.putExtra("ripleCount", mRipleCount);
        intent.putExtra("commentCount", mCommentCount);
        intent.putExtra("createdAt", mCreatedAt);
        this.startActivity(intent);
    }



        /*try {
            if (dropQuery.find().size() != 0) {
                completedByTheseUsers = (ParseUser) dropQuery.find().get(0);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseRelation completedByRelation = completedByTheseUsers.getRelation("completedBy");
        ParseQuery completedByQuery = completedByRelation.getQuery();
        completedByQuery.orderByDescending("createdAt");
        completedByQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {

                if (e != null) {

                } else {
                    for (int i = 0; i < list.size(); i++) {

                        final CompletedByItem completedByItem = new CompletedByItem();

                        //Drop Author Data//////////////////////////////////////////////////////////

                        ParseFile parseProfilePicture = (ParseFile) list.get(i).get("parseProfilePicture");
                        if (parseProfilePicture != null) {
                            parseProfilePicture.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null) {
                                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//                                        Bitmap resized = Bitmap.createScaledBitmap(bmp, 100, 100, true);
                                        completedByItem.setParseProfilePicture(bmp);
                                    }
                                }
                            });
                        }

                        completedByItem.setDropObjectId(list.get(i).getObjectId());

                        completedByItem.setDisplayName (list.get(i).getString("displayName"));
                       *//* //Author id
                        completedByItem.setAuthorId(authorData.getObjectId());

                        //Drop Data////////////////////////////////////////////////////////////////
                        //DropObjectId
                        completedByItem.setObjectId(list.get(i).getObjectId());
                        //CreatedAt
                        completedByItem.setCreatedAt(list.get(i).getCreatedAt());
                        //dropItem.createdAt = new SimpleDateFormat("EEE, MMM d yyyy @ hh 'o''clock' a").parse("date");
                        //Drop description
                        completedByItem.setDescription(list.get(i).getString("description"));
                        //Riple Count
                        completedByItem.setRipleCount(String.valueOf(list.get(i).getInt("ripleCount") + " Riples"));
                        //Comment Count
                        completedByItem.setCommentCount(String.valueOf(list.get(i).getInt("commentCount") + " Comments"));*//*

                        completedByList.add(completedByItem);
                    }
                }

                updateRecyclerView(completedByList);

            }
        });*/


    public void updateRecyclerView(ArrayList<CompletedByItem> mCompletedByList) {

        mCompletedByAdapter = new CompletedByAdapter(this, mCompletedByList);
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(mCompletedByAdapter);
        scaleAdapter.setDuration(250);
        mRecyclerView.setAdapter(new AlphaInAnimationAdapter(scaleAdapter));
        mRecyclerView.setItemAnimator(new SlideInLeftAnimator());
    }
}
