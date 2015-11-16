package com.khfire22gmail.riple.model;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.actions.ViewUserActivity;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    Context mContext;

    //    private final String mTabName;
    private LayoutInflater inflater;
    List<CommentItem> data = Collections.emptyList();

    public static interface TrickleAdapterDelegate {
        public void itemSelected(Object item);
    }

    WeakReference<TrickleAdapterDelegate> delegate;

    public TrickleAdapterDelegate getDelegate() {
        if (delegate == null) {
            return null;
        }
        return delegate.get();
    }

    public void setDelegate(TrickleAdapterDelegate delegate) {
        this.delegate = new WeakReference<>(delegate);
    }


    public Object getItem(int position){
        return data.get(position);
    }

    public CommentAdapter(Context context, List<CommentItem> data){

        this.inflater = LayoutInflater.from(context);
        this.data = data;

    }


    /*public static final String RIPLE = "riple";
    public static final String DROP = "drop";
    public static final String TRICKLE = "trickle";
    public static final String OTHER = "other";*/



    /*  One idea is to pass a param where you can choose which
     xml layout you want inflated by the adapter*/

   /* public CommentAdapter(Context context, List<DropItem> data){
        mContext = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        mTabName = tabName;
    }*/

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_comment, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    private void viewOtherUser(int position) {

        String mClickedUserId = (data.get(position).getCommenterId());
        String mClickedUserName = (data.get(position).getCommenterName());
        String mClickedUserFacebookId = (data.get(position).getFacebookId());

        Log.d("sCommentViewUser", "Clicked User's userId = " + mClickedUserId);
        Log.d("sCommentViewUser", "Clicked User's clickedUserName = " + mClickedUserName);
        Log.d("sCommentViewUser", "Clicked User's clickedFacebookId = " + mClickedUserFacebookId);

        Intent intent = new Intent(mContext, ViewUserActivity.class);
        intent.putExtra("clickedUserId", mClickedUserId);
        intent.putExtra("clickedUserName", mClickedUserName);
        intent.putExtra("clickedUserFacebookId", mClickedUserFacebookId);
        mContext.startActivity(intent);
    }


    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, final int position) {
        viewHolder.update(position);

        viewHolder.profilePicture.setOnClickListener(new View.OnClickListener() {
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
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ProfilePictureView profilePicture;
        public TextView createdAt;
        public TextView commentText;
        public TextView commenterName;

        public MyViewHolder(View itemView) {

            super(itemView);

            profilePicture = (ProfilePictureView) itemView.findViewById(R.id.commenter_profile_picture);
            createdAt = (TextView) itemView.findViewById(R.id.created_at);
            commenterName = (TextView) itemView.findViewById(R.id.commenter);
            commentText = (TextView) itemView.findViewById(R.id.commentText);

            itemView.setOnClickListener(this);
        }

        public void update(int position){

            CommentItem current = data.get(position);

            profilePicture.setProfileId(current.facebookId);
            createdAt.setText(String.valueOf(current.createdAt));
            commenterName.setText(current.commenterName);
            commentText.setText(current.commentText);
//            share.setT(current.objectId();
        }

        /*@Override
        public void onClick(View v) {
            if (View v ){
                mListener.onTomato((ImageView)v)
            } else {
                mListener.onPotato(v);
            }
        }*/

        String adapterPos = String.valueOf((getAdapterPosition()));

        @Override
        public void onClick(View v) {
//            Toast.makeText(this, "The Item Clicked is: " + adapterPos, Toast.LENGTH_SHORT).show();
            getDelegate().itemSelected(data.get(getAdapterPosition()));
           /* Intent intent = new Intent(this, ViewDropActivity.class);
            startActivity(intent);*/


        }
    }
}



