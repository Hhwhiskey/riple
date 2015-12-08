package com.khfire22gmail.riple.model;

import android.content.Context;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.khfire22gmail.riple.R;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder;


/**
 * Created by Kevin on 12/6/2015.
 */
public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendsViewHolder> {

    ParseUser currentUser = ParseUser.getCurrentUser();
    Context mContext;
    List<FriendItem> data = Collections.emptyList();
    private CommentAdapter.MyViewHolder viewHolder;
    private LayoutInflater inflater;

    public FriendAdapter(Context context, ArrayList<FriendItem> friendsList) {
        mContext = context;
        this.data = friendsList;
    }

    @Override
    public FriendAdapter.FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.friend_list_item, parent);
        FriendsViewHolder viewHolder = new FriendsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FriendsViewHolder holder, int position) {
        holder.update(position);

        ParseUser user = null;
        ParseUser currentUser = ParseUser.getCurrentUser();

        /*if (data != null) {
            String senderName = data.get("displayName");
            String userId = user1.getObjectId();

            if (userId.equals(currentUser.getObjectId())) {
                user = (ParseUser) holder.get(position);
            }
            else {
                user = (ParseUser) holder.get(position);
            }
        }*/
    }

    @Override
    public int getItemCount() {
        return 0;
    }




   //FriendsViewHolder/////////////////////////////////////////////////////////////////////////////
    public class FriendsViewHolder extends AnimateViewHolder {

        private ImageView friendProfilePicture;
        private TextView friendName;
        private TextView lastMessage;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            friendProfilePicture = (ImageView) itemView.findViewById(R.id.friend_profile_picture);
            friendName = (TextView) itemView.findViewById(R.id.friend_name);
//            lastMessage = (TextView) itemView.findViewById(R.id.last_message_snippit);
        }

        public void update(int position) {

            FriendItem current = data.get(position);

            friendProfilePicture.setImageBitmap(current.friendProfilePicture);
            friendName.setText(current.friendName);
//            lastMessage.setText(current.lastMessage);
        }


        @Override
        public void animateAddImpl(ViewPropertyAnimatorListener listener) {

        }

        @Override
        public void animateRemoveImpl(ViewPropertyAnimatorListener listener) {

        }
    }
}
