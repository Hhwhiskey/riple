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
import android.view.animation.AnticipateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.activities.SettingsActivity;
import com.khfire22gmail.riple.model.CommentAdapter;
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

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;


public class CommentFragment extends Fragment {

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
    private ParseFile parseProfilePicture;
    private ImageView commenterProfilePictureView;
    private Button postCommentButton;
    private String displayName;
    private String commentText;
    private TextView newCommentView;
    private ParseFile commenterProfilePicture;

    public static final String ARG_PAGE = "COMMENT_PAGE";
    private int mPage;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);


        mRecyclerView = (RecyclerView) view.findViewById(R.id.comment_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new SlideInLeftAnimator(new AnticipateInterpolator(2f)));
        mRecyclerView.getItemAnimator().setRemoveDuration(500);

        mViewDropEmptyView = (TextView) view.findViewById(R.id.comment_empty_view);

        commenterProfilePictureView = (ImageView) view.findViewById(R.id.comment_post_profile_picture);

        newCommentView = (AutoCompleteTextView) view.findViewById(R.id.enter_comment_text);

        postCommentButton = (Button) view.findViewById(R.id.button_post_comment);

        //Display current user picture
        updateUserInfo();

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

        // Allow user to input Drop
        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseUser currentUser = ParseUser.getCurrentUser();
                parseProfilePicture = (ParseFile) currentUser.get("parseProfilePicture");
                displayName = (String) currentUser.get("displayName");

                if (parseProfilePicture == null && displayName == null) {
                    Toast.makeText(getActivity(), "Please upload a picture and set your User Name first, don't be shy :)", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), SettingsActivity.class);
                    startActivity(intent);
                } else if (parseProfilePicture == null) {
                    Toast.makeText(getActivity(), "Please upload a picture first, don't be shy :)", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), SettingsActivity.class);
                    startActivity(intent);

                } else if (displayName == null) {
                    Toast.makeText(getActivity(), "Please set your User Name first, don't be shy :)", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), SettingsActivity.class);
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

        return view;
    }

    private void updateUserInfo() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        commenterProfilePicture = (ParseFile) currentUser.get("parseProfilePicture");
        Bundle parametersPicture = new Bundle();
        parametersPicture.putString("fields", "picture.width(150).height(150)");

        //get parse profile picture if exists, if not, store Facebook picture on Parse and show

        if (commenterProfilePicture != null) {
            Glide.with(this)
                    .load(commenterProfilePicture.getUrl())
                    .crossFade()
                    .fallback(R.drawable.ic_user_default)
                    .error(R.drawable.ic_user_default)
                    .signature(new StringSignature(UUID.randomUUID().toString()))
                    .into(commenterProfilePictureView);
        } else {
            Toast.makeText(getActivity(), "Please upload a picture first, don't be shy :)", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        }
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
                    Toast.makeText(getActivity(), "Your comment has been posted!", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getActivity(), "Please enter some text first!", Toast.LENGTH_LONG).show();
        }

        hideSoftKeyboard();
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

    public void hideSoftKeyboard() {
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }
}





