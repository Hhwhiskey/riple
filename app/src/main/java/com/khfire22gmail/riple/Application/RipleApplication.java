package com.khfire22gmail.riple.application;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.khfire22gmail.riple.R;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import io.fabric.sdk.android.Fabric;


/**
 * Created by Kevin on 9/7/2015.
 */
public class RipleApplication extends Application {

    public static final String TAG = "Kevin";

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        FacebookSdk.sdkInitialize(getApplicationContext());

        Parse.enableLocalDatastore(this);

        // Initializes parse in application
        Parse.initialize(this, getString(R.string.parse_id), getString(R.string.parse_client_key));

        // Initializes FB Parse
        ParseFacebookUtils.initialize(this);

        //Saves current parse instance in the background
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    public static void updateParseInstallation() {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();

        installation.put("userObjectId", ParseUser.getCurrentUser().getObjectId());
        installation.saveInBackground();
        ParseUser.enableRevocableSessionInBackground();
    }
}