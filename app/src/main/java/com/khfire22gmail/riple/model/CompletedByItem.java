package com.khfire22gmail.riple.model;

import android.graphics.Bitmap;

/**
 * Created by Kevin on 12/13/2015.
 */
public class CompletedByItem {

    public String userObjectId;
    public String dropObjectId;
    public String displayName;
    public String userInfo;
    public Bitmap parseProfilePicture;
    public String userRipleCount;
    public String userRank;

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    public String getUserRipleCount() {
        return userRipleCount;
    }

    public void setUserRipleCount(String userRipleCount) {
        this.userRipleCount = userRipleCount;
    }

    public String getUserRank() {
        return userRank;
    }

    public void setUserRank(String userRank) {
        this.userRank = userRank;
    }

    public String getUserObjectId() {
        return userObjectId;
    }

    public void setUserObjectId(String userObjectId) {
        this.userObjectId = userObjectId;
    }

    public String getDropObjectId() {
        return dropObjectId;
    }

    public void setDropObjectId(String dropObjectId) {
        this.dropObjectId = dropObjectId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Bitmap getParseProfilePicture() {
        return parseProfilePicture;
    }

    public void setParseProfilePicture(Bitmap parseProfilePicture) {
        this.parseProfilePicture = parseProfilePicture;
    }
}
