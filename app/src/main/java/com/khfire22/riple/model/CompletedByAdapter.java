package com.khfire22.riple.model;

/**
 * Created by Kevin on 12/13/2015.
 */
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.khfire22.riple.R;
import com.khfire22.riple.activities.MessagingActivity;
import com.khfire22.riple.activities.ViewUserActivity;
import com.khfire22.riple.utils.Constants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompletedByAdapter extends RecyclerView.Adapter<CompletedByAdapter.CompletedByViewHolder> {

    Context mContext;
    private LayoutInflater inflater;
    List<CompletedByItem> data = Collections.emptyList();

    public CompletedByAdapter(Context context, ArrayList<CompletedByItem> data) {
        mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;

    }

    @Override
    public CompletedByViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_completed, parent, false);
        CompletedByViewHolder viewHolder = new CompletedByViewHolder(view);
        return viewHolder;
    }

   /* private void viewOtherUser(int position) {

        String mClickedUserId = (data.get(position).getCommenterId());
        String mClickedUserName = (data.get(position).getCommenterName());

        Log.d("sCommentViewUser", "Clicked User's userObjectId = " + mClickedUserId);
        Log.d("sCommentViewUser", "Clicked User's clickedUserName = " + mClickedUserName);

        Intent intent = new Intent(mContext, ViewUserActivity.class);
        intent.putExtra("clickedUserId", mClickedUserId);
        intent.putExtra("clickedUserName", mClickedUserName);
        mContext.startActivity(intent);
    }*/


    @Override
    public void onBindViewHolder(CompletedByViewHolder viewHolder, final int position) {
        viewHolder.update(position);

        /*viewHolder.commenterParseProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewOtherUser(position);
            }
        });

        viewHolder.commenterName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewOtherUser(position);
            }
        });

        viewHolder.commentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewOtherUser(position);
            }
        });

        viewHolder.createdAt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewOtherUser(position);
            }
        });*/
    }

    private void viewOtherUser(int position) {

        String clickedUserId = (data.get(position).getUserObjectId());
        String clickedUserName = (data.get(position).getDisplayName());
        String clickedUserRank = (data.get(position).getUserRank());
        String clickedUserRipleCount = (data.get(position).getUserRipleCount());
        String clickedUserInfo = (data.get(position).getUserInfo());

        Log.d("sDropViewUser", "Clicked User's Id = " + clickedUserId);
        Log.d("sDropViewUser", "Clicked User's Name = " + clickedUserName);

        Intent intent = new Intent(mContext, ViewUserActivity.class);
        intent.putExtra(Constants.CLICKED_USER_ID, clickedUserId);
        intent.putExtra(Constants.CLICKED_USER_NAME, clickedUserName);
        intent.putExtra(Constants.CLICKED_USER_RANK, clickedUserRank);
        intent.putExtra(Constants.CLICKED_USER_RIPLE_COUNT, clickedUserRipleCount);
        intent.putExtra(Constants.CLICKED_USER_INFO, clickedUserInfo);

        mContext.startActivity(intent);
    }

    private void messageTheAuthor(int position) {

        String author = data.get(position).getUserObjectId();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", author);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> user, ParseException e) {
                if (e == null) {
                    Intent messageIntent = new Intent(mContext, MessagingActivity.class);
                    messageIntent.putExtra("RECIPIENT_ID", user.get(0).getObjectId());
                    mContext.startActivity(messageIntent);
                } else {
                    Toast.makeText(mContext,
                            "Error finding that user",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class CompletedByViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView displayNameView;
        public ImageView otherProfilePicture;
        public TextView userRankView;
        public TextView userRipleCountView;

        public CompletedByViewHolder(View itemView) {
            super(itemView);

            otherProfilePicture = (ImageView) itemView.findViewById(R.id.other_profile_picture);
            displayNameView = (TextView) itemView.findViewById(R.id.other_display_name);
            userRankView = (TextView) itemView.findViewById(R.id.other_rank);
            userRipleCountView = (TextView) itemView.findViewById(R.id.other_riple_count);

            //Set OCL
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            otherProfilePicture.setOnClickListener(this);
            otherProfilePicture.setOnLongClickListener(this);
        }
        //Update item based on position
        public void update(int position){

            String testVariable = "1";

            CompletedByItem current = data.get(position);

            otherProfilePicture.setImageBitmap(current.parseProfilePicture);
            displayNameView.setText(current.displayName);
            userRankView.setText(current.userRank);

            if (current.userRipleCount.equals(testVariable)) {
                userRipleCountView.setText("with " + current.userRipleCount + " Riple");
            } else userRipleCountView.setText("with " + current.userRipleCount + " Riples");
        }

        @Override
        public void onClick(View v) {
            viewOtherUser(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            Vibrator vb = (Vibrator) mContext.getSystemService(mContext.VIBRATOR_SERVICE);
            vb.vibrate(10);
            openFriendMenu();
            return false;
        }

        public void openFriendMenu() {

            CharSequence friendTitles[] = new CharSequence[]{"Message"};

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
            builder.setTitle("Friend Menu");
            builder.setItems(friendTitles, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int selected) {
                    messageTheAuthor(getAdapterPosition());
                }
            });
            builder.show();
        }
    }
}

