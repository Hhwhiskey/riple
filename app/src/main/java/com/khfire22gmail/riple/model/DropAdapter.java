package com.khfire22gmail.riple.model;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.google.gson.Gson;
import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.actions.ViewDropActivity;
import com.khfire22gmail.riple.actions.ViewUserActivity;
import com.khfire22gmail.riple.tabs.DropsTabFragment;
import com.khfire22gmail.riple.tabs.TrickleTabFragment;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Date;
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

    public static final String riple = "riple";
    public static final String drop = "drop";
    public static final String trickle = "trickle";
    public static final String viewUser = "other";

    public DropAdapter(Context context, List<DropItem> data, String tabName){
        mContext = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        mTabName = tabName;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

       // Change the inflated card based on which RV is being viewed
        int xmlLayoutId = -1;

        if (mTabName.equals(riple)) {
            xmlLayoutId = R.layout.card_created;

        } else if (mTabName.equals(drop)) {
            xmlLayoutId = R.layout.card_drop;

        } else if (mTabName.equals(trickle)) {
            xmlLayoutId = R.layout.card_trickle;

        //Todo Show card in ViewDropActivity based on users relation to that Drop
        } else if (mTabName.equals(viewUser)) {
            xmlLayoutId = R.layout.card_created;
        }

        View view = inflater.inflate(xmlLayoutId, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }




    // Get drop associated with action
    private ParseObject getDropObjectFromRow(int position) {
        ParseObject mDrop = DropsTabFragment.dropObjectsList.get(position);
        return mDrop;
    }

    // Get drop associacated with click
    private ParseObject getTrickleObjectFromRow(int position) {
        ParseObject trickle = TrickleTabFragment.trickleObjectsList.get(position);
        return trickle;
    }

    // Add Drop in question to users "Drops" list
    public void todoDrop(ParseObject trickleObject) {

        ParseUser user = ParseUser.getCurrentUser();
        ParseRelation relation = user.getRelation("todoDrops");
        relation.add(trickleObject);
        user.saveInBackground();
    }

    // Remove Drop in question from users "Drops" list
    public void removeFromTodo(ParseObject dropObject) {

        ParseUser user = ParseUser.getCurrentUser();
        ParseRelation removeFromRelation = user.getRelation("todoDrops");
        removeFromRelation.remove(dropObject);
        user.saveInBackground();
    }

    // Add Drop in question to users "Riple" list
    public void completeDrop(ParseObject dropObject) {

        ParseUser user = ParseUser.getCurrentUser();
        ParseRelation addToRelation = user.getRelation("completedDrops");
        addToRelation.add(dropObject);
        ParseRelation removeFromRelation = user.getRelation("todoDrops");
        removeFromRelation.remove(dropObject);
        user.saveInBackground();

        //Todo Add completed timestamp and update the data on parse
       /* Date date = new Date();
        Long time = (date.getTime());*/
    }



    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        viewHolder.update(position);

        // To-do Toggle Listener
        if (viewHolder.todoSwitch != null) {
            viewHolder.todoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        todoDrop(getTrickleObjectFromRow(position));
                        Log.d("checkbox", "Checked");
                    } else {
                       removeFromTodo(getDropObjectFromRow(position));
                    }
                }
            });
        }

        // Complete CheckBox Listener
        if (viewHolder.completeCheckBox != null) {
            viewHolder.completeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                       completeDrop(getDropObjectFromRow(position));
                        Log.d("checkbox", "Checked");
                    } else {
                        Log.d("checkbox", "UnChecked");
                    }
                }
            });
        }

        // View all other Drop Clicks
        viewHolder.description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDrop(position);
            }
        });

        viewHolder.ripleCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDrop(position);
            }
        });

        viewHolder.commentCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDrop(position);
            }
        });

        viewHolder.createdAt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDrop(position);
            }
        });

        // View otherUser Clicks
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

        /*viewHolder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //Todo  shareDrop(position);
            }
        });*/
    }

    private void viewDrop(int position) {
        String mObjectId = (data.get(position).getObjectId());
        String mAuthorId = (data.get(position).getAuthorId());
        String mAuthorName = (data.get(position).getAuthorName());
        String mFacebookId = (data.get(position).getFacebookId());
        String mDescription = (data.get(position).getDescription());
        String mRipleCount = (data.get(position).getRipleCount());
        String mCommentCount = (data.get(position).getCommentCount());
        Date mCreatedAt = (data.get(position).getCreatedAt());

        Log.d("sDropExtra", "Send drop's objectId = " + mObjectId);
        Log.d("sDropExtra", "Send drop's commenter = " + mAuthorId);
        Log.d("sDropExtra", "Send drop's authorName = " + mAuthorName);
        Log.d("sDropExtra", "Send drop's facebookId = " + mFacebookId);
        Log.d("sDropExtra", "Send drop's description = " + mDescription);
        Log.d("sDropExtra", "Send drop's ripleCount = " + mRipleCount);
        Log.d("sDropExtra", "Send drop's commentCount = " + mCommentCount);
        Log.d("sDropExtra", "Send drop's createdAt = " + mCreatedAt);

        Intent intent = new Intent(mContext, ViewDropActivity.class);
            intent.putExtra("objectId", mObjectId);
            intent.putExtra("commenter", mAuthorId);
            intent.putExtra("authorName", mAuthorName);
            intent.putExtra("facebookId", mFacebookId);
            intent.putExtra("description", mDescription);
            intent.putExtra("ripleCount", mRipleCount);
            intent.putExtra("commentCount", mCommentCount);
            intent.putExtra("createdAt", mCreatedAt);
/*
        Bundle bundle = new Bundle(mContext, ViewDropActivity.class);
            bundle.putSerializable("createdAt", mCreatedAt);*/

        mContext.startActivity(intent);
    }

    // onClick action for viewing other user
    private void viewOtherUser(int position) {

        Object mObjectId = (data.get(position).getObjectId());
        String mAuthorId = (data.get(position).getAuthorId());
        String mAuthorName = (data.get(position).getAuthorName());
        String mFacebookId = (data.get(position).getFacebookId());

        Log.d("OTHERUSEREXTRA", "Clicked User's objectId = " + mObjectId);
        Log.d("OTHERUSEREXTRA", "Clicked User's author = " + mAuthorId);
        Log.d("OTHERUSEREXTRA", "Clicked User's authorName = " + mAuthorName);
        Log.d("OTHERUSEREXTRA", "Clicked User's facebookId = " + mFacebookId);

        Intent intent = new Intent(mContext, ViewUserActivity.class);

            Gson gson = new Gson();
            intent.putExtra("objectId", gson.toJson(mObjectId));
            intent.putExtra("author", mAuthorId);
            intent.putExtra("name", mAuthorName);
            intent.putExtra("facebookId", mFacebookId);
            mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }



    class MyViewHolder extends RecyclerView.ViewHolder {

        private final Switch todoSwitch;
        private final CheckBox completeCheckBox;
        public ProfilePictureView profilePicture;
        public TextView authorName;
        public TextView createdAt;
        public TextView description;
        public TextView ripleCount;
        public TextView commentCount;
        public ImageView share;

        public MyViewHolder(View itemView) {

            super(itemView);
//            share = (ImageView) itemView.findViewById(R.id.share_button);
            todoSwitch = (Switch) itemView.findViewById(R.id.switch_todo);
            completeCheckBox = (CheckBox) itemView.findViewById(R.id.checkbox_complete);
            profilePicture = (ProfilePictureView) itemView.findViewById(R.id.profile_picture);
            createdAt = (TextView) itemView.findViewById(R.id.created_at);
            authorName = (TextView) itemView.findViewById(R.id.name);
            description = (TextView) itemView.findViewById(R.id.description);
            ripleCount = (TextView) itemView.findViewById(R.id.riple_count);
            commentCount = (TextView) itemView.findViewById(R.id.comment_count);
        }

        public void update(int position){

            DropItem current = data.get(position);

            profilePicture.setProfileId(current.facebookId);
            createdAt.setText(String.valueOf(current.createdAt));
            authorName.setText(current.authorName);
            description.setText(current.description);
            ripleCount.setText(String.valueOf(current.ripleCount));
            commentCount.setText(String.valueOf(current.commentCount));
        }
    }
}


