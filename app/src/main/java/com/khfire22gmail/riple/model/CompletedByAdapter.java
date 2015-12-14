package com.khfire22gmail.riple.model;

/**
 * Created by Kevin on 12/13/2015.
 */
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.activities.CompletedByActivity;
import com.khfire22gmail.riple.activities.ViewUserActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompletedByAdapter extends RecyclerView.Adapter<CompletedByAdapter.CompletedByViewHolder> {

    Context mContext;

    //    private final String mTabName;
    private LayoutInflater inflater;
    List<CompletedByItem> data = Collections.emptyList();

    public CompletedByAdapter(CompletedByActivity context, ArrayList<CompletedByItem> data) {
        mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;

    }

    @Override
    public CompletedByViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_completed_by, parent, false);
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

        /*viewHolder.parseProfilePicture.setOnClickListener(new View.OnClickListener() {
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

        String mClickedUserId = (data.get(position).getUserObjectId());
        String mClickedUserName = (data.get(position).getDisplayName());

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

    class CompletedByViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView displayName;
        public ImageView parseProfilePicture;

        public CompletedByViewHolder(View itemView) {
            super(itemView);

            parseProfilePicture = (ImageView) itemView.findViewById(R.id.completed_by_profile_picture);
            displayName = (TextView) itemView.findViewById(R.id.completed_by_display_name);


            itemView.setOnClickListener(this);
//            itemView.setLongClickable(true);
//            itemView.setOnLongClickListener(this);
        }

        public void update(int position){

            CompletedByItem current = data.get(position);

            parseProfilePicture.setImageBitmap(current.parseProfilePicture);
            displayName.setText(current.displayName);


        }

        @Override
        public void onClick(View v) {
            viewOtherUser(getAdapterPosition());

        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }
}

