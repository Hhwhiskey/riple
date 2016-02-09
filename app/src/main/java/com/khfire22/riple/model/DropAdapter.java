package com.khfire22.riple.model;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.khfire22.riple.R;
import com.khfire22.riple.activities.MessagingActivity;
import com.khfire22.riple.activities.SettingsActivity;
import com.khfire22.riple.activities.ViewDropActivity;
import com.khfire22.riple.activities.ViewUserActivity;
import com.khfire22.riple.fragments.DropsTabFragment;
import com.khfire22.riple.fragments.TrickleTabFragment;
import com.khfire22.riple.utils.ConnectionDetector;
import com.khfire22.riple.utils.Constants;
import com.khfire22.riple.utils.Vibrate;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Collections;
import java.util.List;


public class DropAdapter extends RecyclerView.Adapter<DropAdapter.DropViewHolder> {


    Context mContext;
    public String mTabName;
    private LayoutInflater inflater;
    public List<DropItem> data = Collections.emptyList();
    public static final String riple = "riple";
    public static final String drop = "drop";
    public static final String trickle = "trickle";
    public static final String viewUser = "viewUser";

    private ParseFile parseProfilePictureCheck;
    private String displayNameCheck;
    private ContentResolver contentResolver;
    private Bitmap storedImage;
    private boolean storeImage;
    private boolean stop;
    private Context applicationContext;
    private static final String TAG = "DropAdapter";
    private ConnectionDetector detector;
    private ParseUser mCurrentUser;
    public String mCurrentUserId;

    public DropAdapter() {
//        this.data =
    }

    public DropAdapter(Context context, List<DropItem> data, String tabName) {

        this.mContext = context;
        this.data = data;
        this.mTabName = tabName;
        detector = new ConnectionDetector(mContext);

        inflater = LayoutInflater.from(context);
    }

    @Override
    public DropViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mCurrentUser = ParseUser.getCurrentUser();

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

        //Modify mCurrentUser relations to Drop
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
        String dropObjectId = (data.get(position).getObjectId());
        String authorId = (data.get(position).getAuthorId());
        String authorName = (data.get(position).getAuthorName());
        String authorRank = (data.get(position).getAuthorRank());
        String authorRipleCount = (data.get(position).getAuthorRipleCount());
        String clickedUserInfo = (data.get(position).getAuthorInfo());
        String dropDescription = (data.get(position).getDescription());
        String ripleCount = (data.get(position).getRipleCount());
        String commentCount = (data.get(position).getCommentCount());
        String createdAt = (data.get(position).getCreatedAt());
        String userLastLocation = (data.get(position).getUserLastLocation());

        Log.d("sViewDropAcitivty", "Send drop's dropObjectId = " + dropObjectId);
        Log.d("sViewDropAcitivty", "Send drop's authorId = " + authorId);
        Log.d("sViewDropAcitivty", "Send drop's commenterName = " + authorName);
        Log.d("sViewDropAcitivty", "Send drop's dropDescription = " + dropDescription);
        Log.d("sViewDropAcitivty", "Send drop's ripleCount = " + ripleCount);
        Log.d("sViewDropAcitivty", "Send drop's commentCount = " + commentCount);
        Log.d("sViewDropAcitivty", "Send drop's createdAt = " + createdAt);

        Intent intent = new Intent(mContext, ViewDropActivity.class);
        intent.putExtra("dropObjectId", dropObjectId);
        intent.putExtra("authorId", authorId);
        intent.putExtra("authorRank", authorRank);
        intent.putExtra("clickedUserRipleCount", authorRipleCount);
        intent.putExtra("clickedUserInfo", clickedUserInfo);
        intent.putExtra("commenterName", authorName);
        intent.putExtra("dropDescription", dropDescription);
        intent.putExtra("ripleCount", ripleCount);
        intent.putExtra("commentCount", commentCount);
        intent.putExtra("createdAt", createdAt);
        intent.putExtra("mTabName", mTabName);
        intent.putExtra("userLastLocation", userLastLocation);

        mContext.startActivity(intent);
    }

    // onClick action for viewing other user
    private void viewOtherUser(int position) {

        String clickedUserId = (data.get(position).getAuthorId());
        String clickedUserName = (data.get(position).getAuthorName());
        String clickedUserRank = (data.get(position).getAuthorRank());
        String clickedUserRipleCount = (data.get(position).getAuthorRipleCount());
        String clickedUserInfo = (data.get(position).getAuthorInfo());

        Log.d(TAG, "Clicked User's Id = " + clickedUserId);
        Log.d(TAG, "Clicked User's Name = " + clickedUserName);
        Log.d(TAG, "Clicked User's RipleCount = " + clickedUserRipleCount);

        Intent intent = new Intent(mContext, ViewUserActivity.class);
        intent.putExtra(Constants.CLICKED_USER_ID, clickedUserId);
        intent.putExtra(Constants.CLICKED_USER_NAME, clickedUserName);
        intent.putExtra(Constants.CLICKED_USER_RANK, clickedUserRank);
        intent.putExtra(Constants.CLICKED_USER_RIPLE_COUNT, clickedUserRipleCount);
        intent.putExtra(Constants.CLICKED_USER_INFO, clickedUserInfo);


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
                            reportedUser.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    Toast.makeText(mContext, "The author has been reported. " +
                                                    "Thank you for keeping Riple safe!",
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    public void shareToFacebook(int position) {
        String displayName = mCurrentUser.getString("displayName");
        String shareAuthor = data.get(position).getAuthorName();
        String shareDescription = data.get(position).getDescription();
        Bitmap sharedImage = data.get(position).getParseProfilePicture();

        ShareDialog shareDialog;
        FacebookSdk.sdkInitialize(mContext);
        shareDialog = new ShareDialog((Activity) mContext);

        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle(displayName + " shared " + shareAuthor + "'s" + " Drop from \"Riple\".")
                .setContentDescription(shareDescription + " ***Get Riple from the PlayStore now!***")
                .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.khfire22.riple"))
                .setImageUrl(Uri.parse("https://scontent-ord1-1.xx.fbcdn.net/hphotos-xap1/v/t1.0-9/923007_665600676799506_1701143490_n.png?oh=9a224427d5c5807ed0db56582363057b&oe=57079C4E"))
                .build();

        shareDialog.show(linkContent);
    }

    // TODO: 11/19/2015 Include pics with share
    public void shareWithOther(int position) {
        String displayName = mCurrentUser.getString("displayName");
        String shareAuthor = data.get(position).getAuthorName();
        String shareDescription = data.get(position).getDescription();
        Bitmap sharedImage = data.get(position).getParseProfilePicture();

        //Share to Other
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, (displayName + " shared " + shareAuthor + "'s" + " Drop from \"Riple\":\n\n" + "\"" + shareDescription + "\"\n\n" + "If you have an Android device you can download \"Riple\" now and start making Riples of your own. Click the link to get started!\n" + "https://play.google.com/store/apps/details?id=com.khfire22.riple"));
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
        private final TextView userLastLocation;
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
            userLastLocation = (TextView) itemView.findViewById(R.id.user_last_location);
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
            createdAt.setText(current.createdAt);
            if (userLastLocation != null) {
                userLastLocation.setText(current.userLastLocation);
            }

            authorName.setText(current.authorName);
            description.setText(current.description);
            ripleCount.setText(String.valueOf(current.ripleCount));
            commentCount.setText(String.valueOf(current.commentCount));
            authorRank.setText(current.authorRank);
        }

        @Override
        public void onClick(View view) {

            ParseUser currentUser;
            int position = getAdapterPosition();
            mCurrentUserId = ParseUser.getCurrentUser().getObjectId();
            String authorId = data.get(position).getAuthorId();

            if (!detector.isConnectedToInternet()) {
                Toast.makeText(mContext, R.string.no_connection, Toast.LENGTH_LONG).show();
            } else {

                Intent intent = new Intent(mContext, SettingsActivity.class);

                if (view == todoButton) {
                    //Check to see if user has picture and display name set
                    currentUser = ParseUser.getCurrentUser();
                    parseProfilePictureCheck = (ParseFile) currentUser.get("parseProfilePicture");
                    displayNameCheck = (String) currentUser.get("displayName");

                    if (parseProfilePictureCheck == null && displayNameCheck == null) {
                        Toast.makeText(mContext, R.string.picAndNameToast, Toast.LENGTH_LONG).show();
                        mContext.startActivity(intent);

                    } else if (parseProfilePicture == null) {
                        Toast.makeText(mContext, R.string.picToast, Toast.LENGTH_LONG).show();
                        mContext.startActivity(intent);

                    } else if (displayNameCheck == null) {
                        Toast.makeText(mContext, R.string.nameToast, Toast.LENGTH_LONG).show();
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
                    if (!authorId.equals(mCurrentUserId)) {
                        switch (mTabName) {
                            case drop:
                                showDropMenu();
                                break;
                            case trickle:
                                showTrickleMenu();
                                break;
                            default:
                                showStandardMenu();
                                break;
                        }
                    } else {
                        showAuthorsDropMenu();
                    }
                    //If click is anywhere on picture or toolbar, view the user
                } else if (view == parseProfilePicture || view == toolbar) {
                    viewOtherUser(getAdapterPosition());
                } else {
                    //otherwise, view the Drop
                    viewDrop(getAdapterPosition());
                }
            }
        }


        @Override
        public boolean onLongClick(View view) {
            Vibrate vibrate = new Vibrate();
            vibrate.vibrate(mContext);

            int position = getAdapterPosition();
            mCurrentUserId = ParseUser.getCurrentUser().getObjectId();
            String authorId = data.get(position).getAuthorId();

            if (!detector.isConnectedToInternet()) {
                Toast.makeText(mContext, R.string.no_connection, Toast.LENGTH_LONG).show();
            } else {

                if (!authorId.equals(mCurrentUserId)) {
                    switch (mTabName) {
                        case drop:
                            showDropMenu();
                            break;
                        case trickle:
                            showTrickleMenu();
                            break;
                        default:
                            showStandardMenu();
                            break;
                    }
                } else {
                    showAuthorsDropMenu();
                }
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
                    } else if (selected == 1) {
                        shareToFacebook(getAdapterPosition());

                    } else if (selected == 2) {
                        shareWithOther(getAdapterPosition());

                    } else if (selected == 3) {
                        final AlertDialog.Builder builderVerify = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
                        builderVerify.setTitle("Report Drop Author");
                        builderVerify.setMessage("Does this user or Drop contain spam or inappropriate/offensive material?");
                        builderVerify.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                        builderVerify.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reportDropAuthor(getAdapterPosition());
                            }
                        });
                        builderVerify.show();
                    }
                }
            });
            builder.show();
        }

        public void showDropMenu() {

            CharSequence todoDrop[] = new CharSequence[]{"Remove from Todo", "Message the Author", "Share with Facebook", "Share", "Report"};

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
            builder.setTitle("Drop Menu");
            builder.setItems(todoDrop, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int selected) {

                    if (selected == 0) {
                        getDropObjectFromRowToRemove(getAdapterPosition());
                        removeDropFromView(getAdapterPosition());

                    } else if (selected == 1) {
                        messageTheAuthor(getAdapterPosition());

                    } else if (selected == 2) {
                        shareToFacebook(getAdapterPosition());

                    } else if (selected == 3) {
                        shareWithOther(getAdapterPosition());

                    } else if (selected == 4) {
                        final AlertDialog.Builder builderVerify = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
                        builderVerify.setTitle("Report Drop Author");
                        builderVerify.setMessage("Does this user or Drop contain spam or inappropriate/offensive material?");
                        builderVerify.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                        builderVerify.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reportDropAuthor(getAdapterPosition());
                            }
                        });
                        builderVerify.show();

                    }
                }
            });
            builder.show();
        }

        private void showAuthorsDropMenu() {

            CharSequence todoDrop[] = new CharSequence[]{"Share with Facebook", "Share"};

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
            builder.setTitle("Menu");
            builder.setItems(todoDrop, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int selected) {

                    if (selected == 0) {
                        shareToFacebook(getAdapterPosition());

                    }else if (selected == 1) {
                        shareWithOther(getAdapterPosition());
                    }
                }
            });
            builder.show();
        }

        private void showStandardMenu() {

            CharSequence todoDrop[] = new CharSequence[]{"Message the Author", "Share with Facebook", "Share", "Report"};

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
            builder.setTitle("Menu");
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


