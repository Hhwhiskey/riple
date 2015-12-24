package com.khfire22gmail.riple.model;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.activities.MessagingActivity;
import com.khfire22gmail.riple.fragments.FriendsTabFragment;
import com.parse.FindCallback;
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

        ParseUser user = null;
        ParseUser currentUser = ParseUser.getCurrentUser();

        /*if (data != null) {
            String senderName = data.get("displayName");
            String userObjectId = user1.getObjectId();
            if (userObjectId.equals(currentUser.getObjectId())) {
                user = (ParseUser) holder.get(position);
            }
            else {
                user = (ParseUser) holder.get(position);
            }
        }*/
    }

    @Override
    public int getItemCount() {
        return data.size();
    }




    //FriendsViewHolder/////////////////////////////////////////////////////////////////////////////
    public class FriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView friendProfilePicture;
        private TextView friendName;
        private TextView lastMessage;
        private View itemView;


        public FriendViewHolder(View itemView) {
            super(itemView);

            friendProfilePicture = (ImageView) itemView.findViewById(R.id.friend_profile_picture);
            friendName = (TextView) itemView.findViewById(R.id.friend_name);


//            lastMessage = (TextView) itemView.findViewById(R.id.last_message_snippit);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getFriendObjectFromRow(getAdapterPosition());
                }
            });
        }

        public void update(int position) {

            FriendItem current = data.get(position);

            friendProfilePicture.setImageBitmap(current.friendProfilePicture);
            friendName.setText(current.friendName);
//            lastMessage.setText(current.lastMessage);
        }

        @Override
        public void onClick(View v) {

        }

        private void getFriendObjectFromRow(int position) {
            FriendItem conversation = FriendsTabFragment.friendTabInteractionList.get(position);
            ParseQuery<ParseObject> conversationQuery = ParseQuery.getQuery("Friends");
            conversationQuery.getInBackground(conversation.getObjectId(), new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        openConversation(object);
                    }
                }
            });
        }

        // Opens sinch conversation when it is clicked
        public void openConversation(ParseObject conversation) {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("objectId", conversation);
            query.findInBackground(new FindCallback<ParseUser>() {
                public void done(List<ParseUser> user, ParseException e) {
                    if (e == null) {
                        Intent intent = new Intent(mContext, MessagingActivity.class);
                        intent.putExtra("RECIPIENT_ID", user.get(0).getObjectId());
                        mContext.startActivity(intent);
                    } else {
                        Toast.makeText(mContext, "Error finding that user", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}