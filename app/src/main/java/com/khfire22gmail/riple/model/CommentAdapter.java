package com.khfire22gmail.riple.model;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.khfire22gmail.riple.R;
import com.khfire22gmail.riple.activities.ViewUserActivity;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.Collections;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    Context mContext;

    //    private final String mTabName;
    private LayoutInflater inflater;
    List<CommentItem> data = Collections.emptyList();
    private ParseUser currentUser = ParseUser.getCurrentUser();

    public CommentAdapter(Context context, List<CommentItem> data) {

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

        /*viewHolder.commenterParseProfilePicture.setOnClickListener(new View.OnClickListener() {
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

    public void reportCommentAuthor(final int position) {

        final String commentObjectId = data.get(position).getCommentObjectId();

        //Get Drop data, which includes the Author pointer
        ParseQuery parseQuery = ParseQuery.getQuery("Comments");
        parseQuery.whereEqualTo("objectId", commentObjectId);
        parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject commentObject, ParseException e) {
                if (e == null) {
                    //Get author data from comment
                    ParseUser authorPointer = commentObject.getParseUser("commenterPointer");
//                    ParseUser reportedCommenter = commentObject.getParseUser("commenterPointer");

                    //Get author for report
                    ParseQuery reportQuery = ParseQuery.getQuery("UserReportCount");
                    reportQuery.whereEqualTo("userPointer", authorPointer);
                    reportQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(final ParseObject reportedUser, ParseException e) {
                            //Increment the author's report count and mark the comment
                            ParseRelation reportRelation = reportedUser.getRelation("reportedComments");
                            reportRelation.add(commentObject);
                            reportedUser.increment("reportCount");
                            reportedUser.saveEventually();
                        }
                    });
                }
            }
        });
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
        public ImageView menuButton;

        public CommentViewHolder(View itemView) {
            super(itemView);

            parseProfilePicture = (ImageView) itemView.findViewById(R.id.commenter_profile_picture);
            createdAt = (TextView) itemView.findViewById(R.id.comment_created_at);
            commenterName = (TextView) itemView.findViewById(R.id.commenter_name);
            commentText = (TextView) itemView.findViewById(R.id.comment_text);
            commenterRipleCount = (TextView) itemView.findViewById(R.id.commenter_rank);
            menuButton = (ImageView) itemView.findViewById(R.id.menu_button);


            //Listeners
            menuButton.setOnClickListener(this);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
//            commenterParseProfilePicture.setOnLongClickListener(this);
//            createdAt.setOnLongClickListener(this);
//            commenterName.setOnLongClickListener(this);
//            commentText.setOnLongClickListener(this);
//            commenterRipleCount.setOnLongClickListener(this);
//            menuButton.setOnLongClickListener(this);
        }

        public void update(int position) {

            CommentItem current = data.get(position);

            parseProfilePicture.setImageBitmap(current.commenterParseProfilePicture);
            commenterName.setText(current.commenterName);
            commentText.setText(current.commentText);
            createdAt.setText(String.valueOf(current.createdAt));
            commenterRipleCount.setText(String.valueOf(current.commenterRank));
        }

        @Override
        public void onClick(View view) {
            if (view == menuButton) {
                showCommentMenu();
            } else {
                viewOtherUser(getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            Vibrator vb = (Vibrator) mContext.getSystemService(mContext.VIBRATOR_SERVICE);
            vb.vibrate(5);
            showCommentMenu();
            return false;
        }

        public void showCommentMenu() {

            CharSequence todoDrop[] = new CharSequence[]{"Report"};

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
            builder.setTitle("Comment Menu");
            builder.setItems(todoDrop, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int selected) {
                    if (selected == 0) {
                        final AlertDialog.Builder builderVerify = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
                        builderVerify.setTitle("Report Comment Author");
                        builderVerify.setMessage("Does this author or comment contain inappropriate or offensive material?");
                        builderVerify.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                        builderVerify.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reportCommentAuthor(getAdapterPosition());
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

    public void addCommentToView(int position, CommentItem comment) {
        data.add(0, comment);
        notifyItemInserted(position);
    }
}



