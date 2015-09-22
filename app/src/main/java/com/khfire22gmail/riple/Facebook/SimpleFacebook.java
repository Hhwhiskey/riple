package com.khfire22gmail.riple.Facebook;

import com.sromku.simple.fb.SessionManager;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.actions.GetPhotosAction;
import com.sromku.simple.fb.listeners.OnPhotosListener;

/**
 * Created by Kevin on 9/19/2015.
 */
public class SimpleFacebook {

    private static SimpleFacebook mInstance = null;
    private static SimpleFacebookConfiguration mConfiguration = new SimpleFacebookConfiguration.Builder().build();

    private static SessionManager mSessionManager = null;

    private SimpleFacebook() {
    }

    public void getPhotos (String Profile, OnPhotosListener onPhotosListener){
        GetPhotosAction getPhotosAction = new GetPhotosAction(mSessionManager);
        getPhotosAction.setActionListener(onPhotosListener);
        getPhotosAction.setTarget(Profile);
        getPhotosAction.execute();
    }

}
