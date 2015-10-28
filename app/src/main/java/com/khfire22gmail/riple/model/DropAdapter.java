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

import com.facebook.login.widget.ProfilePictureView;
import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.actions.ViewDropActivity;
import com.khfire22gmail.riple.actions.ViewOtherUserActivity;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

public class DropAdapter extends RecyclerView.Adapter<DropAdapter.MyViewHolder> {

    Context mContext;

    private final String mTabName;
    private LayoutInflater inflater;
    List<DropItem> data = Collections.emptyList();



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


    public static final String RIPLE = "riple";
    public static final String DROP = "drop";
    public static final String TRICKLE = "trickle";
    public static final String OTHER = "other";



    /*  One idea is to pass a param where you can choose which
     xml layout you want inflated by the adapter*/

    public DropAdapter(Context context, List<DropItem> data, String tabName){
        mContext = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        mTabName = tabName;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int xmlLayoutId = -1;

        if (mTabName.equals(RIPLE)) {
            xmlLayoutId = R.layout.card_riple;

        } else if (mTabName.equals(DROP)) {
            xmlLayoutId = R.layout.card_drop;

        } else if (mTabName.equals(TRICKLE)) {
            xmlLayoutId = R.layout.card_trickle;

        } else if (mTabName.equals(OTHER)) {
            xmlLayoutId = R.layout.card_riple;
        }

        // DRY - DON'T REPEAT YOURSELF
        View view = inflater.inflate(xmlLayoutId, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, final int position) {
        viewHolder.update(position);

        viewHolder.description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDrop(position);
            }
        });



        /*Button button = (Button) findViewById(R.id.share_button);

        viewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDrop(position);
            }
        });*/

        viewHolder.profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewOtherUser(position);
            }
        });

        viewHolder.authorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewOtherUser(position);
            }
        });
    }

    private void viewDrop(int position) {
        String mObjectId = (data.get(position).getObjectId());
        String mAuthorId = (data.get(position).getAuthorId());
        String mAuthorName = (data.get(position).getAuthorName());
        String mFacebookId = (data.get(position).getFacebookId());
        String mDescription = (data.get(position).getDescription());

        Log.d("CLICKEDDROPEXTRA", "Clicked drop's objectId = " + mObjectId);
        Log.d("CLICKEDDROPEXTRA", "Clicked drop's authorId = " + mAuthorId);
        Log.d("CLICKEDDROPEXTRA", "Clicked drop's authorName = " + mAuthorName);
        Log.d("CLICKEDDROPEXTRA", "Clicked drop's facebookId = " + mFacebookId);
        Log.d("CLICKEDDROPEXTRA", "Clicked drop's description = " + mDescription);

        Intent intent = new Intent(mContext, ViewDropActivity.class);
            intent.putExtra("objectId", mObjectId);
            intent.putExtra("authorId", mAuthorId);
            intent.putExtra("authorName", mAuthorName);
            intent.putExtra("facebookId", mFacebookId);
            intent.putExtra("description", mDescription);

        mContext.startActivity(intent);
    }

    /*private void shareDrop(int position) {
        String mDropId = (data.get(position).getObjectId());

        Intent intent = new Intent(mContext, ShareDrop.class);
        intent.putExtra("objectId", mDropId);
        mContext.startActivity(intent);
    }*/

    // onClick action for viewing other user
    private void viewOtherUser(int position) {
//        String mObjectId = (data.get(position).getObjectId());
        String mAuthorId = (data.get(position).getAuthorId());
        String mAuthorName = (data.get(position).getAuthorName());
        String mFacebookId = (data.get(position).getFacebookId());

//        Log.d("OTHERUSEREXTRA", "Clicked User's objectId = " + mObjectId);
        Log.d("OTHERUSEREXTRA", "Clicked User's authorId = " + mAuthorId);
        Log.d("OTHERUSEREXTRA", "Clicked User's authorName = " + mAuthorName);
        Log.d("OTHERUSEREXTRA", "Clicked User's facebookId = " + mFacebookId);

        Intent intent = new Intent(mContext, ViewOtherUserActivity.class);
//            intent.putExtra("objectId", mObjectId);
            intent.putExtra("author", mAuthorId);
            intent.putExtra("name", mAuthorName);
            intent.putExtra("facebookId", mFacebookId);
            mContext.startActivity(intent);
    }

    /*public String passClickExtraData(int position) {
        String mObjectId = (data.get(position).getObjectId());
        String mAuthorId = (data.get(position).getAuthorId());
        String mAuthorName = (data.get(position).getAuthorName());
        String mFacebookId = (data.get(position).getFacebookId());

         return;
    }*/

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ProfilePictureView profilePicture;
        public TextView authorName;
        public TextView createdAt;
        public TextView description;
        public TextView ripleCount;
        public TextView commentCount;
        public ImageView share;
//        public TextView commenter;
//        public TextView comment;

        public MyViewHolder(View itemView) {

            super(itemView);

            profilePicture = (ProfilePictureView) itemView.findViewById(R.id.profile_picture);
            createdAt = (TextView) itemView.findViewById(R.id.created_at);
            authorName = (TextView) itemView.findViewById(R.id.name);
            description = (TextView) itemView.findViewById(R.id.description);
            ripleCount = (TextView) itemView.findViewById(R.id.riple_count);
            commentCount = (TextView) itemView.findViewById(R.id.comment_count);
//            share = (ImageView) itemView.findViewById(R.id.share_button);

//            commenter = (TextView) itemView.findViewById(R.id.commenter);
//            comment = (TextView) itemView.findViewById(R.id.comment);

            itemView.setOnClickListener(this);
        }

        public void update(int position){

            DropItem current = data.get(position);

            profilePicture.setProfileId(current.facebookId);
            createdAt.setText(String.valueOf(current.createdAt));
            authorName.setText(current.authorName);
            description.setText(current.description);
            ripleCount.setText(String.valueOf(current.ripleCount));
            commentCount.setText(String.valueOf(current.commentCount));
//            share.setT(current.objectId();
//            commenter.setText(current.commenter);
//            comment.setText(current.comment);
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


