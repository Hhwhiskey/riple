package com.khfire22gmail.riple.model;

import android.graphics.Bitmap;

/**
 * Created by Kevin on 12/7/2015.
 */
public class FriendItem {

    public Bitmap friendProfilePicture;
    public String friendName;
    public String lastMessage;
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

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
