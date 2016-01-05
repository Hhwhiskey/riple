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
import com.khfire22gmail.riple.model.CompletedByAdapter;
import com.khfire22gmail.riple.model.CompletedByItem;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;


public class CompletedFragment extends Fragment {

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
    private CompletedByAdapter mCompletedByAdapter;
    private RecyclerView mRecyclerView;
    private TextView mCompletedEmptyView;

    public static final String ARG_PAGE = "COMPLETED_PAGE";
    private int mPage;

    public static CompletedFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        CompletedFragment fragment = new CompletedFragment();
        fragment.setArguments(args);
        return fragment ;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.completed_by_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mCompletedEmptyView = (TextView) view.findViewById(R.id.completed_empty_view);

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

        loadCompletedListFromParse();

        return view;
    }

    private void loadCompletedListFromParse() {
        final ArrayList<CompletedByItem> completedByList = new ArrayList<>();

        ParseQuery dropQuery = ParseQuery.getQuery("Drop");
        dropQuery.whereEqualTo("objectId", mDropObjectId);
        dropQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                ParseRelation<ParseObject> completedByRelation = parseObject.getRelation("completedBy");
                ParseQuery completedByQuery = completedByRelation.getQuery();
                completedByQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {

                        if (e != null) {

                        } else {
                            for (int i = 0; i < list.size(); i++) {

                                Log.d("list", "list= " + list);

                                final CompletedByItem completedByItem = new CompletedByItem();

                                ParseFile parseProfilePicture = (ParseFile) list.get(i).get("parseProfilePicture");
                                if (parseProfilePicture != null) {
                                    parseProfilePicture.getDataInBackground(new GetDataCallback() {
                                        @Override
                                        public void done(byte[] data, ParseException e) {
                                            if (e == null) {
                                                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//                                        Bitmap resized = Bitmap.createScaledBitmap(bmp, 100, 100, true);
                                                completedByItem.setParseProfilePicture(bmp);
                                                updateRecyclerView(completedByList);
                                            }
                                        }
                                    });
                                }

                                completedByItem.setUserObjectId(list.get(i).getObjectId());
                                completedByItem.setDisplayName((String) list.get(i).get("displayName"));
                                completedByItem.setUserRank(list.get(i).getString("userRank"));
                                completedByItem.setUserRipleCount(list.get(i).getInt("userRipleCount"));


                                completedByList.add(completedByItem);
                            }
                        }
                    }
                });
            }
        });
    }

    public void updateRecyclerView(ArrayList<CompletedByItem> mCompletedByList) {

        if (mCompletedByList.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            mCompletedEmptyView.setVisibility(View.VISIBLE);
        }
        else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mCompletedEmptyView.setVisibility(View.GONE);
        }

        mCompletedByAdapter = new CompletedByAdapter(getActivity(), mCompletedByList);
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(mCompletedByAdapter);
        scaleAdapter.setDuration(250);
        mRecyclerView.setAdapter(new AlphaInAnimationAdapter(scaleAdapter));
        mRecyclerView.setItemAnimator(new SlideInLeftAnimator());
    }

}
