package com.khfire22gmail.riple.model;

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
import com.khfire22gmail.riple.activities.ViewUserActivity;

import java.util.Collections;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    Context mContext;

    //    private final String mTabName;
    private LayoutInflater inflater;
    List<CommentItem> data = Collections.emptyList();



    public CommentAdapter(Context context, List<CommentItem> data){

        mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;

    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_comment, parent, false);
        CommentViewHolder viewHolder = new CommentViewHolder(view);
        return viewHolder;
    }

    private void viewOtherUser(int position) {

        String mClickedUserId = (data.get(position).getCommenterId());
        String mClickedUserName = (data.get(position).getCommenterName());

        Log.d("sCommentViewUser", "Clicked User's userObjectId = " + mClickedUserId);
        Log.d("sCommentViewUser", "Clicked User's clickedUserName = " + mClickedUserName);

        Intent intent = new Intent(mContext, ViewUserActivity.class);
        intent.putExtra("clickedUserId", mClickedUserId);
        intent.putExtra("clickedUserName", mClickedUserName);
        mContext.startActivity(intent);
    }


    @Override
    public void onBindViewHolder(CommentViewHolder viewHolder, final int position) {
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

    @Override
    public int getItemCount() {
        return data.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView createdAt;
        public TextView commentText;
        public TextView commenterName;
        public TextView commenterRipleCount;
        public ImageView parseProfilePicture;

        public CommentViewHolder(View itemView) {
            super(itemView);

            parseProfilePicture = (ImageView) itemView.findViewById(R.id.commenter_profile_picture);
            createdAt = (TextView) itemView.findViewById(R.id.comment_created_at);
            commenterName = (TextView) itemView.findViewById(R.id.commenter_name);
            commentText = (TextView) itemView.findViewById(R.id.comment_text);
            commenterRipleCount = (TextView) itemView.findViewById(R.id.commenter_rank);

            itemView.setOnClickListener(this);
//            itemView.setLongClickable(true);
//            itemView.setOnLongClickListener(this);
        }

        public void update(int position){

            CommentItem current = data.get(position);

            parseProfilePicture.setImageBitmap(current.parseProfilePicture);
            commenterName.setText(current.commenterName);
            commentText.setText(current.commentText);
            createdAt.setText(String.valueOf(current.createdAt));
            commenterRipleCount.setText(String.valueOf(current.commenterRank));

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



