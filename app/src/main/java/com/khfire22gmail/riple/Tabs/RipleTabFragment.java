package com.khfire22gmail.riple.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.khfire22gmail.riple.LoginActivity;
import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.model.DropAdapter;
import com.khfire22gmail.riple.model.DropItem;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;

/**
 * Created by Kevin on 9/8/2015.
 */
public class RipleTabFragment extends Fragment {

    private ProfilePictureView profilePictureView;
    private TextView nameView;
    private RecyclerView mRecyclerView;
    private List<DropItem> mRipleList;
    private DropAdapter mRipleAdapter;
    private RecyclerView.ItemAnimator animator;
    private TextView profileRankView;
    private TextView profileRipleCountView;
    private TextView parseRankView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_riple_tab,container,false);


//        Profile Card
        profilePictureView = (ProfilePictureView) view.findViewById(R.id.profile_pic);
        nameView = (TextView) view.findViewById(R.id.profile_name);
        profileRankView = (TextView) view.findViewById(R.id.profile_rank);
        profileRipleCountView = (TextView) view.findViewById(R.id.profile_riple_count);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.riple_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new SlideInUpAnimator());

        //Update Profile Card
//        updateViewsWithProfileInfo();

        //Query the users created and completed drops_blue
        updateUserInfo();
        loadRipleItemsFromParse();


//        int size = (int) getResources().getDimension(R.dimen.com_facebook_profilepictureview_preset_size_large);
//        profilePictureView.setPresetSize(ProfilePictureView.LARGE);

//        Fetch Facebook user info if it is logged
//        ParseUser currentUser = ParseUser.getCurrentUser();
//        if ((currentUser != null) && currentUser.isAuthenticated()) {
//
//        }

        return view;
    }



    public void loadRipleItemsFromParse() {
        final List<DropItem> ripleList = new ArrayList<>();

        ParseUser user = ParseUser.getCurrentUser();

        ParseRelation createdRelation = user.getRelation("createdDrops");
        ParseRelation completedRelation = user.getRelation("completedDrops");

        ParseQuery createdQuery = createdRelation.getQuery();
        ParseQuery completedQuery = completedRelation.getQuery();

        List<ParseQuery<ParseObject>> queries = new ArrayList<>();
        queries.add(createdQuery);
        queries.add(completedQuery);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);

        mainQuery.orderByDescending("createdAt");

        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (e != null) {
                    Log.i("KEVIN", "error error");

                } else {
                    for (int i = 0; i < list.size(); i++) {

                        DropItem dropItem = new DropItem();

                        //ObjectId
                        dropItem.setObjectId(list.get(i).getObjectId());

                        //Picture
                        dropItem.setFacebookId(list.get(i).getString("facebookId"));

                        //Author name
                        dropItem.setAuthorName(list.get(i).getString("name"));

                        //Author id
                        dropItem.setAuthorId(list.get(i).getString("author"));

                        //Date
                        dropItem.setCreatedAt(list.get(i).getCreatedAt());
//                      dropItem.createdAt = new SimpleDateFormat("EEE, MMM d yyyy @ hh 'o''clock' a").parse("date");

                        //Drop description
                        dropItem.setDescription(list.get(i).getString("description"));

                        //Riple Count
                        dropItem.setRipleCount(String.valueOf(list.get(i).getInt("ripleCount") + " Riples"));

                        //Comment Count
                        dropItem.setCommentCount(String.valueOf(list.get(i).getInt("commentCount") + " Comments"));

                        //Id that connects commenterName to drop
//                              dropItem.setCommenterName(list.get(i).getString("commenterName"));

                        ripleList.add(dropItem);
                    }
                }

                updateRecyclerView(ripleList);
            }
        });
    }

    public void updateRecyclerView(List<DropItem> mRipleList) {

        mRipleAdapter = new DropAdapter(getActivity(), mRipleList, "riple");
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(mRipleAdapter);
        scaleAdapter.setDuration(250);
        mRecyclerView.setAdapter(new AlphaInAnimationAdapter(scaleAdapter));
    }

    private void updateUserInfo() {

        ParseUser currentUser = ParseUser.getCurrentUser();
        String userName = currentUser.getString("name");
        String facebookId = currentUser.getString("facebookId");

        Bundle parametersPicture = new Bundle();
        parametersPicture.putString("fields", "picture.width(150).height(150)");

        // Update User Picture
        if (facebookId != null) {
            profilePictureView.setProfileId(facebookId);

        } else {
            // Show the default, blank user profile picture
            profilePictureView.setProfileId(null);
        }

        // Update UserName
        if (userName != null) {
            nameView.setText(userName);
        } else {
            nameView.setText("Anonymous");
        }





        String localRank = "\"Drop\"";
        int parseRipleCount = currentUser.getInt("userRipleCount");

        if (parseRipleCount > 19) {
            localRank = ("\"Volunteer\"");//2
        }
        if (parseRipleCount > 39) {
            localRank = ("\"Do-Gooder\"");//3
        }
        if (parseRipleCount > 79) {
            localRank = ("\"Contributor\"");//4
        }
        if (parseRipleCount > 159) {
            localRank = ("\"Patron\"");//5
        }
        if (parseRipleCount > 319) {
            localRank = ("\"Beneficent\"");//6
        }
        if (parseRipleCount > 639) {
            localRank = ("\"Humanitarian\"");//7
        }
        if (parseRipleCount > 1279) {
            localRank = ("\"Saint\"");//8
        }
        if (parseRipleCount > 2559) {
            localRank = ("\"Gandhi\"");//9
        }
        if (parseRipleCount > 5119) {
            localRank = ("\"Mother Teresa\"");//10
        }

        if (localRank != null) {
            currentUser.put("userRank", localRank);
        }

        profileRipleCountView.setText(String.valueOf(parseRipleCount) + " Riples");

        if (localRank != null) {
            profileRankView.setText(localRank);
        }
    }


    private void startLoginActivity() {
        Intent intent = new Intent(this.getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

//    Bitmap fbPic = "https://graph.facebook.com/" + fbId + "/picture?width=108&height=108"

}
