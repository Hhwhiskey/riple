package com.khfire22gmail.riple.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.ProfilePictureView;
import com.khfire22gmail.riple.LoginActivity;
import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.application.RipleApplication;
import com.khfire22gmail.riple.model.DropAdapter;
import com.khfire22gmail.riple.model.DropItem;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 9/8/2015.
 */
public class RipleTab extends Fragment {

    private ProfilePictureView userProfilePictureView;
    private TextView userNameView;
    private RecyclerView mRecyclerView;
    private List<DropItem> mRipleList;
    private DropAdapter mRipleAdapter;
    private RecyclerView.ItemAnimator animator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.tab_riple,container,false);

//        Riple Card
        userProfilePictureView = (ProfilePictureView) view.findViewById(R.id.ripleProfilePic);
        userNameView = (TextView) view.findViewById(R.id.ripleUserName);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.riple_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadRipleItemsFromParse();

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setItemAnimator(animator);

//        Fetch Facebook user info if it is logged
        ParseUser currentUser = ParseUser.getCurrentUser();
        if ((currentUser != null) && currentUser.isAuthenticated()) {
            makeMeRequest();
        }

        return view;
    }

    public void loadRipleItemsFromParse() {
        final List<DropItem> ripleList = new ArrayList<>();

        String currentUser = ParseUser.getCurrentUser().getObjectId();
//      String viewRiple = ParseUser.getViewRiple().getObjectId();

        final ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Drop");
        query1.whereEqualTo("author", currentUser);

        final ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Drop");
        query2.whereEqualTo("done", currentUser);

        List<ParseQuery<ParseObject>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);

        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (e != null) {
                    Log.i("KEVIN", "error error");

                } else {
                    for (int i = 0; i < list.size(); i++) {

                        DropItem dropItem = new DropItem();

                        //Picture
                        dropItem.setFacebookId(list.get(i).getString("facebookId"));
                        //Name
                        dropItem.setAuthor(list.get(i).getString("name"));

                        //Date
                        dropItem.setCreatedAt(list.get(i).getCreatedAt());

//                      dropItem.createdAt = new SimpleDateFormat("EEE, MMM d yyyy @ hh 'o''clock' a").parse("date");

                        //Drop Title
                        dropItem.setTitle(list.get(i).getString("title"));

                        //Drop description
                        dropItem.setDescription(list.get(i).getString("description"));

                        //Riple Count
                        dropItem.setRipleCount(String.valueOf(list.get(i).getInt("ripleCount") + " Riples"));

                        //Comment Count
                        dropItem.setCommentCount(String.valueOf(list.get(i).getInt("commentCount") + " Comments"));

                        //Id that connects commenter to drop
//                              dropItem.setCommenter(list.get(i).getString("commenter"));

                        ripleList.add(dropItem);
                    }

                    Log.i("KEVIN", "PARSE LIST SIZE: " + ripleList.size());
                    updateRecyclerView(ripleList);
                }
            }
        });
    }

    private void updateRecyclerView(List<DropItem> items) {
        Log.d("KEVIN", "RIPLE LIST SIZE: " + items.size());

        mRipleList = items;

        mRipleAdapter = new DropAdapter(getActivity(), mRipleList, "riple");
        mRecyclerView.setAdapter(mRipleAdapter);

    }

    private void makeMeRequest() {

        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        if (jsonObject != null) {
                            JSONObject userProfile = new JSONObject();

                            try {
                                userProfile.put("facebookId", jsonObject.getString("id"));
                                userProfile.put("name", jsonObject.getString("name"));

                                // Save the user profile info in a user property
                                ParseUser currentUser = ParseUser.getCurrentUser();
                                currentUser.put("profile", userProfile);
                                currentUser.saveInBackground();

                                // Show the user info
                                updateViewsWithProfileInfo();
                            } catch (JSONException e) {
                                Log.d(RipleApplication.TAG,
                                        "Error parsing returned user data. " + e);
                            }
                        } else if (graphResponse.getError() != null) {
                            switch (graphResponse.getError().getCategory()) {
                                case LOGIN_RECOVERABLE:
                                    Log.d(RipleApplication.TAG,
                                            "Authentication error: " + graphResponse.getError());
                                    break;

                                case TRANSIENT:
                                    Log.d(RipleApplication.TAG,
                                            "Transient error. Try again. " + graphResponse.getError());
                                    break;

                                case OTHER:
                                    Log.d(RipleApplication.TAG,
                                            "Some other error: " + graphResponse.getError());
                                    break;
                            }
                        }
                    }
                });

        request.executeAsync();
    }

    private void updateViewsWithProfileInfo() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser.has("profile")) {
            JSONObject userProfile = currentUser.getJSONObject("profile");
            try {
                String url;
                Bundle parametersPicture = new Bundle();
                parametersPicture.putString("fields", "picture.width(150).height(150)");

                if (userProfile.has("facebookId")) {
                    userProfilePictureView.setProfileId(userProfile.getString("facebookId"));

                } else {
                    // Show the default, blank user profile picture
                    userProfilePictureView.setProfileId(null);
                }

                if (userProfile.has("name")) {
                    userNameView.setText(userProfile.getString("name"));
                } else {
                    userNameView.setText("");
                }

            } catch (JSONException e) {
                Log.d(RipleApplication.TAG, "Error parsing saved user data.");
            }
        }
    }
    /*public void onLogoutClick(View v) {
        logout();
    }

    private void logout() {
        // Log the user out
        ParseUser.logOut();

        // Go to the login view
        startLoginActivity();
    }*/

    private void startLoginActivity() {
        Intent intent = new Intent(this.getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
