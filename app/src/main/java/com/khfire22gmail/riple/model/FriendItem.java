package com.khfire22gmail.riple.model;

import android.graphics.Bitmap;

/**
 * Created by Kevin on 12/7/2015.
 */
public class FriendItem {

    public Bitmap friendProfilePicture;
    public String friendName;
    public String ripleRank;
    public String ripleCount;
    public String friendInfo;
    public String relationshipObjectId;
    public String friendObjectId;

    public String getFriendInfo() {
        return friendInfo;
    }

    public void setFriendInfo(String friendInfo) {
        this.friendInfo = friendInfo;
    }

    public String getFriendObjectId() {
        return friendObjectId;
    }

    public void setFriendObjectId(String friendObjectId) {
        this.friendObjectId = friendObjectId;
    }

    public String getRelationshipObjectId() {
        return relationshipObjectId;
    }

    public void setRelationshipObjectId(String relationshipObjectId) {
        this.relationshipObjectId = relationshipObjectId;
    }

    public String getRipleRank() {
        return ripleRank;
    }

    public void setRipleRank(String ripleRank) {
        this.ripleRank = ripleRank;
    }

    public String getRipleCount() {
        return ripleCount;
    }

    public void setRipleCount(String ripleCount) {
        this.ripleCount = ripleCount;
    }

    public String objectId;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Bitmap getFriendProfilePicture() {
        return friendProfilePicture;
    }

    public void setFriendProfilePicture(Bitmap friendProfilePicture) {
        this.friendProfilePicture = friendProfilePicture;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }
}
