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
import com.parse.GetCallback;
import com.parse.ParseException;
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

    public FriendAdapter(Context context, ArrayList<FriendItem> data) {
        mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_friend, parent, false);
        FriendViewHolder viewHolder = new FriendViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FriendViewHolder viewHolder, int position) {
        viewHolder.update(position);
    }

    private void viewFriendProfile(int position) {

        String mClickedUserId = (data.get(position).getObjectId());
        String mClickedUserName = (data.get(position).getFriendName());

        Log.d("sDropViewUser", "Clicked User's Id = " + mClickedUserId);
        Log.d("sDropViewUser", "Clicked User's Name = " + mClickedUserName);

        Intent intent = new Intent(mContext, ViewUserActivity.class);
        intent.putExtra("clickedUserId", mClickedUserId);
        intent.putExtra("clickedUserName", mClickedUserName);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    //FriendsViewHolder/////////////////////////////////////////////////////////////////////////////
    public class FriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private ImageView friendProfilePicture;
        private TextView friendName;
        private TextView lastMessage;
        private ImageView menuButton;

        public FriendViewHolder(View itemView) {
            super(itemView);

            friendProfilePicture = (ImageView) itemView.findViewById(R.id.friend_profile_picture);
            friendName = (TextView) itemView.findViewById(R.id.friend_name);
            menuButton = (ImageView) itemView.findViewById(R.id.menu_button);
//            lastMessage = (TextView) itemView.findViewById(R.id.last_message_snippit);

            friendProfilePicture.setOnClickListener(this);
            friendName.setOnClickListener(this);
//            menuButton.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void update(int position) {

            FriendItem current = data.get(position);

            friendProfilePicture.setImageBitmap(current.friendProfilePicture);
            friendName.setText(current.friendName);
//            lastMessage.setText(current.lastMessage);
        }

        @Override
        public void onClick(View v) {
            if (v == menuButton) {
                openConversationMenu(getAdapterPosition());
            } else {
                openConversation(getAdapterPosition());
            }
        }


        @Override
        public boolean onLongClick(View v) {
            openConversationMenu(getAdapterPosition());
            return false;
        }

        public void openConversationMenu(int Position) {

            CharSequence todoDrop[] = new CharSequence[]{"View profile", "Remove Friend"};

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
            builder.setTitle("Drop Menu");
            builder.setItems(todoDrop, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int selected) {

                    if (selected == 0) {
                        viewFriendProfile(getAdapterPosition());
                    } else if (selected == 1) {
//                        RemoveFriend(getAdapterPosition());

                    }
                }
            });
        }


        // Opens sinch conversation when it is clicked
        public void openConversation(int position) {
            final String friendId = data.get(position).getObjectId();

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
                        Toast.makeText(mContext,
                                "Error finding that user",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }
}