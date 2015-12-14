package com.khfire22gmail.riple.model;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by Kevin on 10/27/2015.
 */
public class CommentItem {


    public Date createdAt;
    public String dropId;
    public String objectId;
    public String commenterFacebookId;
    public String commenterName;
    public Bitmap parseProfilePicture;

    public Bitmap getParseProfilePicture() {
        return parseProfilePicture;
    }

    public void setParseProfilePicture(Bitmap parseProfilePicture) {
        this.parseProfilePicture = parseProfilePicture;
    }

    public String getCommenterId() {
        return commenterId;
    }

    public void setCommenterId(String commenterId) {
        this.commenterId = commenterId;
    }

    public String commenterId;
    public String commentText;

    public String getDropId() {
        return dropId;
    }

    public void setDropId(String dropId) {
        this.dropId = dropId;
    }

    public String getCommenterFacebookId() {
        return commenterFacebookId;
    }

    public void setCommenterFacebookId(String commenterFacebookId) {
        this.commenterFacebookId = commenterFacebookId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }


}
