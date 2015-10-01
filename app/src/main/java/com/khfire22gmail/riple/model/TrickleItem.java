package com.khfire22gmail.riple.model;

import com.facebook.login.widget.ProfilePictureView;

/**
 * Created by Kevin on 9/16/2015.
 */
public class TrickleItem {

    public ProfilePictureView userProfilePictureView;
    public String trickleUserName;
    public String trickleTime;
    public String trickleTitle;
    public String trickleDescription;
    public int trickleRipleCount;
    public int trickleCommentCount;

    public ProfilePictureView getUserProfilePictureView() {

        return userProfilePictureView;
    }

    public void setUserProfilePictureView(ProfilePictureView userProfilePictureView) {
        this.userProfilePictureView = userProfilePictureView;
    }

    public String getUsernameView() {
        return trickleUserName;
    }

    public void setTrickleUserName(String trickleUserName) {
        this.trickleUserName = trickleUserName;
    }

    public String getTrickleTitle() {
        return trickleTitle;
    }

    public void setTrickleTitle(String trickleTitle) {
        this.trickleTitle = trickleTitle;
    }

    public String getTrickleDescription() {
        return trickleDescription;
    }

    public void setTrickleDescription(String trickleDescription) {
        this.trickleDescription = trickleDescription;
    }

    public String getTrickleTime() {
        return trickleTime;
    }

    public void setTrickleTime(String trickleTime) {
        this.trickleTime = trickleTime;
    }

    public int getTrickleRipleCount() {
        return trickleRipleCount;
    }

    public void setTrickleRipleCount(int trickleRipleCount) {
        this.trickleRipleCount = trickleRipleCount;
    }

    public int getTrickleCommentCount() {
        return trickleCommentCount;
    }

    public void setTrickleCommentCount(int trickleCommentCount) {
        this.trickleCommentCount = trickleCommentCount;
    }

}