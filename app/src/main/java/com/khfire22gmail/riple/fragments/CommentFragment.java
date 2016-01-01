package com.khfire22gmail.riple.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.model.CommentAdapter;
import com.khfire22gmail.riple.model.CommentItem;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;


public class CommentFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    private String mDropObjectId;
    private String mAuthorId;
    private String mAuthorRank;
    private String mAuthorName;
    private String mAuthorFacebookId;
    private String mDropDescription;
    private String mRipleCount;
    private String mCommentCount;
    private Date mCreatedAt;
    private String mTabName;
    private RecyclerView mRecyclerView;
    private TextView mViewDropEmptyView;
    private ArrayList<CommentItem> mCommentList;
    private CommentAdapter mCommentAdapter;

    public static CommentFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        CommentFragment fragment = new CommentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.comment_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mViewDropEmptyView = (TextView) view.findViewById(R.id.comment_empty_view);

        Intent intent = getActivity().getIntent();
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

        loadCommentsFromParse();

        return view;
    }

    public void loadCommentsFromParse() {
        final ArrayList<CommentItem> commentList = new ArrayList<>();

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

                    Log.i("KEVIN", "Comment list size: " + commentList.size());

                }
            }
        });
    }

    private void updateRecyclerView(ArrayList<CommentItem> comments) {

        if (comments.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            mViewDropEmptyView.setVisibility(View.VISIBLE);
        }
        else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mViewDropEmptyView.setVisibility(View.GONE);
        }

        mCommentAdapter = new CommentAdapter(getActivity(), comments);
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(mCommentAdapter);
        mRecyclerView.setAdapter(new AlphaInAnimationAdapter(scaleAdapter));
        mRecyclerView.setAdapter(mCommentAdapter);
    }
}





