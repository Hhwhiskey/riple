package com.khfire22gmail.riple.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.activities.MessagingActivity;
import com.khfire22gmail.riple.model.FriendAdapter;
import com.khfire22gmail.riple.model.FriendItem;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * Created by Kevin on 9/8/2015.
 */

public class FriendsTabFragment extends Fragment {

    private RecyclerView friendsRecyclerView;
    private String currentUserId;
    private ArrayList<String> clickedUserObjectId;
    private FriendAdapter mFriendAdapter;
    private ProgressDialog progressDialog;
    private BroadcastReceiver receiver;
    private ArrayList<String> displayNames;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_friends_tab, container, false);

        friendsRecyclerView = (RecyclerView) view.findViewById(R.id.friends_list_recycler_view);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendsRecyclerView.setItemAnimator(new SlideInUpAnimator());
//        loadSavedPreferences();

        return view;
    }

    public void loadSavedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean friendTipBoolean = sharedPreferences.getBoolean("friendTipBoolean", true);
        if (friendTipBoolean) {
            friendTip();
        }
    }

    public void savePreferences(String key, Boolean value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void friendTip() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FriendsTabFragment.this.getActivity(), R.style.MyAlertDialogStyle);

        builder.setTitle("Friends...");
        builder.setMessage("This is your friends list. Everyone you have contacted will show up " +
                "here so you can easily keep in touch. Simple enough, right?");

        builder.setNegativeButton("HIDE THIS TIP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                savePreferences("friendTipBoolean", false);

            }
        });

        builder.setPositiveButton("KEEP THIS AROUND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    //Show list of all users
    private void setConversationsList() {

        final ArrayList<FriendItem> friendsList = new ArrayList();

        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseQuery senderQuery = ParseQuery.getQuery("Friends");
        ParseQuery recipientQuery = ParseQuery.getQuery("Friends");

        senderQuery.whereEqualTo("sender", currentUser);
        recipientQuery.whereEqualTo("recipient", currentUser);

        List<ParseQuery<ParseObject>> queries = new ArrayList<>();
        queries.add(senderQuery);
        queries.add(recipientQuery);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
        mainQuery.orderByDescending("updatedAt");
        mainQuery.include("localUser");
        mainQuery.include("remoteUser");

        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {

                    for (int i = 0; i < list.size(); i++) {

                        final FriendItem friendItem = new FriendItem();

                        ParseFile profilePicture = (ParseFile) list.get(i).get("parseProfilePicture");
                        if (profilePicture != null) {
                            profilePicture.getDataInBackground(new GetDataCallback() {

                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null) {
                                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//                                        Bitmap resized = Bitmap.createScaledBitmap(bmp, 100, 100, true);
                                        friendItem.setFriendProfilePicture(bmp);
                                    }
                                }
                            });
                        }

                        friendItem.setFriendName(list.get(i).getString("displayName"));
//                        friendItem.setLastMessage(list.get(i).getString("lastMessage"));

                        friendsList.add(friendItem);
                    }
                }

                updateFriendsListRecyclerView(friendsList);
            }
        });
    }

    public void updateFriendsListRecyclerView(ArrayList <FriendItem> friendsList) {

        friendsRecyclerView = (RecyclerView) getActivity().findViewById(R.id.friends_list_recycler_view);
        mFriendAdapter = new FriendAdapter(getActivity(), friendsList);
        friendsRecyclerView.setAdapter(mFriendAdapter);
    }

    // Opens sinch conversation when it is clicked
    public void openConversation(ArrayList<String> names, int pos) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", names.get(pos));
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> user, ParseException e) {
                if (e == null) {
                    Intent intent = new Intent(getActivity(), MessagingActivity.class);
                    intent.putExtra("RECIPIENT_ID", user.get(0).getObjectId());
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(),
                            "Error finding that user",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //show a loading spinner while the sinch client starts
    private void showSpinner() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Boolean success = intent.getBooleanExtra("success", false);
                progressDialog.dismiss();
                if (!success) {
                    Toast.makeText(getActivity(), "Messaging service failed to start", Toast.LENGTH_LONG).show();
                }
            }
        };

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter("com.khfire22gmail.riple.tabs.FriendsTabFragment"));

    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_users, menu);
        return true;
    }*/



    @Override
    public void onResume() {
//  todo      setConversationsList();
        super.onResume();
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

