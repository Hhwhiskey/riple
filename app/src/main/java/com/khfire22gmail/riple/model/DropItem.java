package com.khfire22gmail.riple.model;

import android.graphics.Bitmap;

import com.parse.ParseObject;

import java.util.Date;

/**
 * Created by Kevin on 9/16/2015.
 */
public class DropItem {

    public static final String DROP_KEY = "Drop";

    public Date createdAt;
    public String objectId;
    public String authorId;
    public String authorName;
    public String authorRank;
    public String authorRipleCount;
    public String description;
    public String comment;
    public String ripleCount;
    public String commentCount;
    public ParseObject drop;
    public Bitmap parseProfilePicture;


    public String getAuthorRipleCount() {
        return authorRipleCount;
    }

    public void setAuthorRipleCount(String authorRipleCount) {
        this.authorRipleCount = authorRipleCount;
    }

    public String getAuthorRank() {
        return authorRank;
    }

    public void setAuthorRank(String authorRank) {
        this.authorRank = authorRank;
    }

    public Bitmap getParseProfilePicture() {
        return parseProfilePicture;
    }

    public void setParseProfilePicture(Bitmap parseProfilePicture) {
        this.parseProfilePicture = parseProfilePicture;
    }

    public ParseObject getDrop() {
        return drop;
    }

    public void setDrop(ParseObject drop) {
        this.drop = drop;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRipleCount() {
        return ripleCount;
    }

    public void setRipleCount(String ripleCount) {
        this.ripleCount = ripleCount;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

}