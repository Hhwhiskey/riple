package com.khfire22gmail.riple.model;

import java.util.Date;

/**
 * Created by Kevin on 10/27/2015.
 */
public class CommentItem {


    public Date createdAt;
    public String objectId;
    public String commentersID;
    public String authorName;
    public String authorId;
    public String commenter;
    public String facebookId;
    public String comment;

    public String getCommentersID() {
        return commentersID;
    }

    public void setCommentersID(String commentersID) {
        this.commentersID = commentersID;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }


    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getCommenter() {
        return commenter;
    }

    public void setCommenter(String commenter) {
        this.commenter = commenter;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


}
