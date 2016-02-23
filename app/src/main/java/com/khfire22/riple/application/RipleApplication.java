package com.khfire22.riple.application;

import android.app.Application;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.khfire22.riple.R;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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

        ParseUser.enableRevocableSessionInBackground();

        ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.e("com.parse.push", "done" + e);
            }
        });

        // Sub currentUser to Push channels
        ParsePush.subscribeInBackground("messages");

        updateParseInstallation();
    }

    // Update the current user installation, which will label it on parse
    public static void updateParseInstallation() {

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();

        if (installation != null) {

            ParseUser currentUser = ParseUser.getCurrentUser();

            if (currentUser != null) {

                String userObjectId = ParseUser.getCurrentUser().getObjectId();
                String displayName = ParseUser.getCurrentUser().getString("displayName");

                if (displayName == null) {
                    displayName = "newUser";
                }

                installation.put("userObjectId", userObjectId);
                installation.put("displayName", displayName);
                installation.saveInBackground();
            }
        }
    }
}