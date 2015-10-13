package com.khfire22gmail.riple.model;

import java.util.Date;

/**
 * Created by Kevin on 9/16/2015.
 */
public class DropItem {

//    public ProfilePictureView profilePictureView;
    public String objectId;
    //    public String userName;
    public String author;
    public String facebookId;
    public String commenter;
    public Date createdAt;
    public String title;
    public String description;
    public String ripleCount;
    public String commentCount;


    /*public DropItem(ParseObject po) {
//        dropItem.setObjectId(list.get(i).getObjectId());
        objectId = po.getObjectId();
        author = po.getString("author");

// TODO Get authors author from String
        dropItem.setName(list.get(i).getString("author"));

// TODO Turn Unix time into a readable date (1444327164)
        dropItem.setCreatedAt(list.get(i).getDate("createdAt"));

        dropItem.setFacebookId(list.get(i).getFacebookId());

//                        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(oldstring);


        dropItem.setTitle(list.get(i).getString("title"));
        dropItem.setDescription(list.get(i).getString("description"));

        dropItem.setRipleCount(String.valueOf(list.get(i).getInt("ripleCount") + " Riples"));
        dropItem.setCommentCount(String.valueOf(list.get(i).getInt("commentCount") + " Comments"));
    }*/

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    /*public ProfilePictureView getUserProfilePictureView() {
        return profilePictureView;
    }

    public void setUserProfilePictureView(ProfilePictureView userProfilePictureView) {
        this.profilePictureView = userProfilePictureView;
    }*/

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

   /* public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
*/
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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