package com.khfire22gmail.riple.facebook;

import android.util.Log;

import com.khfire22gmail.riple.MainActivity;
import com.sromku.simple.fb.entities.Comment;
import com.sromku.simple.fb.listeners.OnCommentsListener;

import java.util.List;

/**
 * Created by Kevin on 9/13/2015.
 */
public class Comments extends MainActivity {

    OnCommentsListener onCommentsListener = new OnCommentsListener() {
        @Override
        public void onComplete(List<Comment> comments) {
            Log.i("Kevin", "Number of comments = " + comments.size());
        }
        /*
     * You can override other methods here:
     * onThinking(), onFail(String reason), onException(Throwable throwable)
     */
    };

    /*Get comments of next entities by passing entity id:
    Album
    Checkin
    Comment
    Photo
    Post
    Video*/

    //One comment
    //mSimpleFacebook.getComment(commentId, onCommentsListener);

    //Multiple Comments
    //String entityId = ...;
    //mSimpleFacebook.getComments(entityId, onCommentsListener);

}

