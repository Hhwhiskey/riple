package com.khfire22gmail.riple.model;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
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

import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.activities.ViewDropActivity;
import com.khfire22gmail.riple.activities.ViewUserActivity;
import com.khfire22gmail.riple.fragments.DropsTabFragment;
import com.khfire22gmail.riple.fragments.TrickleTabFragment;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder;

public class DropAdapter extends RecyclerView.Adapter<DropAdapter.DropViewHolder> {

    Context mContext;
    private final String mTabName;
    private LayoutInflater inflater;
    List<DropItem> data = Collections.emptyList();
    public static final String created = "created";
    public static final String drop = "drop";
    public static final String trickle = "trickle";
    public static final String viewUser = "viewUser";

    public DropAdapter(Context context, List<DropItem> data, String tabName){

        mContext = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        mTabName = tabName;
    }

    @Override
    public DropViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Change the inflated card based on which RV is being viewed
        int xmlLayoutId = -1;


        /**
         * THIS ONLY APPLIES TO VIEW USER ADAPTER:
         * We're going to move this comparison logic into onBindViewHolder,
         * where we're going to hide or show different components depending on
         * the relation to the current user.
         * If it's completed, set Text to ripl'd
         * If it's createed, set Text to drop'd
         * and if it's no relation, show the switch
         */

        if (mTabName.equals(created)) {
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
        DropViewHolder viewHolder = new DropViewHolder(view);
        return viewHolder;
    }

    // Get drop associacated with click
    private void getTrickleObjectFromRowToAdd(int position) {
        DropItem interactedDrop = TrickleTabFragment.trickleTabInteractionList.get(position);
        ParseQuery<ParseObject> interactedDropQuery = ParseQuery.getQuery("Drop");
        interactedDropQuery.getInBackground(interactedDrop.getObjectId(), new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    todoDrop(object);
                }
            }
        });
    }

    private void getDropObjectFromRowToRemove(int position) {
        DropItem interactedDrop = DropsTabFragment.dropTabInteractionList.get(position);
        ParseQuery<ParseObject> interactedDropQuery = ParseQuery.getQuery("Drop");
        interactedDropQuery.getInBackground(interactedDrop.getObjectId(), new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    removeFromTodo(object);
                }
            }
        });
    }

    private void getDropObjectFromRowToComplete(final int position) {
        DropItem interactedDrop = DropsTabFragment.dropTabInteractionList.get(position);
        ParseQuery<ParseObject> interactedDropQuery = ParseQuery.getQuery("Drop");
        interactedDropQuery.getInBackground(interactedDrop.getObjectId(), new GetCallback<ParseObject>() {
            public void done(ParseObject dropObject, ParseException e) {
                if (e == null) {
                    String authorId = dropObject.getString("author");
                    completeDrop(dropObject, authorId);
                }
            }
        });


    }



    // Add Drop in question to users "Drops" list
    public static void todoDrop(ParseObject trickleObject) {

        ParseUser user = ParseUser.getCurrentUser();

        ParseRelation <ParseObject> todoRelation1 = user.getRelation("todoDrops");
        todoRelation1.add(trickleObject);
        user.saveInBackground();

        ParseRelation<ParseObject> todoRelation2 = user.getRelation("hasRelationTo");
        todoRelation2.add(trickleObject);
        user.saveInBackground();
    }

    // Remove Drop in question from users "Drops" list
    public void removeFromTodo(ParseObject dropObject) {

        ParseUser user = ParseUser.getCurrentUser();

        ParseRelation<ParseObject> removeRelation1 = user.getRelation("todoDrops");
        removeRelation1.remove(dropObject);
        user.saveInBackground();

        ParseRelation<ParseObject> removeRelation2 = user.getRelation("hasRelationTo");
        removeRelation2.remove(dropObject);
        user.saveInBackground();
    }

    // Add Drop in question to users "Riple" list
    public void completeDrop(ParseObject mDropObject, String dropAuthor) {

        //Increment the user
        ParseUser user = ParseUser.getCurrentUser();

        ParseQuery<ParseUser> authorQuery = ParseUser.getQuery();
        authorQuery.getInBackground(dropAuthor, new GetCallback<ParseUser>() {
            public void done(ParseUser mDropAuthor, ParseException e) {
                if (e == null) {
                    incrementDropAuthorRipleCount(mDropAuthor);
                }
            }
        });

        /* user.increment("userRipleCount");
        user.saveInBackground();*/
        /*mDropAuthor.increment("userRipleCount",1);
        mDropAuthor.saveInBackground();*/


        //Increment the Drop
        mDropObject.increment("ripleCount");
        mDropObject.saveInBackground();

        ParseRelation completeRelation1 = user.getRelation("completedDrops");
        completeRelation1.add(mDropObject);

        ParseRelation completeRelation2 = user.getRelation("todoDrops");
        completeRelation2.remove(mDropObject);
        user.saveInBackground();

        ParseRelation completeRelation3 = user.getRelation("hasRelationTo");
        completeRelation3.add(mDropObject);
        user.saveInBackground();

        //Todo Add completed timestamp and update the data on parse
       /* Date date = new Date();
        Long time = (date.getTime());*/
    }

    public void incrementDropAuthorRipleCount(final ParseUser dropAuthor) {

        dropAuthor.increment("userRipleCount");
        dropAuthor.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                int i = (int) dropAuthor.get("userRipleCount");
                Log.d("Kevin e", "error = " + e);
            }
        });
    }

    public void removeDropFromView(int position) {
        data.remove(position);
        notifyItemRemoved(position);

    }

    @Override
    public void onBindViewHolder(final DropViewHolder viewHolder, final int position) {
        viewHolder.update(position);

        // To-do Toggle Listener
        if (viewHolder.todoSwitch != null) {
            viewHolder.todoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        getTrickleObjectFromRowToAdd(position);
                        removeDropFromView(position);

                        Log.d("checkbox", "Checked");
                    } else {
                        getDropObjectFromRowToRemove(position);
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
                        getDropObjectFromRowToComplete(position);
                        removeDropFromView(position);
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
        viewHolder.parseProfilePicture.setOnClickListener(new View.OnClickListener() {
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

//        viewHolder.share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               shareDrop(position);
//            }
//        });
    }

    // TODO: 11/19/2015 SHare function
    public void shareDrop(int position) {

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("String");// might be text, sound, whatever
        share.putExtra(Intent.EXTRA_STREAM, (data.get(position).getAuthorId()) + " has shared a Drop from Riple! " +
                "A Drop is an idea to make the world a better place. If you have an" +
                " adroid phone you can download Riple from the Play Store and" +
                " start making Riples right now!" + (data.get(position).getDescription()));
        mContext.startActivity(Intent.createChooser(share, "share"));
    }

    public void viewDrop(int position) {
        String mDropObjectId = (data.get(position).getObjectId());
        String mAuthorId = (data.get(position).getAuthorId());
        String mAuthorName = (data.get(position).getAuthorName());
        String mDropDescription = (data.get(position).getDescription());
        String mRipleCount = (data.get(position).getRipleCount());
        String mCommentCount = (data.get(position).getCommentCount());
        Date mCreatedAt = (data.get(position).getCreatedAt());

        Log.d("sViewDropAcitivty", "Send drop's dropObjectId = " + mDropObjectId);
        Log.d("sViewDropAcitivty", "Send drop's authorId = " + mAuthorId);
        Log.d("sViewDropAcitivty", "Send drop's commenterName = " + mAuthorName);
        Log.d("sViewDropAcitivty", "Send drop's dropDescription = " + mDropDescription);
        Log.d("sViewDropAcitivty", "Send drop's ripleCount = " + mRipleCount);
        Log.d("sViewDropAcitivty", "Send drop's commentCount = " + mCommentCount);
        Log.d("sViewDropAcitivty", "Send drop's createdAt = " + mCreatedAt);

        Intent intent = new Intent(mContext, ViewDropActivity.class);
        intent.putExtra("dropObjectId", mDropObjectId);
        intent.putExtra("authorId", mAuthorId);
        intent.putExtra("commenterName", mAuthorName);
        intent.putExtra("dropDescription", mDropDescription);
        intent.putExtra("ripleCount", mRipleCount);
        intent.putExtra("commentCount", mCommentCount);
        intent.putExtra("createdAt", mCreatedAt);

        mContext.startActivity(intent);
    }

    // onClick action for viewing other user
    private void viewOtherUser(int position) {

        String mClickedUserId = (data.get(position).getAuthorId());
        String mClickedUserName = (data.get(position).getAuthorName());

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




    //DropViewHolder//////////////////////////////////////////////////////////////////////////////
    public class DropViewHolder extends AnimateViewHolder  {

        private final Switch todoSwitch;
        private final CheckBox completeCheckBox;
        public TextView authorName;
        public TextView createdAt;
        public TextView description;
        public TextView ripleCount;
        public TextView commentCount;
        public ImageView share;
        private ViewPropertyAnimatorListener listener;
        private ParseUser currentUser;
        private ImageView parseProfilePicture;


        public DropViewHolder(View itemView) {
            super(itemView);

            parseProfilePicture = (ImageView) itemView.findViewById(R.id.profile_picture);
//            share = (ImageView) itemView.findViewById(R.id.share_button);
            todoSwitch = (Switch) itemView.findViewById(R.id.switch_todo);
/*
            if (todoSwitch != null) {
                todoSwitch.setOnCheckedChangeListener(this);
            }*/

            completeCheckBox = (CheckBox) itemView.findViewById(R.id.checkbox_complete);
            createdAt = (TextView) itemView.findViewById(R.id.created_at);
            authorName = (TextView) itemView.findViewById(R.id.name);
            description = (TextView) itemView.findViewById(R.id.description);
            ripleCount = (TextView) itemView.findViewById(R.id.riple_count);
            commentCount = (TextView) itemView.findViewById(R.id.comment_count);


        }

        public void update(int position){

            DropItem current = data.get(position);

            parseProfilePicture.setImageBitmap(current.parseProfilePicture);
            createdAt.setText(String.valueOf(current.createdAt));
            authorName.setText(current.authorName);
            description.setText(current.description);
            ripleCount.setText(String.valueOf(current.ripleCount));
            commentCount.setText(String.valueOf(current.commentCount));
        }

        @Override
        public void animateAddImpl(ViewPropertyAnimatorListener viewPropertyAnimatorListener) {
            ViewCompat.animate(itemView)
                    .translationY(-itemView.getHeight() * 0.3f)
                    .alpha(0)
                    .setDuration(300)
                    .setListener(listener)
                    .start();
        }

        @Override
        public void preAnimateAddImpl() {
            ViewCompat.setTranslationY(itemView, -itemView.getHeight() * 0.3f);
            ViewCompat.setAlpha(itemView, 0);
        }

        @Override
        public void animateRemoveImpl(ViewPropertyAnimatorListener viewPropertyAnimatorListener) {
            ViewCompat.animate(itemView)
                    .translationY(0)
                    .alpha(1)
                    .setDuration(300)
                    .setListener(listener)
                    .start();
        }

        /*@Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            removeDropFromView(getAdapterPosition(), this);
        }

        public void removeDropFromView(int position, DropViewHolder viewholder) {
            data.remove(position);
            notifyItemRemoved(position);

        }*/
    }


}

