package com.khfire22gmail.riple.model;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by Kevin on 10/27/2015.
 */
public class CommentItem {

    public String dropObjectId;
    public String commentObjectId;
    public String commentText;
    public Date createdAt;

    public String commenterId;
    public String commenterName;
    public String commenterRank;
    public String commenterInfo;
    public String commenterRipleCount;
    public Bitmap commenterParseProfilePicture;


    public String getCommenterInfo() {
        return commenterInfo;
    }

    public void setCommenterInfo(String commenterInfo) {
        this.commenterInfo = commenterInfo;
    }

    public String getCommenterRipleCount() {
        return commenterRipleCount;
    }

    public void setCommenterRipleCount(String commenterRipleCount) {
        this.commenterRipleCount = commenterRipleCount;
    }

    public String getCommentObjectId() {
        return commentObjectId;
    }

    public void setCommentObjectId(String commentObjectId) {
        this.commentObjectId = commentObjectId;
    }

    public String getCommenterRank() {
        return commenterRank;
    }

    public void setCommenterRank(String commenterRank) {
        this.commenterRank = commenterRank;
    }

    public Bitmap getCommenterParseProfilePicture() {
        return commenterParseProfilePicture;
    }

    public void setCommenterParseProfilePicture(Bitmap commenterParseProfilePicture) {
        this.commenterParseProfilePicture = commenterParseProfilePicture;
    }

    public String getCommenterId() {
        return commenterId;
    }

    public void setCommenterId(String commenterId) {
        this.commenterId = commenterId;
    }

    public String getDropObjectId() {
        return dropObjectId;
    }

    public void setDropObjectId(String dropObjectId) {
        this.dropObjectId = dropObjectId;
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
