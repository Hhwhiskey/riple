package com.khfire22gmail.riple.model;

import android.widget.ImageView;

/**
 * Created by Kevin on 9/30/2015.
 */
public class RipleItem {

    public ImageView trickleProfilePic;
    public String trickleUserName;
    public String trickleTitle;
    public String trickleDescription;
    public String trickleTime;
    public int trickleRipleCount;
    public int trickleCommentCount;

    public ImageView getTrickleProfilePic() {
        return trickleProfilePic;
    }

    public void setTrickleProfilePic(ImageView trickleProfilePic) {
        this.trickleProfilePic = trickleProfilePic;
    }

    public String getTrickleUserName() {
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
