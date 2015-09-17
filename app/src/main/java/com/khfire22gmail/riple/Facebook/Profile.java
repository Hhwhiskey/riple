package com.khfire22gmail.riple.Facebook;

import com.google.gson.annotations.SerializedName;
import com.sromku.simple.fb.entities.Image;
import com.sromku.simple.fb.entities.User;
import com.sromku.simple.fb.utils.Utils;

/**
 * Created by Kevin on 9/16/2015.
 */
public class Profile extends User{

    /**
     * Returns the ID of the user. <br>
     * <br>
     * <b> Permissions:</b><br>
     * {@link com.sromku.simple.fb.Permission#PUBLIC_PROFILE}
     *
     * @return the ID of the user
     */
    public String getId() {
        return super.getId();
    }

    /**
     * Returns the first name of the user. <br>
     * <br>
     * <b> Permissions:</b><br>
     * {@link com.sromku.simple.fb.Permission#PUBLIC_PROFILE}
     *
     * @return the first name of the user
     */
    public String getFirstName() {
        return mFirstName;
    }

    @SerializedName(com.sromku.simple.fb.entities.Profile.Properties.FIRST_NAME)
    private String mFirstName;

    /**
     * Returns the last name of the user. <br>
     * <br>
     * <b> Permissions:</b><br>
     * {@link com.sromku.simple.fb.Permission#PUBLIC_PROFILE}
     *
     * @return the last name of the user
     */
    public String getLastName() {
        return mLastName;
    }

    @SerializedName(com.sromku.simple.fb.entities.Profile.Properties.LAST_NAME)
    private String mLastName;

    /**
     * The user's profile pic <br>
     * <br>
     * <b> Permissions:</b><br>
     * {@link com.sromku.simple.fb.Permission#PUBLIC_PROFILE}
     *
     * @return The user's profile pic
     */
    public String getPicture() {
        if (mPicture == null || mPicture.data == null) {
            return null;
        }
        return mPicture.data.getUrl();
    }

    @SerializedName(com.sromku.simple.fb.entities.Profile.Properties.PICTURE)
    private Utils.SingleDataResult<Image> mPicture;
}
