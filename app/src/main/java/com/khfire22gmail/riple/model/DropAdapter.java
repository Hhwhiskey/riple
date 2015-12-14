package com.khfire22gmail.riple.model;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.activities.CompletedByActivity;
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

import java.util.Collections;
import java.util.Date;
import java.util.List;

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

    // TODO: 12/10/2015 Add this option with a card overflow menu
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
                    ParseObject dropAuthor = dropObject.getParseObject("authorPointer");
                    completeDropAndIncrement(dropObject, dropAuthor);
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

    //Complete drop and increment the Drop and Author
    public void completeDropAndIncrement(ParseObject mDropObject, ParseObject dropAuthor) {

        final ParseUser currentUser = ParseUser.getCurrentUser();

        //Modify currentUser relations to Drop
        ParseRelation completeRelation1 = currentUser.getRelation("completedDrops");
        completeRelation1.add(mDropObject);

        ParseRelation completeRelation2 = currentUser.getRelation("todoDrops");
        completeRelation2.remove(mDropObject);
        currentUser.saveInBackground();

        ParseRelation completeRelation3 = currentUser.getRelation("hasRelationTo");
        completeRelation3.add(mDropObject);
        currentUser.saveInBackground();

        //Add to completedBy list
        ParseRelation completedByRelation = mDropObject.getRelation("completedBy");
        completedByRelation.add(currentUser);
        mDropObject.saveInBackground();

        //Increment the Drop
        mDropObject.increment("ripleCount");
        mDropObject.saveInBackground();

        //Increment the Author
        ParseQuery ripleCountQuery = ParseQuery.getQuery("UserRipleCount");
        ripleCountQuery.whereEqualTo("userPointer", dropAuthor);
        ripleCountQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                parseObject.increment("ripleCount");
                parseObject.saveInBackground();
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
        if (viewHolder.todoButton != null) {
            viewHolder.todoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getTrickleObjectFromRowToAdd(position);
//                    removeDropFromView(position);
                }
            });
        }

        // Complete CheckBox Listener
        if (viewHolder.completeButton != null) {
            viewHolder.completeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDropObjectFromRowToComplete(position);
//                    removeDropFromView(position);
                    Log.d("checkbox", "Checked");

                    Log.d("checkbox", "UnChecked");
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
                viewCompletedBy(position);
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

    private void viewCompletedBy(int position){

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

        Intent intent = new Intent(mContext, CompletedByActivity.class);
        intent.putExtra("dropObjectId", mDropObjectId);
        intent.putExtra("authorId", mAuthorId);
        intent.putExtra("commenterName", mAuthorName);
        intent.putExtra("dropDescription", mDropDescription);
        intent.putExtra("ripleCount", mRipleCount);
        intent.putExtra("commentCount", mCommentCount);
        intent.putExtra("createdAt", mCreatedAt);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }






    //DropViewHolder//////////////////////////////////////////////////////////////////////////////
    public class DropViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final Button todoButton;
        private final Button completeButton;
        public TextView authorName;
        public TextView createdAt;
        public TextView description;
        public TextView ripleCount;
        public TextView commentCount;
        private ImageView parseProfilePicture;


        public DropViewHolder(View itemView) {
            super(itemView);

            parseProfilePicture = (ImageView) itemView.findViewById(R.id.profile_picture);
            todoButton = (Button) itemView.findViewById(R.id.button_todo);
            completeButton = (Button) itemView.findViewById(R.id.button_complete);
            createdAt = (TextView) itemView.findViewById(R.id.created_at);
            authorName = (TextView) itemView.findViewById(R.id.name);
            description = (TextView) itemView.findViewById(R.id.description);
            ripleCount = (TextView) itemView.findViewById(R.id.riple_count);
            commentCount = (TextView) itemView.findViewById(R.id.comment_count);

            itemView.setOnClickListener(this);
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
        public void onClick(View v) {
            viewDrop(getAdapterPosition());
        }
    }


}

