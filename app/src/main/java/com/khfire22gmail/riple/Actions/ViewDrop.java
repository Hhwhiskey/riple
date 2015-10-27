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

import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.model.CommentAdapter;
import com.khfire22gmail.riple.model.CommentItem;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ViewDrop extends AppCompatActivity {

    private String mAuthorId;
    private String mAuthorName;
    private String mFacebookId;
    private String mDropId;
    private Object animator;
    private RecyclerView mRecyclerView;
    private List<CommentItem> mViewDropList;
    private CommentAdapter mCommenterAdapter;
    private RecyclerView.Adapter mCommentAdapter;
    private String mDescription;
    private String currentDrop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_drop);

        Intent intent = getIntent();
        mDropId = intent.getStringExtra("DropId");
        mAuthorId = intent.getStringExtra("authorId");
        mAuthorName = intent.getStringExtra("authorName");
        mFacebookId = intent.getStringExtra("facebookId");
        mDescription = intent.getStringExtra("description");

        currentDrop = mDropId;

        Log.d("dropExtra", "mDropId = " + mDropId);
        Log.d("dropExtra", "mAuthorId = " + mAuthorId);
        Log.d("dropExtra", "mAuthorName = " + mAuthorName);
        Log.d("dropExtra", "mFacebookId = " + mFacebookId);
        Log.d("dropExtra", "mDescription = " + mDescription);

        mRecyclerView = (RecyclerView) findViewById(R.id.view_drop_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadCommentsFromParse();

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setItemAnimator((RecyclerView.ItemAnimator) animator);


    }

    public void loadCommentsFromParse() {
        final List<CommentItem> viewDropList = new ArrayList<>();

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Comment");
        query.whereEqualTo("DropId", "currentDrop");
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

                        // DropId
//                        commentItem.setDropId(list.get(i).getString("objectId"));
                        //Picture
                        commentItem.setFacebookId(list.get(i).getString("facebookId"));
                        //Author name
                        commentItem.setCommenter(list.get(i).getString("commenter"));

                        //Author id
//                        dropItem.setAuthorId(list.get(i).getString("author"));

                        //Date
                        commentItem.setCreatedAt(list.get(i).getCreatedAt());

                        //Comment
                        commentItem.setComment(list.get(i).getString("comment"));

//                      dropItem.createdAt = new SimpleDateFormat("EEE, MMM d yyyy @ hh 'o''clock' a").parse("date");

                        //Drop Title
//                        dropItem.setTitle(list.get(i).getString("title"));

                        //Drop description
//                        dropItem.setDescription(list.get(i).getString("description"));

                        //Riple Count
//                        dropItem.setRipleCount(String.valueOf(list.get(i).getInt("ripleCount") + " Riples"));

                        //Comment Count
//                        dropItem.setCommentCount(String.valueOf(list.get(i).getInt("commentCount") + " Comments"));

                        //Id that connects commenter to drop
//                              dropItem.setCommenter(list.get(i).getString("commenter"));

                        viewDropList.add(commentItem);
                    }

                    Log.i("KEVIN", "PARSE LIST SIZE: " + viewDropList.size());
                    updateRecyclerView(viewDropList);
                }
            }
        });
    }

    private void updateRecyclerView(List<CommentItem> items) {
        Log.d("KEVIN", "VIEWDROP LIST SIZE: " + items.size());

        mViewDropList = items;

        mCommenterAdapter = new CommentAdapter(this, mViewDropList);
        mRecyclerView.setAdapter(mCommentAdapter);
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
