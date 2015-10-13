
package com.khfire22gmail.riple.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.khfire22gmail.riple.R;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

public class DropAdapter extends RecyclerView.Adapter<DropAdapter.MyViewHolder> {

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


    /**
     * One idea is to pass a param where you can choose which
     * xml layout you want inflated by the adapter
     */

    public DropAdapter(Context context, List<DropItem> data, String tabName){
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
            xmlLayoutId = R.layout.card_trickle;

        } else if (mTabName.equals(TRICKLE)) {
            xmlLayoutId = R.layout.card_trickle;
        }

        // DRY - DON'T REPEAT YOURSELF
        View view = inflater.inflate(xmlLayoutId, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        viewHolder.update(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

//        public ImageView trickleProfilePic;
        public ProfilePictureView profilePicture;
        public TextView author;
        public TextView createdAt;
        public TextView title;
        public TextView description;
        public TextView ripleCount;
        public TextView commentCount;
//        public TextView commenter;
//        public TextView comment;

        public MyViewHolder(View itemView) {

            super(itemView);

            profilePicture = (ProfilePictureView) itemView.findViewById(R.id.profile_picture);
            author = (TextView) itemView.findViewById(R.id.author);
            createdAt = (TextView) itemView.findViewById(R.id.created_at);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            ripleCount = (TextView) itemView.findViewById(R.id.riple_count);
            commentCount = (TextView) itemView.findViewById(R.id.comment_count);
//            commenter = (TextView) itemView.findViewById(R.id.commenter);
//            comment = (TextView) itemView.findViewById(R.id.comment);

            itemView.setOnClickListener(this);
        }

        public void update(int position){

            DropItem current = data.get(position);

            profilePicture.setProfileId(current.facebookId);
            author.setText(current.author);
            createdAt.setText((CharSequence) current.createdAt);
            title.setText(current.title);
            description.setText(current.description);
            ripleCount.setText(String.valueOf(current.ripleCount));
            commentCount.setText(String.valueOf(current.commentCount));
//            commenter.setText(current.commenter);
//            comment.setText(current.comment);
        }

        @Override
        public void onClick(View v) {

            getDelegate().itemSelected(data.get(getPosition()));
        }
    }
}


