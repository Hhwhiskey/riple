package com.khfire22gmail.riple.model;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.activities.CompletedActivity;
import com.khfire22gmail.riple.activities.MessagingActivity;
import com.khfire22gmail.riple.activities.SettingsActivity;
import com.khfire22gmail.riple.activities.ViewDropActivity;
import com.khfire22gmail.riple.activities.ViewUserActivity;
import com.khfire22gmail.riple.fragments.DropsTabFragment;
import com.khfire22gmail.riple.fragments.TrickleTabFragment;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.Collections;
import java.util.Date;
import java.util.List;


public class DropAdapter extends RecyclerView.Adapter<DropAdapter.DropViewHolder> {


    Context mContext;
    public final String mTabName;
    private LayoutInflater inflater;
    public List<DropItem> data = Collections.emptyList();
    public static final String riple = "riple";
    public static final String drop = "drop";
    public static final String trickle = "trickle";
    public static final String viewUser = "viewUser";
    public ParseUser currentUser;
    private ParseFile parseProfilePictureCheck;
    private String displayNameCheck;
    private ContentResolver contentResolver;
    private Bitmap storedImage;
    private boolean storeImage;
    private boolean stop;
    private Context applicationContext;

    public DropAdapter(Context context, List<DropItem> data, String tabName) {
        mContext = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        mTabName = tabName;
    }

    @Override
    public DropViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        currentUser = ParseUser.getCurrentUser();

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

        if (mTabName.equals(riple)) {
            xmlLayoutId = R.layout.card_riple;

        } else if (mTabName.equals(drop)) {
            xmlLayoutId = R.layout.card_drop;

        } else if (mTabName.equals(trickle)) {
            xmlLayoutId = R.layout.card_trickle;

            //Todo Show card in ViewDropActivity based on users relation to that Drop
        } else if (mTabName.equals(viewUser)) {
            xmlLayoutId = R.layout.card_riple;
        }

        View view = inflater.inflate(xmlLayoutId, parent, false);
        return new DropViewHolder(view);
    }

    // Get Drop info from Trickle list for add
    private void getTrickleObjectFromRowToAdd(int position) {
        DropItem interactedDrop = TrickleTabFragment.allDropsList.get(position);
        ParseQuery<ParseObject> interactedDropQuery = ParseQuery.getQuery("Drop");
        interactedDropQuery.getInBackground(interactedDrop.getObjectId(), new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    todoDrop(object);
                }
            }
        });
    }

    // Get Drop info from Drop list for remove
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

    // Get Drop info from Drop list for complete
    private void getDropObjectFromRowToComplete(final int position) {
        DropItem interactedDrop = DropsTabFragment.dropTabInteractionList.get(position);
        ParseQuery<ParseObject> interactedDropQuery = ParseQuery.getQuery("Drop");
        interactedDropQuery.getInBackground(interactedDrop.getObjectId(), new GetCallback<ParseObject>() {
            public void done(ParseObject dropObject, ParseException e) {
                if (e == null) {
                    ParseObject dropAuthorPointer = dropObject.getParseObject("authorPointer");
                    completeDropAndIncrement(dropObject, dropAuthorPointer);
                }
            }
        });
    }

    // Add Drop in question to users "Drops" list
    public static void todoDrop(ParseObject trickleObject) {

        ParseUser user = ParseUser.getCurrentUser();

        ParseRelation<ParseObject> todoRelation1 = user.getRelation("todoDrops");
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
        ParseRelation completeRelation3 = currentUser.getRelation("hasRelationTo");
        completeRelation3.add(mDropObject);

        currentUser.saveEventually();

        //Add to completedBy list
        ParseRelation completedByRelation = mDropObject.getRelation("completedBy");
        completedByRelation.add(currentUser);
        //Increment the Drop
        mDropObject.increment("ripleCount");

        mDropObject.saveEventually();

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


    @Override
    public void onBindViewHolder(final DropViewHolder viewHolder, final int position) {
        viewHolder.update(position);

//        viewHolder.description.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewDrop(position);
//            }
//        });
//
//        viewHolder.ripleCount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewCompletedBy(position);
//            }
//        });
//
//        viewHolder.commentCount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewDrop(position);
//            }
//        });
//
//        viewHolder.createdAt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewDrop(position);
//            }
//        });
//
//        viewHolder.commenterParseProfilePicture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                viewOtherUser(position);
//            }
//        });
//
//        viewHolder.authorName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewOtherUser(position);
//            }
//        });
//
//        viewHolder.authorRank.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewOtherUser(position);
//            }
//        });

    }

    public void viewDrop(int position) {
        String mDropObjectId = (data.get(position).getObjectId());
        String mAuthorId = (data.get(position).getAuthorId());
        String mAuthorName = (data.get(position).getAuthorName());
        String mAuthorRank = (data.get(position).getAuthorRank());
        String mDropDescription = (data.get(position).getDescription());
        String mRipleCount = (data.get(position).getRipleCount());
        String mCommentCount = (data.get(position).getCommentCount());
        String mPosition = String.valueOf((data.get(position)));
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
        intent.putExtra("authorRank", mAuthorRank);
        intent.putExtra("commenterName", mAuthorName);
        intent.putExtra("dropDescription", mDropDescription);
        intent.putExtra("ripleCount", mRipleCount);
        intent.putExtra("commentCount", mCommentCount);
        intent.putExtra("createdAt", mCreatedAt);
        intent.putExtra("mTabName", mTabName);
        intent.putExtra("mPosition", mPosition);

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

    private void viewCompletedBy(int position) {

        String mDropObjectId = (data.get(position).getObjectId());
        String mAuthorId = (data.get(position).getAuthorId());
        String mAuthorName = (data.get(position).getAuthorName());
        String mAuthorRank = (data.get(position).getAuthorRank());
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

        Intent intent = new Intent(mContext, CompletedActivity.class);
        intent.putExtra("dropObjectId", mDropObjectId);
        intent.putExtra("authorId", mAuthorId);
        intent.putExtra("commenterName", mAuthorName);
        intent.putExtra("authorRank", mAuthorRank);
        intent.putExtra("dropDescription", mDropDescription);
        intent.putExtra("ripleCount", mRipleCount);
        intent.putExtra("commentCount", mCommentCount);
        intent.putExtra("createdAt", mCreatedAt);

        mContext.startActivity(intent);
    }


    private void messageTheAuthor(int position) {

        String author = data.get(position).getAuthorId();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", author);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> user, ParseException e) {
                if (e == null) {
                    Intent messageIntent = new Intent(mContext, MessagingActivity.class);
                    mContext.startActivity(messageIntent);
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


    //Report the Drop author and store the Drop in question in UserReportCount table
    public void reportDropAuthor(final int position) {

        final String dropObjectId = data.get(position).getObjectId();

        //Get Drop data, which includes the Author pointer
        ParseQuery parseQuery = ParseQuery.getQuery("Drop");
        parseQuery.whereEqualTo("objectId", dropObjectId);
        parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject dropObject, ParseException e) {
                if (e == null) {
                    //Get author pointer out of Drop
                    final ParseObject reportedDropAuthorPointer = dropObject.getParseObject("authorPointer");

                    //Get author for report
                    ParseQuery reportQuery = ParseQuery.getQuery("UserReportCount");
                    reportQuery.whereEqualTo("userPointer", reportedDropAuthorPointer);
                    reportQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(final ParseObject reportedUser, ParseException e) {
                            //Increment the author's report count and save mark the Drop in
                            ParseRelation reportRelation = reportedUser.getRelation("reportedDrops");
                            reportRelation.add(dropObject);
                            reportedUser.increment("reportCount");
                            reportedUser.saveEventually();
                        }
                    });
                }
            }
        });
    }

    public void shareToFacebook(int position) {
        String displayName = currentUser.getString("displayName");
        String shareAuthor = data.get(position).getAuthorName();
        String shareDescription = data.get(position).getDescription();
        Bitmap sharedImage = data.get(position).getParseProfilePicture();

        ShareDialog shareDialog;
        FacebookSdk.sdkInitialize(mContext);
        shareDialog = new ShareDialog((Activity) mContext);

        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle("Make a Riple...")
                .setContentDescription(displayName + " shared " + shareAuthor + "'s" + " Drop from \"Riple\":\n\n" + "\"" + shareDescription + "\"\n\n" + "If you have an Android device you can download \"Riple\" now and start making Riples of your own. Click the link to get started!\n" + "facebook.com/kevinhodges0")
                .setContentUrl(Uri.parse("https://play.google.com/store/search?q=pub:Google%20Inc."))
                .setImageUrl(Uri.parse("https://scontent-ord1-1.xx.fbcdn.net/hphotos-xap1/v/t1.0-9/923007_665600676799506_1701143490_n.png?oh=9a224427d5c5807ed0db56582363057b&oe=57079C4E"))
                .build();

        shareDialog.show(linkContent);
    }

    // TODO: 11/19/2015 Include pics with share
    public void shareWithOther(int position) {
        String displayName = currentUser.getString("displayName");
        String shareAuthor = data.get(position).getAuthorName();
        String shareDescription = data.get(position).getDescription();
        Bitmap sharedImage = data.get(position).getParseProfilePicture();

        //Share to Other
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, (displayName + " shared " + shareAuthor + "'s" + " Drop from \"Riple\":\n\n" + "\"" + shareDescription + "\"\n\n" + "If you have an Android device you can download \"Riple\" now and start making Riples of your own. Click the link to get started!\n" + "facebook.com/kevinhodges0"));
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//      Uri imageUri = Uri.parse("https://scontent-ord1-1.xx.fbcdn.net/hphotos-xap1/v/t1.0-9/923007_665600676799506_1701143490_n.png?oh=9a224427d5c5807ed0db56582363057b&oe=57079C4E");
//      shareIntent.setType("*/*");
//      shareIntent.putExtra(Intent.EXTRA_STREAM, String.valueOf(imageUri));
//      shareIntent.setType("image/*");
        mContext.startActivity(Intent.createChooser(shareIntent, "Share this Drop with friends"));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //DropViewHolder//////////////////////////////////////////////////////////////////////////////
    public class DropViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private final Button todoButton;
        private final Button completeButton;
        private ImageView menuButton;
        public TextView authorName;
        public TextView createdAt;
        public TextView description;
        public TextView ripleCount;
        public TextView commentCount;
        public TextView authorRank;
        private ImageView parseProfilePicture;
        private RelativeLayout topLayout;
        private Toolbar toolbar;

        public DropViewHolder(View itemView) {
            super(itemView);

            parseProfilePicture = (ImageView) itemView.findViewById(R.id.other_profile_picture);
            todoButton = (Button) itemView.findViewById(R.id.button_todo);
            completeButton = (Button) itemView.findViewById(R.id.button_complete);
            createdAt = (TextView) itemView.findViewById(R.id.comment_created_at);
            authorName = (TextView) itemView.findViewById(R.id.name);
            description = (TextView) itemView.findViewById(R.id.description);
            ripleCount = (TextView) itemView.findViewById(R.id.riple_count);
            commentCount = (TextView) itemView.findViewById(R.id.comment_count);
            authorRank = (TextView) itemView.findViewById(R.id.author_rank);
            menuButton = (ImageView) itemView.findViewById(R.id.menu_button);
            topLayout = (RelativeLayout) itemView.findViewById(R.id.top_layout);
            toolbar = (Toolbar) itemView.findViewById(R.id.trickle_card_tool_bar);

            //OnClickListeners for items
//            itemView.setOnClickListener(this);
//            itemView.setOnLongClickListener(this);
            topLayout.setOnClickListener(this);
            topLayout.setOnLongClickListener(this);
            toolbar.setOnClickListener(this);
            toolbar.setOnLongClickListener(this);
            parseProfilePicture.setOnClickListener(this);
            parseProfilePicture.setOnLongClickListener(this);

            //OnClickListeners for buttons
            if (todoButton != null) {
                todoButton.setOnClickListener(this);
            }
            if (completeButton != null) {
                completeButton.setOnClickListener(this);
            }
            if (menuButton != null) {
                menuButton.setOnClickListener(this);
            }
        }

        public void update(int position) {

            DropItem current = data.get(position);

            parseProfilePicture.setImageBitmap(current.parseProfilePicture);
            createdAt.setText(String.valueOf(current.createdAt));
            authorName.setText(current.authorName);
            description.setText(current.description);
            ripleCount.setText(String.valueOf(current.ripleCount));
            commentCount.setText(String.valueOf(current.commentCount));
            authorRank.setText(current.authorRank);
        }

        @Override
        public void onClick(View view) {
            if (view == todoButton) {
                //Check to see if user has picture and display name set
                ParseUser currentUser = ParseUser.getCurrentUser();
                parseProfilePictureCheck = (ParseFile) currentUser.get("parseProfilePicture");
                displayNameCheck = (String) currentUser.get("displayName");

                if (parseProfilePictureCheck == null && displayNameCheck == null) {
                    Toast.makeText(mContext, "Please upload a picture and set your User Name first, don't be shy :)", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(mContext, SettingsActivity.class);
                    mContext.startActivity(intent);
                } else if (parseProfilePicture == null) {
                    Toast.makeText(mContext, "Please upload a picture first, don't be shy :)", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(mContext, SettingsActivity.class);
                    mContext.startActivity(intent);

                } else if (displayNameCheck == null) {
                    Toast.makeText(mContext, "Please set your User Name first, don't be shy :)", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(mContext, SettingsActivity.class);
                    mContext.startActivity(intent);
                //Add Drop to to-do list if user has picture and display name set
                } else {
                    getTrickleObjectFromRowToAdd(getAdapterPosition());
                    removeDropFromView(getAdapterPosition());
                }
            //Complete the Drop
            } else if (view == completeButton) {
                getDropObjectFromRowToComplete(getAdapterPosition());
                removeDropFromView(getAdapterPosition());
            //Show menu context
            } else if (view == menuButton) {
                if (mTabName.equals(drop)) {
                    showDropMenu();
                } else {
                    showTrickleMenu();
                }
            //If click is anywhere on picture or toolbar, view the user
            } else if (view == parseProfilePicture || view == toolbar) {
                viewOtherUser(getAdapterPosition());
            } else {
                //otherwise, view the Drop
                viewDrop(getAdapterPosition());
            }
        }



        @Override
        public boolean onLongClick(View view) {
            Vibrator vb = (Vibrator) mContext.getSystemService(mContext.VIBRATOR_SERVICE);
            vb.vibrate(5);
            if (mTabName.equals(drop)) {
                showDropMenu();
            } else {
                showTrickleMenu();
            }
            return false;
        }

        public void showTrickleMenu() {

            CharSequence trickleDrop[] = new CharSequence[]{"Message the Author", "Share with Facebook", "Share", "Report"};

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
            builder.setTitle("Drop Menu");
            builder.setItems(trickleDrop, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int selected) {
                    if (selected == 0) {
                        messageTheAuthor(getAdapterPosition());
                    }else if (selected == 1) {
                        shareToFacebook(getAdapterPosition());

                    } else if (selected == 2) {
                        shareWithOther(getAdapterPosition());

                    }else if (selected == 3) {
                        final AlertDialog.Builder builderVerify = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
                        builderVerify.setTitle("Report Drop Author");
                        builderVerify.setMessage("Would you say this Drop contains spam or inappropriate/offensive material?");
                        builderVerify.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                        builderVerify.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reportDropAuthor(getAdapterPosition());
                                Toast.makeText(mContext, "The author has been reported. Thank you for keeping Riple safe!", Toast.LENGTH_LONG).show();
                            }
                        });
                        builderVerify.show();
                    }
                }
            });
            builder.show();
        }

        public void showDropMenu() {

            CharSequence todoDrop[] = new CharSequence[]{"Message the Author", "Share with Facebook", "Share", "Remove From Todo", "Report"};

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
            builder.setTitle("Drop Menu");
            builder.setItems(todoDrop, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int selected) {

                    if (selected == 0) {
                        messageTheAuthor(getAdapterPosition());

                    }else if (selected == 1) {
                        shareToFacebook(getAdapterPosition());

                    }else if (selected == 2) {
                        shareWithOther(getAdapterPosition());

                    } else if (selected == 3) {
                        getDropObjectFromRowToRemove(getAdapterPosition());
                        removeDropFromView(getAdapterPosition());

                    } else if (selected == 4) {
                        final AlertDialog.Builder builderVerify = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
                        builderVerify.setTitle("Report Drop Author");
                        builderVerify.setMessage("Would you say this Drop contains spam or inappropriate/offensive material?");
                        builderVerify.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                        builderVerify.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reportDropAuthor(getAdapterPosition());
                                Toast.makeText(mContext, "The author has been reported. Thank you for keeping Riple safe!", Toast.LENGTH_LONG).show();

                            }
                        });
                        builderVerify.show();

                    }
                }
            });
            builder.show();
        }
    }

    public void removeDropFromView(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }
}


