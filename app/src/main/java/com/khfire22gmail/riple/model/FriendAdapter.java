package com.khfire22gmail.riple.model;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.activities.MessagingActivity;
import com.khfire22gmail.riple.activities.ViewUserActivity;
import com.khfire22gmail.riple.utils.ConnectionDetector;
import com.khfire22gmail.riple.utils.Constants;
import com.khfire22gmail.riple.utils.Vibrate;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by Kevin on 12/6/2015.
 */
public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    ParseUser currentUser = ParseUser.getCurrentUser();
    Context mContext;
    List<FriendItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private ConnectionDetector detector;

    public FriendAdapter() {
    }

    public FriendAdapter(Context context, ArrayList<FriendItem> data) {
        mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;
        detector = new ConnectionDetector(mContext);
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_user_view, parent, false);
        FriendViewHolder viewHolder = new FriendViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FriendViewHolder viewHolder, int position) {
        viewHolder.update(position);
    }

    private void viewFriendProfile(int position) {

        String clickedUserId = (data.get(position).getFriendObjectId());
        String clickedUserName = (data.get(position).getFriendName());
        String clickedUserRank = (data.get(position).getRipleRank());
        String clickedUserRipleCount = (data.get(position).getRipleCount());

        Log.d("sDropViewUser", "Clicked User's Id = " + clickedUserId);
        Log.d("sDropViewUser", "Clicked User's Name = " + clickedUserName);

        Intent intent = new Intent(mContext, ViewUserActivity.class);
        intent.putExtra(Constants.CLICKED_USER_ID, clickedUserId);
        intent.putExtra(Constants.CLICKED_USER_NAME, clickedUserName);
        intent.putExtra(Constants.CLICKED_USER_RANK, clickedUserRank);
        intent.putExtra(Constants.CLICKED_USER_RIPLE_COUNT, clickedUserRipleCount);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    //FriendsViewHolder/////////////////////////////////////////////////////////////////////////////
    public class FriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private ImageView otherProfilePicture;
        private TextView friendName;
        private TextView ripleRank;
        private TextView ripleCount;
        private TextView lastMessageSnippet;
        private TextView lastMessageTimeStamp;

//        private ImageView menuButton;
//        private RelativeLayout otherLayout;
        public FriendViewHolder(View itemView) {
            super(itemView);

            lastMessageSnippet = (TextView) itemView.findViewById(R.id.last_message_snippet);
            lastMessageTimeStamp = (TextView) itemView.findViewById(R.id.last_message_date);
            otherProfilePicture = (ImageView) itemView.findViewById(R.id.other_profile_picture);
            friendName = (TextView) itemView.findViewById(R.id.other_display_name);
            ripleRank = (TextView) itemView.findViewById(R.id.other_rank);
            ripleCount = (TextView) itemView.findViewById(R.id.other_riple_count);

//            menuButton = (ImageView) itemView.findViewById(R.id.other_menu);
//            otherLayout = (RelativeLayout) itemView.findViewById(R.id.other_layout);

//            lastMessage = (TextView) itemView.findViewById(R.id.last_message_snippit);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            otherProfilePicture.setOnClickListener(this);

//            menuButton.setOnClickListener(this);

        }

        public void update(int position) {

            String testVariable = "1";

            FriendItem current = data.get(position);

            lastMessageTimeStamp.setText(current.lastMessageTimeStamp);
            lastMessageSnippet.setText(current.lastMessageSnippet);
            otherProfilePicture.setImageBitmap(current.friendProfilePicture);
            friendName.setText(current.friendName);
            ripleRank.setText(current.ripleRank);

            if (current.ripleCount.equals(testVariable)) {
                ripleCount.setText("with " + current.ripleCount + " Riple");
            } else ripleCount.setText("with " + current.ripleCount + " Riples");
        }

        @Override
        public void onClick(View v) {

            if (!detector.isConnectedToInternet()) {
                Toast.makeText(mContext, R.string.no_connection, Toast.LENGTH_LONG).show();
            } else {

                if (v == otherProfilePicture) {
                    viewFriendProfile(getAdapterPosition());
                } else {
                    openConversation(getAdapterPosition());
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            Vibrate vibrate = new Vibrate();
            vibrate.vibrate(mContext);

            if (!detector.isConnectedToInternet()) {
                Toast.makeText(mContext, R.string.no_connection, Toast.LENGTH_LONG).show();
            } else {
                openFriendMenu();
            }
            return false;
        }

        public void openFriendMenu() {

            CharSequence friendTitles[] = new CharSequence[]{"View profile", "Remove Friend"};

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
            builder.setTitle("Friend Menu");
            builder.setItems(friendTitles, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int selected) {

                    if (selected == 0) {
                        viewFriendProfile(getAdapterPosition());
                    } else if (selected == 1) {
                        RemoveFriend(getAdapterPosition());

                    }
                }
            });
            builder.show();
        }


        // Opens sinch conversation when it is clicked
        public void openConversation(int position) {
            final String friendId = data.get(position).getFriendObjectId();

            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("objectId", friendId);
            query.getFirstInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    if (e == null) {
                        Intent messageIntent = new Intent(mContext, MessagingActivity.class);
                        messageIntent.putExtra("RECIPIENT_ID", parseUser.getObjectId());
                        mContext.startActivity(messageIntent);
                    } else {
                        Toast.makeText(mContext, "Error finding that user", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void RemoveFriend(final int position) {
        final String relationshipToRemove = data.get(position).getRelationshipObjectId();

        ParseQuery relationshipQuery = ParseQuery.getQuery("Friends");
        relationshipQuery.whereEqualTo("objectId", relationshipToRemove);
        relationshipQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                try {
                    parseObject.delete();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                Toast.makeText(mContext.getApplicationContext(), "Friend has been removed", Toast.LENGTH_LONG).show();
                removeFriendFromView(position);
            }
        });

    }

    public void removeFriendFromView(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }
}