package com.khfire22gmail.riple.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.khfire22gmail.riple.R;

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


    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, final int position) {
        viewHolder.update(position);

        /*viewHolder.description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDrop(position);
            }
        });*/



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
//                viewOtherUser(position);
            }
        });

        viewHolder.commenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                viewOtherUser(position);
            }
        });
    }

    /*private void viewDrop(int position) {
        String mDropId = (data.get(position).getObjectId());

        String mAuthorId = (data.get(position).getAuthorId());
        String mAuthorName = (data.get(position).getAuthorName());
        String mFacebookId = (data.get(position).getFacebookId());
        String mCommenter = (data.get(position).getCommenter());

        Intent intent = new Intent(mContext, ViewDropActivity.class);
        intent.putExtra("objectId", mDropId);
        intent.putExtra("authorId", mAuthorId);
        intent.putExtra("authorName", mAuthorName);
        intent.putExtra("facebookId", mFacebookId);
        intent.putExtra("commenter", mCommenter);
        mContext.startActivity(intent);
    }
*/
    /*private void shareDrop(int position) {
        String mDropId = (data.get(position).getObjectId());

        Intent intent = new Intent(mContext, ShareDrop.class);
        intent.putExtra("objectId", mDropId);
        mContext.startActivity(intent);
    }*/

    // onClick action for viewing other user
    /*private void viewOtherUser(int position) {
        String mAuthorId = (data.get(position).getAuthorId());
        String mAuthorName = (data.get(position).getAuthorName());
        String mFacebookId = (data.get(position).getFacebookId());

        Log.d("Kevin", "Clicked User's authorId = " + mAuthorId);
        Log.d("Kevin", "Clicked User's authorName = " + mAuthorName);
        Log.d("Kevin", "Clicked User's facebookId = " + mFacebookId);

        Intent intent = new Intent(mContext, ViewOtherUserActivity.class);
        intent.putExtra("authorId", mAuthorId);
        intent.putExtra("authorName", mAuthorName);
        intent.putExtra("facebookId", mFacebookId);
        mContext.startActivity(intent);
    }*/

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
        public TextView createdAt;
        public TextView description;
        public TextView commenter;
        public TextView comment;
        public ImageView share;

        public MyViewHolder(View itemView) {

            super(itemView);

            profilePicture = (ProfilePictureView) itemView.findViewById(R.id.commenter_profile_picture);
            createdAt = (TextView) itemView.findViewById(R.id.created_at);
            commenter = (TextView) itemView.findViewById(R.id.commenter);
            comment = (TextView) itemView.findViewById(R.id.comment);
            //            share = (ImageView) itemView.findViewById(R.id.share_button);

            itemView.setOnClickListener(this);
        }

        public void update(int position){

           CommentItem current = data.get(position);

            profilePicture.setProfileId(current.facebookId);
            createdAt.setText(String.valueOf(current.createdAt));
            commenter.setText(current.commenter);
            comment.setText(current.comment);
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


