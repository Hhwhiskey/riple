package com.khfire22.riple.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.khfire22.riple.R;
import com.khfire22.riple.model.FriendAdapter;
import com.khfire22.riple.model.FriendItem;
import com.khfire22.riple.utils.ConnectionDetector;
import com.khfire22.riple.utils.Constants;
import com.khfire22.riple.utils.SaveToSharedPrefs;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

/**
 * Created by Kevin on 9/8/2015.
 */

public class FriendsTabFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private String currentUserId;
    private ArrayList<String> clickedUserObjectId;
    private FriendAdapter friendAdapter;
    private ProgressDialog progressDialog;
    private BroadcastReceiver receiver;
    private ArrayList<String> displayNames;
    private TextView friendsEmptyView;
    private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    private ConnectionDetector detector;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_tab, container, false);

        detector = new ConnectionDetector(getActivity());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.friends_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new LandingAnimator());
        mRecyclerView.getItemAnimator().setRemoveDuration(500);

        friendsEmptyView = (TextView) view.findViewById(R.id.friends_tab_empty_view);

        loadFriendsListFromParse();
//        showUserTips();

        //Pull refresh conversation list
        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) view.findViewById(R.id.friend_swipe);
        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {

                // Check for network connection
                if (!detector.isConnectedToInternet()) {
                    Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                } else {
                    loadFriendsListFromParse();
                    new refreshTask().execute();
                }

                // Hide the refresh indicator after 5 seconds if no data is found
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mWaveSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 5000);
            }
        });

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

//        loadFriendsListFromParse();

        if (isVisibleToUser && loadSavedPreferences()) {
            friendTip();
        }
    }

    public boolean loadSavedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean test = sharedPreferences.getBoolean("friendTips", true);

        if (!test) {
            return false;
        } else {
            return true;
        }
    }

    public void friendTip() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FriendsTabFragment.this.getActivity(), R.style.MyAlertDialogStyle);

        builder.setTitle("Friends...");
        builder.setMessage(R.string.friend_tip);

        builder.setNegativeButton("HIDE THIS TIP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SaveToSharedPrefs saveToSharedPrefs = new SaveToSharedPrefs();
                saveToSharedPrefs.saveBooleanPreferences(getActivity(), "friendTips", false);

            }
        });

        builder.setPositiveButton("KEEP THIS AROUND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

//    Show list of all users
    public void loadFriendsListFromParse() {

        final ArrayList<FriendItem> friendsList = new ArrayList<>();

        final ParseUser currentUser = ParseUser.getCurrentUser();

        ParseQuery senderQuery = ParseQuery.getQuery("Friends");
        ParseQuery recipientQuery = ParseQuery.getQuery("Friends");

        senderQuery.whereEqualTo("user1", currentUser);
        recipientQuery.whereEqualTo("user2", currentUser);

        List<ParseQuery<ParseObject>> queries = new ArrayList<>();
        queries.add(senderQuery);
        queries.add(recipientQuery);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
        mainQuery.orderByDescending("updatedAt");
        mainQuery.include("user1");
        mainQuery.include("user2");
        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {

                    for (int i = 0; i < list.size(); i++) {
                        Log.d("MyApp", "current relation objects = " + list.size());
                        ParseUser user1 = (ParseUser) list.get(i).get(Constants.USER1);
                        String userId = user1.getObjectId();
                        ParseUser recipient;

                        final FriendItem friendItem = new FriendItem();
                        //Assign recipient to the !currentUser
                        if (userId.equals(currentUser.getObjectId())) {
                            recipient = (ParseUser) list.get(i).get(Constants.USER2);
                        } else {
                            recipient = (ParseUser) list.get(i).get(Constants.USER1);
                        }

                        // Get recepient data
                        ParseFile profilePicture1 = (ParseFile) recipient.get("parseProfilePicture");
                        if (profilePicture1 != null) {
                            profilePicture1.getDataInBackground(new GetDataCallback() {

                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null) {
                                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        if (bmp != null) {
                                            Bitmap resized = Bitmap.createScaledBitmap(bmp, 150, 150, true);
                                            friendItem.setFriendProfilePicture(resized);
                                        }
                                        updateFriendsListRecyclerView(friendsList);
                                    }
                                }
                            });
                        }
                        //Get relation objectId
                        friendItem.setRelationshipObjectId(list.get(i).getObjectId());
                        friendItem.setLastMessageSnippet(list.get(i).getString("lastMessage"));

                        //Get created at from parse and convert it to friendly String
                        Format formatter = new SimpleDateFormat("MMM d");
                        String dateAfter = formatter.format(list.get(i).getUpdatedAt());
                        friendItem.setLastMessageTimeStamp(dateAfter);

                        //Get all friend info
                        friendItem.setFriendObjectId(recipient.getObjectId());
                        friendItem.setFriendName(recipient.getString("displayName"));
                        friendItem.setRipleRank(recipient.getString("userRank"));
                        friendItem.setFriendInfo(recipient.getString("userInfo"));

                        //Author location
                        String authorLocation = recipient.getString("userLastLocation");
                        if (authorLocation == null || authorLocation.equals("")) {
                            friendItem.setFriendLastLocation("Location unavailable");
                        } else {
                            friendItem.setFriendLastLocation(authorLocation);
                        }

                        friendItem.setRipleCount(String.valueOf(recipient.getInt("userRipleCount")));
                        friendsList.add(friendItem);
                    }
                }
//                friendTabInteractionList = friendsList;
            }
        });
    }

    public void updateFriendsListRecyclerView(ArrayList <FriendItem> friendsList) {

        //Show textview if list is empty, otherwise show data
        if (friendsList.isEmpty()) {
           mRecyclerView.setVisibility(View.GONE);
            friendsEmptyView.setVisibility(View.VISIBLE);
        }
        else {
            mRecyclerView.setVisibility(View.VISIBLE);
            friendsEmptyView.setVisibility(View.GONE);
        }

        //Set animating adapter to recyclerView
        friendAdapter = new FriendAdapter(getActivity(), friendsList);
        AlphaInAnimationAdapter alphaInAnimationAdapter = new AlphaInAnimationAdapter(friendAdapter);
        mRecyclerView.setAdapter(alphaInAnimationAdapter);
        alphaInAnimationAdapter.setDuration(500);
    }

//    //show a loading spinner while the sinch client starts
//    private void showSpinner() {
//        progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setTitle("Loading");
//        progressDialog.setMessage("Please wait...");
//        progressDialog.show();
//
//        receiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Boolean success = intent.getBooleanExtra("success", false);
//                progressDialog.dismiss();
//                if (!success) {
//                    Toast.makeText(getActivity(), "Messaging service failed to start", Toast.LENGTH_LONG).show();
//                }
//            }
//        };
//
//        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter("com.khfire22gmail.riple.tabs.FriendsTabFragment"));
//
//    }

    @Override
    public void onResume() {
        super.onResume();
        loadFriendsListFromParse();
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

    private class refreshTask extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... params) {
            return new String[0];
        }

        @Override protected void onPostExecute(String[] result) {
            // Call setRefreshing(false) when the list has been refreshed.
            mWaveSwipeRefreshLayout.setRefreshing(false);
            super.onPostExecute(result);
        }
    }
}

